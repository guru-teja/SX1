package com.xinthe.spax.services;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xinthe.spax.BeaconCollector;
import com.xinthe.spax.LocationServerConfiguration;
import com.xinthe.spax.R;
import com.xinthe.spax.data.BeaconScanData;
import com.xinthe.spax.data.BeaconsCollectedData;
import com.xinthe.spax.data.WindowCollectedData;
import com.xinthe.spax.db.CollectedData;
import com.xinthe.spax.db.DatabaseHandler;
import com.xinthe.spax.utils.Constants;
import com.xinthe.spax.utils.IDFA;
import com.xinthe.spax.utils.Utils;
import com.xinthe.spax.utils.Webservices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Koti
 *         Background Service to fetch all beacons near to device.
 */
public class BeaconScanner extends IntentService implements
        AsyncServiceListener {
    private BluetoothAdapter mBluetoothAdapter;
    private BeaconManager beaconManager;
    private Handler uploadHandler;
    private Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",
            UUID.fromString(Constants.XINTHE_UUID), null, null);
    ArrayList<BeaconScanData> dumpBeaconsList = new ArrayList<BeaconScanData>();
    public int count = 0;
    private WindowCollectedData windowCollectedData;
    private DatabaseHandler dbHandler;
    AsyncService asyncService;

    private int aggregationType;
    boolean computeLocation = false;
    LocationServerConfiguration locationServerConfig;
    int locationAlgorithmType;
    BeaconCollector.InitializationCallBack callBack;

    public BeaconScanner() {
        super("Beacon Scanner");
    }

    public void intialize(BeaconScannerSettings settings )
    {
        this.aggregationType = settings.aggregationType;
        ALL_ESTIMOTE_BEACONS_REGION = new Region("rid",
                UUID.fromString(settings.beaconUUID), null, null);

        this.locationServerConfig = settings.locationServerConfig;

        this.computeLocation = settings.computeLocation;
        this.locationAlgorithmType = settings.locationAlgorithmType;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(getString(R.string.service_stop)));
        count = 0;
        dbHandler = new DatabaseHandler(this);
        startScan();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BeaconScannerSettings settings = (BeaconScannerSettings )intent.getSerializableExtra(getString(R.string.scansettings));
        intialize(settings);
        asyncService = new AsyncService(this, this, locationServerConfig);
        return START_STICKY;
    }
    /**
     * startScan
     * 0 - success
     * 1 - bluetooth not enabled
     * 2 - other errors
     */
    private int startScan() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
//            Utils.openDialog(this, getString(R.string.bluetooth_not_enabled),
//                    getString(R.string.please_enable_bluetooth), true);
            return 1;
        } else {
            uploadHandler = new Handler();
            windowCollectedData = new WindowCollectedData(this);
            scanLeDevice();

        }
        return 0;
    }

    @Override
    public void onResponseSuccess(int requestType, CollectedData data) {
        Log.e("estimote app success", data.getTimestamp());
        List<CollectedData> collectedList = dbHandler.getAllCollectedData();
        if (collectedList.size() == 0)
            sendServiceStatus(true);
    }

    @Override
    public void onResponseError(int errorCode, CollectedData data) {
        sendServiceStatus(false);
        Log.e("estimote app error", errorCode + "," + data.getTimestamp());
        if (data.getNoOfAttempts() < 5) {
            data.setErrorCode(errorCode);
            data.setNoOfAttempts(data.getNoOfAttempts() + 1);
            dbHandler.addCollectedData(data);
        }
    }

    public void onResponseError(int requestType, List<CollectedData> data) {
        sendServiceStatus(false);

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getNoOfAttempts() < 5) {
                data.get(i).setNoOfAttempts(data.get(i).getNoOfAttempts() + 1);
                dbHandler.addCollectedData(data.get(i));
            }
        }
    }

    /**
     * @param status
     */
    public void sendServiceStatus(boolean status) {
        Intent conIntent = new Intent(getString(R.string.service_changes));
        conIntent.putExtra("service", status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(conIntent);
    }

    /**
     * Scan for beacons
     */
    private void scanLeDevice() {
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 1);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region,
                                            final List<Beacon> beacons) {
                ArrayList<BeaconScanData> beaconsList = new ArrayList<BeaconScanData>();
                long timestamp = System.currentTimeMillis();
                for (int i = 0; i < beacons.size(); i++) {
                    BeaconScanData beacon = new BeaconScanData();
                    beacon.proximityUUID = beacons.get(i).getProximityUUID().toString();
                    beacon.bssid = beacons.get(i).getMacAddress().toStandardString();
                    beacon.timestamp = Utils.getTimestampUTC(timestamp);
                    beacon.rssi = beacons.get(i).getRssi();
                    beacon.major = beacons.get(i).getMajor();
                    beacon.minor = beacons.get(i).getMinor();
                    beacon.accuracy = com.estimote.sdk.Utils
                            .computeAccuracy(beacons.get(i));
                    beacon.proximity = Utils.findProximity(beacon.accuracy);
                    beacon.txPower = beacons.get(i).getMeasuredPower();
                    beacon.microseconds = Utils.getMilliSec(timestamp);
                    beaconsList.add(beacon);
                }
                if (Utils.isUploading) {
                    dumpBeaconsList.addAll(beaconsList);
                    count++;
                    if (count == 5) {
                        count = 0;
                        uploadHandler.post(uploadRun);
                    }
                }
                sendMessage(beaconsList);
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            }
        });

    }

    /**
     * @param beacon
     */
    private void sendMessage(ArrayList<BeaconScanData> beacon) {
        Intent intent = new Intent(getString(R.string.beacondata));
        intent.putExtra(getString(R.string.beacon), beacon);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * @param beacons
     */
    public void uploadData(ArrayList<BeaconScanData> beacons) {
        long bundleTime = System.currentTimeMillis();
        if (IDFA.isIDFAAvailable())
            windowCollectedData.idfa = IDFA.idfa;
        windowCollectedData.timestamp = Utils.getDateTime(bundleTime);

        if (aggregationType == Constants.AGGREGATION_W)
            beacons = getAggregateWBeacon(beacons);
        HashMap<String, ArrayList<BeaconScanData>> beaconsByBSSID = arrangeBeaconsScanDataByBSSID(beacons);
        ArrayList<BeaconsCollectedData> beaconsPacketsList = getBeaconsPacketsAsList(beaconsByBSSID);
        windowCollectedData.beacons = beaconsPacketsList;
        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls()
                .setPrettyPrinting().create();
        JsonElement element = gson.toJsonTree(windowCollectedData,
                new TypeToken<WindowCollectedData>() {
                }.getType());

        JsonObject jsonobj = element.getAsJsonObject();
        //Log.e("JSON", jsonobj.toString());

        CollectedData collectedData = new CollectedData(1, jsonobj.toString()
                .getBytes());
        collectedData.setTimeStamp(windowCollectedData.timestamp);
        if (locationServerConfig.ServerType == LocationServerConfiguration.ServerTypes.FILESYSTEM) {
            sendDataToFileSystem(collectedData, bundleTime);
        } else {
            if (Utils.getNetworkAvailabilityStatus(this)) {

                List<CollectedData> collectedList = dbHandler.getAllCollectedData();
                collectedList.add(collectedData);
                for (int i = 0; i < collectedList.size(); i++) {
                    dbHandler.deleteCollectedData(collectedList.get(i).getId());
                    //Log.e("beacons app ",collectedList.get(i).getTimestamp());
                    asyncService.saveBeaconData(collectedList.get(i));
                }
            } else {
                sendServiceStatus(false);
                //	Log.e("Fail", "Network");

                List<CollectedData> collectedList = dbHandler.getAllCollectedData();
                for (int i = 0; i < collectedList.size(); i++) {
                    CollectedData cData = collectedList.get(i);
                    if (cData.getNoOfAttempts() >= 5) {
                        dbHandler.deleteCollectedData(cData.getId());
                    } else {
                        dbHandler.updateCollectedDataAttempts(cData);
                    }
                }
                dbHandler.addCollectedData(collectedData);
                sendServiceStatus(false);
            }
        }
    }

    Runnable uploadRun = new Runnable() {
        @Override
        public void run() {
            ArrayList<BeaconScanData> ubeacons = new ArrayList<BeaconScanData>(
                    dumpBeaconsList);
            dumpBeaconsList.clear();
            uploadData(ubeacons);
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getIntExtra(getString(R.string.start), -1) == Constants.STOP_SERVICE) {
                if (beaconManager != null)
                    beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
                LocalBroadcastManager.getInstance(context)
                        .unregisterReceiver(mReceiver);
            } else if (intent.getIntExtra(getString(R.string.start), -1) == Constants.START_UPLOAD) {
                aggregationType = aggregationType;
            }
            //aggregationType = intent.getIntExtra(
                  //      getString(R.string.aggregation), 1);

            // else if (intent.getIntExtra(getString(R.string.start), -1) ==
            // Constants.STOP_UPLOAD) {
            // times += " To "
            // + Utils.getDateTime(System.currentTimeMillis());
            // Utils.openDialog(BeaconScanner.this,
            // getString(R.string.app_name), times, false);
            // }
        }
    };

    /**
     * @param collectedData
     * @param bundleTime
     * @return
     */
    private int sendDataToFileSystem(CollectedData collectedData,
                                     long bundleTime) {
        List<CollectedData> collectedList = null;
        try {
            String dirPath = Environment.getExternalStorageDirectory()
                    .toString() + "/" + getString(R.string.app_name) + "/";
            File dirFile = new File(dirPath);
            if (!dirFile.exists())
                dirFile.mkdir();
            String mPath = dirPath + bundleTime + "-"
                    + System.currentTimeMillis() + ".json";
            File jsonFile = new File(mPath);
            jsonFile.createNewFile();
            FileWriter out = new FileWriter(jsonFile, true);
            collectedList = dbHandler.getAllCollectedData();
            collectedList.add(collectedData);
            for (int i = 0; i < collectedList.size(); i++) {
                out.append(new String(collectedList.get(i).getData(), "UTF-8"));
                dbHandler.deleteCollectedData(collectedList.get(i).getId());
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (collectedList != null)
                onResponseError(0, collectedList);
            return 0;
        }
        onResponseSuccess(200, collectedData);
        return 200;
    }

    /**
     * @param beacons
     * @return
     */
    private HashMap<String, ArrayList<BeaconScanData>> arrangeBeaconsScanDataByBSSID(
            ArrayList<BeaconScanData> beacons) {
        HashMap<String, ArrayList<BeaconScanData>> beaconsMap = new HashMap<String, ArrayList<BeaconScanData>>();
        for (int i = 0; i < beacons.size(); i++) {
            if (beaconsMap.containsKey(beacons.get(i).bssid)) {
                ArrayList<BeaconScanData> beaconList = beaconsMap.get(beacons
                        .get(i).bssid);
                beaconList.add(beacons.get(i));
                beaconsMap.put(beacons.get(i).bssid, beaconList);
            } else {
                ArrayList<BeaconScanData> beaconList = new ArrayList<BeaconScanData>();
                beaconList.add(beacons.get(i));
                beaconsMap.put(beacons.get(i).bssid, beaconList);
            }
        }
        return beaconsMap;
    }

    /**
     * @param beaconsByBSSID
     * @return
     */
    private ArrayList<BeaconsCollectedData> getBeaconsPacketsAsList(
            HashMap<String, ArrayList<BeaconScanData>> beaconsByBSSID) {
        ArrayList<BeaconsCollectedData> beaconPacketsList = new ArrayList<BeaconsCollectedData>();
        Iterator<Entry<String, ArrayList<BeaconScanData>>> it = beaconsByBSSID
                .entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Entry pair = (Entry) it.next();
            @SuppressWarnings("unchecked")
            ArrayList<BeaconScanData> beaconList = (ArrayList<BeaconScanData>) pair
                    .getValue();
            BeaconsCollectedData beacon = new BeaconsCollectedData();
            beacon.bssid = beaconList.get(0).bssid;
            beacon.proximityUUID = beaconList.get(0).proximityUUID;
            beacon.major = beaconList.get(0).major;
            beacon.minor = beaconList.get(0).minor;
            beacon.txPower = beaconList.get(0).txPower;
            if (aggregationType == Constants.RAW
                    || aggregationType == Constants.AGGREGATION_W) {
                for (int i = 0; i < beaconList.size(); i++)
                    beacon.packets.add(getRawBeaconsData(beaconList.get(i)));

            } else if (aggregationType == Constants.AGGREGATION_B)
                beacon.packets.add(getAggregateBBeacons(beaconList));

            beaconPacketsList.add(beacon);
        }
        return beaconPacketsList;
    }

    /**
     * @param beacons
     * @return
     */
    private ArrayList<BeaconScanData> getAggregateWBeacon(
            ArrayList<BeaconScanData> beacons) {
        ArrayList<BeaconScanData> beaconsTemp;
        int pos = Math.round((beacons.size() - 1) / 2);
        beaconsTemp = new ArrayList<BeaconScanData>();
        beaconsTemp.add(beacons.get(pos));
        beacons.clear();
        beacons = beaconsTemp;
        return beacons;
    }

    /**
     * @param beaconList
     * @return
     */
    private ArrayList<Object> getAggregateBBeacons(
            ArrayList<BeaconScanData> beaconList) {
        int pos = Math.round((beaconList.size() - 1) / 2);
        ArrayList<Object> objList = new ArrayList<Object>();
        if (beaconList.get(pos).proximity == 0
                || beaconList.get(pos).accuracy < 0
                || beaconList.get(pos).rssi == -1) {
            beaconList.get(pos).rssi = -123;
            beaconList.get(pos).accuracy = 31.0;
            beaconList.get(pos).proximity = 0;
        }
        objList.add(beaconList.get(pos).rssi);
        objList.add(beaconList.get(pos).accuracy);
        objList.add(beaconList.get(pos).proximity);
        objList.add(Utils.getDateTime(beaconList.get(pos).timestamp));
        objList.add(beaconList.get(pos).microseconds);
        return objList;
    }

    /**
     * @param beacon
     * @return
     */
    private ArrayList<Object> getRawBeaconsData(BeaconScanData beacon) {
        ArrayList<Object> objList = new ArrayList<Object>();
        if (beacon.proximity == 0 || beacon.accuracy < 0 || beacon.rssi == -1) {
            beacon.rssi = -123;
            beacon.accuracy = 31.0;
            beacon.proximity = 0;
        }
        objList.add(beacon.rssi);
        objList.add(beacon.accuracy);
        objList.add(beacon.proximity);
        objList.add(Utils.getDateTime(beacon.timestamp));
        objList.add(beacon.microseconds);
        return objList;
    }
}
