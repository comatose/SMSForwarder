package com.example.comatose.smsforwarder;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String DATABASE_TABLE_NAME = "message_filter";
    public static final String COLID = "id";
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

    public ArrayList<Matcher> listMatchers() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Matcher> results = new ArrayList<Matcher>();

        Cursor cursor = db.rawQuery("SELECT * from " + DATABASE_TABLE_NAME, new String[] {});

        if (cursor.moveToFirst()) {
            do {
                results.add(new Matcher(cursor.getInt(cursor.getColumnIndex(COLID)),
                        cursor.getString(cursor.getColumnIndex(COLVALUE))));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return results;
    }

    public boolean removeMatcher(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DATABASE_TABLE_NAME, COLID + "=" + id, null) == 1;
    }

    public Matcher executeMatchers(String message) {
        for (Matcher matcher : listMatchers()) {
            if(message.contains(matcher.value))
                return matcher;
        }
        return null;
    }
}