package com.mycompany.todolist;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TodoActivity extends ActionBarActivity implements EditTodoDialog.EditTodoDialogListener{

    private TodoCursorAdapter todoAdapter;
    private ListView lvItems;
    private int mYear=0, mMonth=0, mDay=0;
    private EditText txtDesc;
    private TextView txtDate;
    private EditText etPri;
    private TodoItem itemToBeEdited = null;

    private static final String TAG = TodoActivity.class.getSimpleName();
    private static final int DEFAULT_PRIORITY = 3;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        Log.d(TAG, "Will initialize lvItems");

        todoAdapter = new TodoCursorAdapter(this, TodoItem.fetchResultCursorSortedByDate());
        lvItems = (ListView)findViewById(R.id.listViewItems);
        lvItems.setAdapter(todoAdapter);

        txtDesc = (EditText) findViewById(R.id.addNewItem);
        txtDate = (TextView) findViewById(R.id.txtDate);
        etPri = (EditText) findViewById(R.id.add_Pri);

        //default values
        txtDate.setText(todayAsString());
        etPri.setText(String.valueOf(DEFAULT_PRIORITY));

        Log.d(TAG, "Will set up view listener now");

        setupListViewListener();
    }

    public void onAddItem(View v){

        Log.d(TAG, "onAddItem() adding a new item");

        // Create an item on SQLite
        TodoItem item = new TodoItem();
        item.priority = (int)Integer.parseInt(etPri.getText().toString());
        item.dueDate = stringTimeToLong(txtDate.getText().toString());
        item.description = txtDesc.getText().toString();
        item.save();

        //update adapter
        todoAdapter.changeCursor(TodoItem.fetchResultCursorSortedByDate());

        //put default values back
        txtDesc.setText("");
        etPri.setText(String.valueOf(DEFAULT_PRIORITY));
        txtDate.setText(todayAsString());

        Toast.makeText(this, getString(R.string.item_suc_added), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onAddItem() A new item added");

    }

    private void  setupListViewListener(){

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "long click event");

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int itemID = cursor.getInt(cursor.getColumnIndexOrThrow("Id"));
                TodoItem item = TodoItem.load(TodoItem.class, itemID/*id*/);
                item.delete();

                todoAdapter.changeCursor(TodoItem.fetchResultCursorSortedByDate());

                Toast.makeText(getApplicationContext(), getString(R.string.item_suc_deleted), Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Done with long click event");
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "click event");
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int itemID = cursor.getInt(cursor.getColumnIndexOrThrow("Id"));
                itemToBeEdited = TodoItem.load(TodoItem.class, itemID/*id*/);
                showEditDialog();
                Log.d(TAG, "Done with click event");
            }
        }); ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();

        String oldDesc = itemToBeEdited.description;
        int oldPri = itemToBeEdited.priority;
        long ms = itemToBeEdited.dueDate;
        String oldDue = longTimeToString(ms);

        EditTodoDialog etDialog = EditTodoDialog.newInstance(this, oldDesc, oldPri, oldDue, getString(R.string.edit_item));
        etDialog.show(fm, "fragment_edit_todo");
    }

    @Override
    public void onFinishEditDialog(String newDesc, int newPri, String newDue) {

        Log.d(TAG, "Entering onFinishedEditDialog");

        itemToBeEdited.priority = newPri;
        itemToBeEdited.dueDate = stringTimeToLong(newDue);
        itemToBeEdited.description = newDesc;
        itemToBeEdited.save();

        todoAdapter.changeCursor(TodoItem.fetchResultCursorSortedByDate());

        Toast.makeText(this, getString(R.string.item_suc_edited), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Done with editing item onFinishedEditDialog");
    }

    public void showDatePickerDialog(View v) {

        Log.d(TAG, "Entering showDatePickerDialog");

        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        Calendar ca = Calendar.getInstance();
                        ca.set(year, monthOfYear, dayOfMonth);

                        txtDate.setText(calendarToString(ca));
                    }
                }, mYear, mMonth, mDay);

        Log.d(TAG, "mYear: " + mYear + ", mMonth: " + mMonth + ", mDay: " + mDay);
        Log.d(TAG, "Done with showTimePickerDialog");
        dpd.show();
    }

    //gets String version of current date
    public static String todayAsString(){

        Calendar c = Calendar.getInstance();
        return calendarToString(c);
    }

    //gets String version of given calendar
    public static String calendarToString(Calendar c){

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String out = formatter.format(c.getTime());
        Log.d(TAG, "calendar string: " + out);

        return out;
    }

    //gets String version Date given long time milliseconds
    public static String longTimeToString(long ms){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        return calendarToString(cal);
    }

    //gets the time in milliseconds for a formatted String of a date
    public static long stringTimeToLong(String stringDate){

        Date date = null;
        try{
            date = (Date)formatter.parse(stringDate);
        }catch(ParseException e){
            Log.d(TAG, "Exception while parsing the date: " + stringDate);
            e.printStackTrace();
        }
        return date.getTime();
    }
}
