package com.example.comatose.smsforwarder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SimpleCursorAdapter adapter = null;
    MatcherDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateNote();
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);

        db = new MatcherDatabase(this);

        Cursor cursor = db.getWritableDatabase().rawQuery("SELECT * FROM " + MatcherDatabase.DATABASE_TABLE_NAME, null);

        adapter = new SimpleCursorAdapter(this,
                R.layout.rowlayout, cursor,
                new String[] { MatcherDatabase.COLVALUE }, new int[] { R.id.label },
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

        listView.setOnItemLongClickListener(onItemLongClickListener);
        hideKeyboard();
    }

    private void hideKeyboard() {
        // This hides the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showChangeReceiver();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * On a long click delete the selected item
     */
    public AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Log.i("SMSForwarder", "arg2=" + arg2);
            Log.i("SMSForwarder", "arg3=" + arg3);
            final long selected = arg3;

            // Creating a new alert dialog to confirm the delete
            AlertDialog alert = new AlertDialog.Builder(arg1.getContext())
                    .setTitle("Remove?")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    if(db.removeMatcher(selected)) {
                                        Cursor cursor = db.getWritableDatabase().rawQuery("SELECT * FROM " + MatcherDatabase.DATABASE_TABLE_NAME, null);
                                        adapter.changeCursor(cursor);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Remove failed.", Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // When you press cancel, just close the
                                    // dialog
                                    dialog.cancel();
                                }
                            }).show();

            return false;
        }
    };

    public void showCreateNote() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Input a pattern");

        final EditText name = new EditText(this);
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (name.getText().toString().length() > 0) {
                    long id = db.addMatcher(name.getText().toString());
                    if(id != -1) {
                        Cursor cursor = db.getWritableDatabase().rawQuery("SELECT * FROM " + MatcherDatabase.DATABASE_TABLE_NAME, null);
                        adapter.changeCursor(cursor);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Add failed", Toast.LENGTH_SHORT).show();
                    }
                }

                // This hides the android keyboard
                hideKeyboard();
            }
        });


        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // This hides the android keyboard
                hideKeyboard();
            }
        });

        alert.show();
    }

    public void showChangeReceiver() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change the receiver number");

        final EditText name = new EditText(this);
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (name.getText().toString().length() > 0) {
                }

                // This hides the android keyboard
                hideKeyboard();
            }
        });


        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // This hides the android keyboard
                hideKeyboard();
            }
        });

        alert.show();
    }
}
