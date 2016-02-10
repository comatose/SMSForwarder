package com.example.comatose.smsforwarder;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MatcherDatabase extends SQLiteOpenHelper {
    public class Matcher {
        public Matcher(int id, String value) {
            this.id = id;
            this.value = value;
        }
        int id;
        String value;
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sms_forwarder";
    public static final String DATABASE_TABLE_NAME = "message_filter";
    public static final String COLID = "_id";
    public static final String COLVALUE = "value";

    MatcherDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This runs once after the installation and creates a database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE_NAME + " (" + COLID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLVALUE + " TEXT)");
    }

    /**
     * This would run after the user updates the app. This is in case you want
     * to modify the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    public long addMatcher(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLVALUE, value);

        return db.insert(DATABASE_TABLE_NAME, null, cv);
    }

    public Matcher[] listMatchers() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * from " + DATABASE_TABLE_NAME, new String[]{});
        if(!cursor.moveToFirst()) // table is empty.
            return null;

        int count = cursor.getCount();
        Matcher results[] = new Matcher[count];
        for(int i = 0; i < count; ++i) {
            int id = cursor.getInt(cursor.getColumnIndex(COLID));
            String value = cursor.getString(cursor.getColumnIndex(COLVALUE));
            results[i] = new Matcher(id, value);
            Log.i("SMSForwarder", id + ", " + value);
            cursor.moveToNext();
        }

        cursor.close();
        return results;
    }

    public boolean removeMatcher(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DATABASE_TABLE_NAME, COLID + "=" + id, null) == 1;
    }

    public Matcher executeMatchers(String message) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * from " + DATABASE_TABLE_NAME, new String[]{});
        try {
            if(cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(COLID));
                    String value = cursor.getString(cursor.getColumnIndex(COLVALUE));
                    Log.i("SMSForwarder", id + ", " + value);
                    if(message.contains(value))
                        return new Matcher(id, value);
                } while (cursor.moveToNext());
            }
            return null;
        } finally {
            cursor.close();
        }
    }
}