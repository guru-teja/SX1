package com.xinthe.spax;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.amazonaws.regions.Regions;
import com.estimote.sdk.Region;
import com.xinthe.spax.services.BeaconScanner;
import com.xinthe.spax.services.BeaconScannerSettings;
import com.xinthe.spax.utils.Constants;
import com.xinthe.spax.utils.IDFA;
import com.xinthe.spax.utils.Utils;
import com.xinthe.spax.utils.Webservices;

import java.util.List;

public class BeaconCollector {

    Intent beaconScanner;
    LocationServerConfiguration config;
    Context parentContext;
    BeaconScannerSettings beaconScannerSettings;
    IDFA idfa;

    public static final LocationServerConfiguration SNSconfig =
            new LocationServerConfiguration(LocationServerConfiguration.ServerTypes.SNS, Regions.US_WEST_2, Constants.ARN_SNS_BEACONS, Constants.ACCESS_ID, Constants.SECRET_KEY);
    public static final LocationServerConfiguration SQSconfig =
            new LocationServerConfiguration(LocationServerConfiguration.ServerTypes.SQS, Regions.US_WEST_2, Constants.SQS_URL, Constants.ACCESS_ID, Constants.SECRET_KEY);

    public interface InitializationCallBack
    {
        public void onInitialized();
    }

    public BeaconCollector(Context context, String beaconUUID,LocationServerConfiguration locationServerConfig , int aggrigateType,
                           boolean computeLocation, int locationAlgorithm)
    {
        this.parentContext = context;

        //start fetching IDFA
        idfa = new IDFA(context);
        idfa.execute();

        this.config = locationServerConfig;
        beaconScannerSettings =
                new BeaconScannerSettings(beaconUUID,locationServerConfig ,
                        aggrigateType, computeLocation, locationAlgorithm );
        startService();
    }

    /**
     * Start beacons scanning service running in background.
     */
    private void startService() {
        beaconScanner = new Intent(parentContext, BeaconScanner.class);
        beaconScanner.putExtra(getString(R.string.scansettings),this.beaconScannerSettings);
        beaconScanner.putExtra(getString(R.string.start),
                Constants.START_SERVICE);
        parentContext.startService(beaconScanner);
    }

    /**
     * Stops beacons scanning service running in background.
     */
    private void stopService() {
        Utils.isUploading = false;
        Intent intent = new Intent(getString(R.string.service_stop));
        intent.putExtra(getString(R.string.start), Constants.STOP_SERVICE);
        LocalBroadcastManager.getInstance(parentContext).sendBroadcast(intent);
    }

    public BeaconCollector(Context context, String beaconUUID,LocationServerConfiguration locationServerConfig)
    {
        this(context, beaconUUID, locationServerConfig, Constants.AGGREGATION_B, false, -1);
    }

    private String getString (int resId)
    {
        //Context appContext = beaconScanner.getApplicationContext();
        return parentContext.getString(resId);
    }

    /**
     * Start Uploading data to Server
     */
    public void startUpload() {
        Utils.isUploading = true;
        Intent intent = new Intent(getString(R.string.service_stop));
        intent.putExtra(getString(R.string.start), Constants.START_UPLOAD);
        //        intent.putExtra(getString(R.string.aggregation), );
        LocalBroadcastManager.getInstance(parentContext).sendBroadcast(intent);
    }

    /**
     * Stop uploading data to Server
     */
    public void stopUpload() {
        Utils.isUploading = false;
        Intent intent = new Intent(getString(R.string.service_stop));
        intent.putExtra(getString(R.string.start), Constants.STOP_UPLOAD);
        LocalBroadcastManager.getInstance(parentContext).sendBroadcast(intent);
    }
}

