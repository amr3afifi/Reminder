package com.example.reminder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.lang.reflect.Type;
import java.util.ArrayList;
import android.database.Cursor;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.os.Debug;
import android.view.ContextMenu;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    EditText et;
    Button can_bt;
    Button com_bt;
    Button upd_bt;
    ListView lv;
    CheckBox important;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    final RemindersDbAdapter MyDatabase = new RemindersDbAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        final String[] from = {RemindersDbAdapter.COL_ID, RemindersDbAdapter.COL_CONTENT, RemindersDbAdapter.COL_IMPORTANT};
        MyDatabase.open();

        important = (CheckBox) findViewById(R.id.reminder_checkbox);
        et = (EditText) findViewById(R.id.reminder_textbox);
        com_bt = (Button) findViewById(R.id.commit_button);
        can_bt = (Button) findViewById(R.id.cancel_button);
        upd_bt = (Button) findViewById(R.id.update_button);
        lv = (ListView) findViewById(R.id.listaya);

        final Cursor cursor = MyDatabase.fetchAllReminders();

        final RemindersSimpleCursorAdapter adapter = new RemindersSimpleCursorAdapter(this, R.layout.reminder_row, cursor, new String[]{RemindersDbAdapter.COL_CONTENT}, new int[]{R.id.textView}, 0);
        lv = findViewById(R.id.listaya);
        //LoadFromGson();
        lv.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et.setText(" ");
                important.setChecked(false);
                showpopup(0);
            }
        });

        can_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidepopup();
            }
        });

        com_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = et.getText().toString();

                if (result.contentEquals(" ") || result.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter text", Toast.LENGTH_LONG).show();
                } else {
                    if (important.isChecked())
                        MyDatabase.createReminder(result, true);
                     else
                        MyDatabase.createReminder(result, false);

                    notifyListUpdate();
                    //SaveToGson();
                    hidepopup();
                    Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reminder reminder = MyDatabase.fetchReminderById((int)id);
                Toast.makeText(getApplicationContext(), "You clicked on: " + reminder.getContent()+ "\n*Long Press to edit or delete*", Toast.LENGTH_LONG).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               //cursor.moveToPosition(position);
                final Reminder reminder = MyDatabase.fetchReminderById((int)id);
                new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_menu_edit).setTitle("Change Your Reminder ?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyDatabase.deleteReminderById(reminder.getId());
                        notifyListUpdate();
                    }
                }).setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et.setText(reminder.getContent());
                        if (reminder.getImportant() == 1)
                            important.setChecked(true);
                        else
                            important.setChecked(false);

                        showpopup(1);

                        upd_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (important.isChecked())
                                    reminder.setImportant(1);
                                else
                                    reminder.setImportant(0);

                                reminder.setContent(et.getText().toString());
                                MyDatabase.updateReminder(reminder);
                                notifyListUpdate();
                                //SaveToGson();
                                hidepopup();
                                Toast.makeText(getApplicationContext(), "Edited Reminder Successfully", Toast.LENGTH_LONG).show();
                            }

                        });

                    }
                }).show();
                return true;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.new_reminder) {
            showpopup(0);
        }
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Haha d7kt 3leek", Toast.LENGTH_LONG).show();
        }
        if (item.getItemId() == R.id.exit) {
            MyDatabase.close();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }


    public void showpopup(int type) {
        findViewById(R.id.label_newreminder).setVisibility(View.VISIBLE);
        findViewById(R.id.reminder_textbox).setVisibility(View.VISIBLE);
        findViewById(R.id.reminder_checkbox).setVisibility(View.VISIBLE);
        findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        if(type==0)
            findViewById(R.id.commit_button).setVisibility(View.VISIBLE);
            else
            findViewById(R.id.update_button).setVisibility(View.VISIBLE);
    }

    public void hidepopup() {
        findViewById(R.id.label_newreminder).setVisibility(View.INVISIBLE);
        findViewById(R.id.reminder_textbox).setVisibility(View.INVISIBLE);
        findViewById(R.id.reminder_checkbox).setVisibility(View.INVISIBLE);
        findViewById(R.id.cancel_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.commit_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.update_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.INVISIBLE);
    }

    private void notifyListUpdate() {
        ((RemindersSimpleCursorAdapter)lv.getAdapter()).swapCursor(MyDatabase.fetchAllReminders());
    }

    public void SaveToGson() {
        adapter.notifyDataSetChanged();
        //Save to mem
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("task list", json);
        editor.apply();
    }

    public void LoadFromGson() {
        //Load from mem
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        arrayList = gson.fromJson(json, type);

        if (arrayList == null) {
            arrayList = new ArrayList<String>();
        }

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
    }

}
