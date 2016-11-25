package com.xinthe.spax.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.xinthe.spax.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Koti
 */
public class Utils {
    public static boolean isUploading = false;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static boolean isIdfaAvailable = false;

    /**
     * @param str
     * @return
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * @param con
     * @return
     */
    public static String getWifiSSID(Context con) {
        WifiManager wifiManager = (WifiManager) con
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID().replace("\"", "").trim();
    }

    /**
     * @param context
     * @return
     */
    public static boolean checkWifiState(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled())
            return true;
        else
            return false;
    }

    /**
     * @param context
     */
    public static void enableWifi(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    /**
     * @param context
     */
    public static void disableWifi(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);
    }

    /**
     * enableBluetooth
     */
    public static void enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }

    /**
     * disableBluetooth
     */
    public static void disableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
    }

    /**
     * @param context
     * @return
     */
    public static int verifyBluetooth(Context context) {
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return -1;
        } else {
            if (((BluetoothManager) context
                    .getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter()
                    .isEnabled())
                return 1;

        }
        return 0;
    }

    /**
     * @param time
     * @return
     */
    public static String getDateTime(long time) {
        Timestamp timestamp = new Timestamp(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd' 'HH:mm:ss");
        // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(timestamp);

    }

    /**
     * @param time
     * @return
     */
    public static String getDateTimeUTC(long time) {
        Timestamp timestamp = new Timestamp(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd' 'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(timestamp);

    }




    /**
     * @param time
     * @return
     */
    public static long getTimestampUTC(long time) {
        Timestamp timestamp = new Timestamp(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd' 'HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = sdf.parse(simpleDateFormat.format(timestamp));
            return date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * @param time
     * @return
     */
    public static int getMilliSec(long time) {
        Timestamp timestamp = new Timestamp(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("S");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(simpleDateFormat.format(timestamp));
    }

    /**
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * @param txPower
     * @param rssi
     * @return
     */
    public static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    /**
     * @param distance
     * @return
     */
    public static int findProximity(double distance) {
        if (distance <= 0.5D)
            return 1;
        else if (distance <= 3.0D)
            return 2;
        else if (distance > 3.0D)
            return 3;
        else
            return 0;
    }

    /**
     * @param context
     * @param title
     * @param desc
     * @param mOpt
     */
    /*
    public static void openDialog(Context context, String title, String desc,
                                  boolean mOpt) {
        Intent trIntent = new Intent("android.intent.action.MAIN");
        trIntent.setClass(context,
                com.xinthe.span.ble.activity.DialogActivity.class);
        trIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        trIntent.putExtra("title", title);
        trIntent.putExtra("desc", desc);
        trIntent.putExtra("mOpt", mOpt);
        context.startActivity(trIntent);
    }
*/
    /**
     * @param con
     * @param title
     * @param msg
     * @param closeScreen
     */
    public static void showAlertSingleOpt(final Activity con, String title,
                                          final String msg, final boolean closeScreen) {
        new AlertDialog.Builder(con)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                if (closeScreen)
                                    con.finish();

                            }
                        }).show();
    }

    /**
     * @param con
     * @param title
     * @param msg
     */
    public static void showAlertMultiOpt(final Activity con,
                                         final String title, final String msg) {
        new AlertDialog.Builder(con)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                con.finish();
                                if (msg.contains(con
                                        .getString(R.string.please_enable_bluetooth)))
                                    enableBluetooth();

                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                con.finish();
                            }
                        }).show();
    }

    /**
     * @param beacon
     * @return
     */
    public static double computeAccuracy(Beacon beacon) {
        if (beacon.getRssi() == 0) {
            return -1.0D;
        }

        double ratio = beacon.getRssi() / beacon.getMeasuredPower();
        double rssiCorrection = 0.96D + Math.pow(Math.abs(beacon.getRssi()),
                3.0D) % 10.0D / 150.0D;

        if (ratio <= 1.0D) {
            return Math.pow(ratio, 9.98D) * rssiCorrection;
        }
        return (0.103D + 0.89978D * Math.pow(ratio, 7.71D)) * rssiCorrection;
    }

    /**
     * @param con
     * @return
     */
    public static boolean isNetworkOnline(Activity con) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            Log.e("isNetworkOnline", "No");
            return false;
        }
        return status;

    }

    /**
     * @param con
     * @param status
     */
    public static void setNetworkAvailabilityStatus(Context con, boolean status) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(con);
        Editor mEditor = mPrefs.edit();
        mEditor.putBoolean("network", status);
        mEditor.commit();
    }

    /**
     * @param con
     * @return
     */
    public static boolean getNetworkAvailabilityStatus(Context con) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(con);
        return mPrefs.getBoolean("network", true);
    }

    public static void showNetworkAlert(final Activity con, String title,
                                        final String msg, boolean isCancelable) {
        new AlertDialog.Builder(con)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(isCancelable)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public static void saveVisitorInfo(Context context, String emailID, String visitorID) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Constants.EMAIL_ID, emailID);
        editor.putString(Constants.VISITOR_ID, visitorID);
        editor.commit();
    }

    public static void saveFirebaseToken(Context context, String token) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public static String getFirebaseToken(Context context) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return mPrefs.getString("token", null);
    }

    public static boolean hasVisitorInfo(Context context) {
        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return mPrefs.contains(Constants.VISITOR_ID) && (mPrefs.getString(Constants.VISITOR_ID, "").length() > 0);
    }

    public static void showLongToast(Context context, String message) {
        if (context == null)
            return;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getDeviceId(Activity con) {
        TelephonyManager telephonyManager = (TelephonyManager) con
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf
                        .getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
//						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = (addr instanceof Inet4Address) ? true : false;
                        if (useIPv4) {

                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0,
                                        delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null)
                    return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";

    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
