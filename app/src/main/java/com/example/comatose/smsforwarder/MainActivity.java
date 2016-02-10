package com.example.comatose.smsforwarder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Out custom adapter
    MySimpleArrayAdapter adapter;

    // contains our listview items
    ArrayList<com.example.comatose.smsforwarder.DatabaseHelper.Matcher> listItems;

    // database
    DatabaseHelper DatabaseHelper;

    // list of todo titles
    ArrayList<String> newData;

    // contains the id of the item we are about to delete
    public int deleteItem;

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

        // We're getting our listView by the id
        ListView listView = (ListView) findViewById(R.id.listView);

        // Creating a new instance of our DatabaseHelper, which we've created
        // earlier
        DatabaseHelper = new DatabaseHelper(this);

        // This returns a list of all our current available notes
        listItems = DatabaseHelper.getAll();

        newData = new ArrayList<String>();

        // Assigning the title to our global property so we can access it
        // later after certain actions (deleting/adding)
        for (com.example.comatose.smsforwarder.DatabaseHelper.Matcher note : listItems) {
            newData.add(note.value);
        }

        // We're initialising our custom adapter with all our data from the
        // database
        adapter = new MySimpleArrayAdapter(this, newData);

        // Assigning the adapter to ListView
        listView.setAdapter(adapter);

        // Assigning an event to the listview
        // This event will be used to delete records
        listView.setOnItemLongClickListener(myClickListener);

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
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                showChangeReceiver();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public MySimpleArrayAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.rowlayout, values);

            this.context = context;
            this.values = values;
        }

        /**
         * Here we go and get our rowlayout.xml file and set the textview text.
         * This happens for every row in your listview.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);

            // Setting the text to display
            textView.setText(values.get(position));

            return rowView;
        }
    }

    /**
     * On a long click delete the selected item
     */
    public AdapterView.OnItemLongClickListener myClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            // Assigning the item position to our global variable
            // So we can access it within our AlertDialog below
            deleteItem = arg2;

            // Creating a new alert dialog to confirm the delete
            AlertDialog alert = new AlertDialog.Builder(arg1.getContext())
                    .setTitle("Delete " + listItems.get(deleteItem).value)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // Retrieving the note from our listItems
                                    // property, which contains all notes from
                                    // our database
                                    com.example.comatose.smsforwarder.DatabaseHelper.Matcher note = listItems.get(deleteItem);

                                    // Deleting it from the ArrayList<string>
                                    // property which is linked to our adapter
                                    newData.remove(deleteItem);

                                    // Deleting the note from our database
                                    DatabaseHelper.deleteNote(note.id);

                                    // Tell the adapter to update the list view
                                    // with the latest changes
                                    adapter.notifyDataSetChanged();

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

    /**
     * This simply shows a alert dialog asking for the todo text
     */
    public void showCreateNote() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Input a pattern");

        final EditText name = new EditText(this);
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (name.getText().toString().length() > 0) {
                    long Id = DatabaseHelper.addRecord(name.getText().toString());

                    com.example.comatose.smsforwarder.DatabaseHelper.Matcher matcher = DatabaseHelper.new Matcher();
                    matcher.id = (int) Id;
                    matcher.value = name.getText().toString();

                    listItems.add(matcher);
                    newData.add(matcher.value);
                    adapter.notifyDataSetChanged();
                }

                // This hides the android keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });


        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // This hides the android keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });


        alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // This hides the android keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        alert.show();
    }
}
