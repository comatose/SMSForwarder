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
    DatabaseAdapter adapter;

    ArrayList<DatabaseHelper.Matcher> matchers;

    DatabaseHelper db;

    ArrayList<String> matcherList;

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

        db = new DatabaseHelper(this);

        matchers = db.listMatchers();

        matcherList = new ArrayList<String>();

        for (DatabaseHelper.Matcher matcher : matchers) {
            matcherList.add(matcher.value);
        }

        adapter = new DatabaseAdapter(this);

        listView.setAdapter(adapter);

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

    public class DatabaseAdapter extends ArrayAdapter<String> {
        private final Context context;

        public DatabaseAdapter(Context context) {
            super(context, R.layout.rowlayout, matcherList);

            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);
            textView.setText(matcherList.get(position));

            return rowView;
        }
    }

    /**
     * On a long click delete the selected item
     */
    public AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            final int deleteItem = arg2;

            // Creating a new alert dialog to confirm the delete
            AlertDialog alert = new AlertDialog.Builder(arg1.getContext())
                    .setTitle("Delete " + matchers.get(deleteItem).value)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // Retrieving the note from our matchers
                                    // property, which contains all notes from
                                    // our database
                                    DatabaseHelper.Matcher note = matchers.get(deleteItem);

                                    // Deleting it from the ArrayList<string>
                                    // property which is linked to our adapter
                                    matcherList.remove(deleteItem);

                                    // Deleting the note from our database
                                    db.removeMatcher(note.id);

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

    public void showCreateNote() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Input a pattern");

        final EditText name = new EditText(this);
        alert.setView(name);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (name.getText().toString().length() > 0) {
                    long Id = db.addMatcher(name.getText().toString());

                    DatabaseHelper.Matcher matcher = db.new Matcher((int) Id, name.getText().toString());

                    matchers.add(matcher);
                    matcherList.add(matcher.value);
                    adapter.notifyDataSetChanged();
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
