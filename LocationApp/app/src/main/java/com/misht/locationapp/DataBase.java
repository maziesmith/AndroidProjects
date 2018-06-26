package com.misht.locationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Omen23 on 21/02/2018.
 */

public class DataBase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myDatabase.db";
    private static String TABLE_NAME = "notifications";

    //Table colum names
    private static final String COL_1 = "LATITUDE";
    private static final String COL_2 = "LONGITUDE";
    private static final String COL_3 = "MESSAGE";

    private static final String DATABASE_CREATE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS" +  TABLE_NAME + "(" + COL_1 + "TEXT," +COL_2 + "TEXT," + COL_3 + "TEXT,);";

    public DataBase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //To create tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(LATITUDE TEXT, LONGITUDE TEXT, MESSAGE TEXT)" );
    }

    //To upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public boolean addNotification(String latitude, String longitude, String message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_1, latitude);
        values.put(COL_2, longitude);
        values.put(COL_3, message);
        // Inserting Row
        long result = db.insert(TABLE_NAME, null, values);
        if(result == -1)
            return false;
        else
            return true;
    }

    //To get all the notifications stored in the database
    public Cursor getAllNotification() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    // Updating single contact
    /*public boolean updateNotification(String latitude, String longitude, String message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_1, latitude);
        values.put(COL_2, longitude);
        values.put(COL_3, message);

        // updating TABLE_NOTIFICATION
        db.update(TABLE_NAME, values, "ID = ?",new String[] { id });
        return true;
    }*/

}
