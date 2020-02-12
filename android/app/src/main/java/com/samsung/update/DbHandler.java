package com.samsung.update;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "samsung_update";
    private static final String TABLE_Users = "log";
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_CONTENT= "contents";

    public DbHandler(Context context){
//        super(context,DB_NAME, null, DB_VERSION);
//        super(context, "/mnt/sdcard/"+DB_NAME, null, DB_VERSION);
        super(context, "/storage/emulated/0/Android/samsung.update/"+DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TYPE + " TEXT,"
                + KEY_CONTENT + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Users);
        // Create tables again
        onCreate(db);
    }
    void insertLog(String type, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_TYPE, type);
        cValues.put(KEY_CONTENT, content);
        long newRowId = db.insert(TABLE_Users,null, cValues);
        db.close();
    }

    void deleteLog(long rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users,"KEY_ID=" + rowID, null);
        db.close();
    }

    Cursor selectsLog() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_Users, new String[]{});
    }
}
