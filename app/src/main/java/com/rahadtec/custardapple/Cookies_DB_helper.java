package com.rahadtec.custardapple;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper; // এটি ইমপোর্ট করতে হবে

public class Cookies_DB_helper extends SQLiteOpenHelper { // SQLiteOpenHelper যোগ করা হয়েছে

    private static final String DATABASE_NAME = "Custard_Cookies.db";
    private static final int DATABASE_VERSION = 1;

    // টেবিল ও কলামের নাম
    public static final String TABLE_NAME = "user_cookies";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_SECRET_KEY = "cookies";

    public Cookies_DB_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // টেবিল তৈরির কোয়েরি
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_SECRET_KEY + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ডেটা ইনসার্ট করার মেথড
    public boolean insertData(String user, String pass, String secret) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, user);
        values.put(COL_PASSWORD, pass);
        values.put(COL_SECRET_KEY, secret);

        long result = db.insert(TABLE_NAME, null, values);
        // db.close(); // ডাটা ইনসার্ট করার পর চাইলে ক্লোজ করতে পারেন
        return result != -1;
    }

    // মোট কয়টি ডাটা আছে তা দেখার মেথড
    public int getTotalCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    // সব ডাটা পাওয়ার মেথড
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);
    }

    // সব ডাটা মুছে ফেলার মেথড
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // সহজ পদ্ধতি
        // অটো-ইনক্রিমেন্ট আইডি রিসেট করার জন্য:
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLE_NAME + "'");
        db.close();
    }
}