package com.xinthe.spax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Koti
 *         Class to manage with SQLite Database
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "collecting-app-estimote";
    private static final String TABLE_ENDPOINT = "endPoint";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_COLLECTEDDATA = "collectedData";

    private static final String KEY_ID = "id";

    /**
     * End Points Table Column
     */
    private static final String KEY_ENDPOINT = "endPoint";

    /**
     * Notifications Table Column
     */
    private static final String KEY_MESSAGE = "message";

    /**
     * FailedData Table Column
     */
    private static final String KEY_ERROR_CODE = "errorCode";
    private static final String KEY_NOOFATTEMPTS = "noOfAttempts";
    private static final String KEY_TIMESTAMP = "Timestamp";
    private static final String KEY_DATA = "data";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ENDPOINTS_TABLE = "CREATE TABLE " + TABLE_ENDPOINT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENDPOINT + " TEXT"
                + ")";
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE + " TEXT"
                + ")";
        String CREATE_FAILEDDATA_TABLE = "CREATE TABLE " + TABLE_COLLECTEDDATA
                + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOOFATTEMPTS
                + " INTEGER," + KEY_TIMESTAMP
                + " DATETIME DEFAULT CURRENT_TIMESTAMP," + KEY_DATA + " BLOB,"
                + KEY_ERROR_CODE + " INTEGER" + ")";
        db.execSQL(CREATE_ENDPOINTS_TABLE);
        db.execSQL(CREATE_FAILEDDATA_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENDPOINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTEDDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    /**
     * add New End Point
     *
     * @param endPoint
     */
    public void addEndPoint(EndPoint endPoint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENDPOINT, endPoint.getEndPoint());
        // Inserting Row
        db.insert(TABLE_ENDPOINT, null, values);
        db.close();
    }

    /**
     * Add End points in Bulk insert
     *
     * @param endPoints
     */
    public void addEndPoints(ArrayList<EndPoint> endPoints) {
        String sql = "INSERT INTO " + TABLE_ENDPOINT + " VALUES (?);";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        for (int i = 0; i < endPoints.size(); i++) {
            statement.clearBindings();
            statement.bindString(1, endPoints.get(i).getEndPoint());
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    /**
     * Get all endpoints from DB
     *
     * @return
     */
    public List<EndPoint> getAllEndPoints() {
        List<EndPoint> endPointsList = new ArrayList<EndPoint>();
        String selectQuery = "SELECT  * FROM " + TABLE_ENDPOINT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                EndPoint contact = new EndPoint();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setEndPoint(cursor.getString(1));
                endPointsList.add(contact);
            } while (cursor.moveToNext());
        }
        return endPointsList;
    }


    /**
     * @param endPoint
     * @return
     */
    public int updateEndPoint(EndPoint endPoint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENDPOINT, endPoint.getEndPoint());
        return db.update(TABLE_ENDPOINT, values, KEY_ID + " = ?",
                new String[]{String.valueOf(endPoint.getId())});
    }


    /**
     * @param collectedData
     */
    public void addCollectedData(CollectedData collectedData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOOFATTEMPTS, collectedData.getNoOfAttempts());
        // values.put(KEY_TIMESTAMP, failedData.getTimestamp());
        values.put(KEY_DATA, collectedData.getData());
        values.put(KEY_ERROR_CODE, collectedData.getErrorCode());
        // Inserting Row
        db.insert(TABLE_COLLECTEDDATA, null, values);
        db.close();
    }

    /**
     * @return
     */
    public List<CollectedData> getAllCollectedData() {
        List<CollectedData> collectedList = new ArrayList<CollectedData>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLLECTEDDATA
                + " ORDER BY " + KEY_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CollectedData collectedData = new CollectedData();
                collectedData.setId(cursor.getInt(0));
                collectedData.setNoOfAttempts(cursor.getInt(1));
                collectedData.setTimeStamp(cursor.getString(2));
                collectedData.setData(cursor.getBlob(3));
                collectedData.setErrorCode(cursor.getInt(4));
                collectedList.add(collectedData);
            } while (cursor.moveToNext());
        }
        return collectedList;
    }


    /**
     * @param collectedData
     * @return
     */
    public int updateCollectedDataAttempts(CollectedData collectedData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOOFATTEMPTS, collectedData.getNoOfAttempts() + 1);
        values.put(KEY_ERROR_CODE, collectedData.getErrorCode());
        return db.update(TABLE_COLLECTEDDATA, values, KEY_ID + " = ?",
                new String[]{String.valueOf(collectedData.getId())});
    }

    /**
     * @param id
     * @return
     */
    public int deleteCollectedData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_COLLECTEDDATA, KEY_ID + "=" + id, null);
    }


    /**
     * add New Notification
     *
     * @param message
     */
    public void addNotificationToDB(String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message);
        // Inserting Row
        db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
    }

    /**
     * @return
     */
    public ArrayList<String> getAllNotifications() {
        ArrayList<String> messagesList = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTIFICATIONS
                + " ORDER BY " + KEY_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                messagesList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return messagesList;
    }

}
