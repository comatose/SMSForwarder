package com.example.comatose.smsforwarder;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public class Matcher {
        public Matcher() {}
        int id;
        String value;
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sms_forwarder";
    private static final String DATABASE_TABLE_NAME = "message_filter";
    public static final String COLID = "id";
    public static final String COLVALUE = "value";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This runs once after the installation and creates a database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Here we are creating two columns in our database.
        // EntryID, which is the primary key and Title which will hold the
        // todo text
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

    /**
     * This method adds a record to the database. All we pass in is the todo
     * text
     */
    public long addRecord(String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLVALUE, msg);

        return db.insert(DATABASE_TABLE_NAME, null, cv);
    }

    /**
     * //This method returns all notes from the database
     */
    public ArrayList<Matcher> getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Matcher> listItems = new ArrayList<Matcher>();

        Cursor cursor = db.rawQuery("SELECT * from " + DATABASE_TABLE_NAME,
                new String[] {});

        if (cursor.moveToFirst()) {
            do {
                Matcher note = new Matcher();
                note.id = cursor.getInt(cursor.getColumnIndex(COLID));
                note.value = cursor.getString(cursor.getColumnIndex(COLVALUE));

                listItems.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listItems;
    }

    /*
     * //This method deletes a record from the database.
     */
    public void deleteNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_NAME + " WHERE " + COLID
                + "=" + id + "");
    }

    public Boolean match(String message) {
        // This returns a list of all our current available notes
        ArrayList<Matcher> listItems;
        listItems = getAll();

        // Assigning the title to our global property so we can access it
        // later after certain actions (deleting/adding)
        for (Matcher note : listItems) {
            if(message.contains(note.value))
                return true;
        }
        return false;
    }
}