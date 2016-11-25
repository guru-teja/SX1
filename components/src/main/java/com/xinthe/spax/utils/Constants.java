package com.xinthe.spax.utils;

/**
 * @author Koti
 */
public class Constants {
    public static int START_SERVICE = 1;
    public static int STOP_SERVICE = 2;
    public static int START_UPLOAD = 3;
    public static int STOP_UPLOAD = 4;
    public static int RAW = 1;
    public static int AGGREGATION_B = 2;
    public static int AGGREGATION_W = 3;

    public static String XINTHE_UUID = "f8c62883-3408-cb7e-7175-430326af8bd0";

    public static String SQS_URL = "https://sqs.us-west-2.amazonaws.com/829558341394/Beacons_Median";
    public static String ARN_SNS_BEACONS = "arn:aws:sns:us-west-2:829558341394:BEACONS_MEDIAN";
    public static String ACCESS_ID = "AKIAJRPN6ULKQXBZ3NWA";
    public static String SECRET_KEY = "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";


    public static final int REQUEST_GPS_PERMS = 100;


    public static final String EMAIL_ID = "EMAIL_ID";
    public static final String VISITOR_ID = "VISITOR_ID";
    public static final String SHARED_PREF_FILE = "SharedPrefsFile";

    public static final String KEY_VISITORS_END_POINTS_LOADED_TO_PREFS = "KEY_VISITORS_END_POINTS_LOADED_TO_PREFS";
    public static final String KEY_VISITORS_END_POINT_URL = "KEY_VISITORS_END_POINT_URL_";
    public static final String KEY_VISITORS_END_POINT_URLS_COUNT = "KEY_VISITORS_END_POINT_URLS_COUNT";


    public static final int REQUEST_ENABLE_BLUETOOTH = 102;
    public static final int REQUEST_PHONE_PERMS = 103;

    public static final String COLLECTING_SERVICE_URL = "https://sqs.us-west-2.amazonaws.com/829558341394/Beacons_Median";
}
