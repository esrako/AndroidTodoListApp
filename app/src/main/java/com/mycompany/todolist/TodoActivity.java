package com.mycompany.todolist;

import android.app.DatePickerDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TodoActivity extends ActionBarActivity implements EditTodoDialog.EditTodoDialogListener{

    private TodoAdapter adapter;
    ArrayList<TodoItem> arrayOfMyItems;
    private ListView lvItems;
    private int mYear=0, mMonth=0, mDay=0;
    EditText txtDate;
    EditText etPri;

    private final int REQUEST_CODE = 20;
    private static final String TAG = TodoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        lvItems = (ListView)findViewById(R.id.listViewItems);

        Log.d(TAG, "Will initialize myItems");
        arrayOfMyItems = new ArrayList<TodoItem>();
        readItemsFromDB();

        // Create the adapter to convert the array to views
        adapter = new TodoAdapter(this, arrayOfMyItems);
        // Attach the adapter to a ListView
        lvItems.setAdapter(adapter);

        txtDate = (EditText) findViewById(R.id.txtDate);
        etPri = (EditText) findViewById(R.id.add_Pri);

        //default values
        txtDate.setText(todayAsString());
        etPri.setText("3");

        Log.d(TAG, "Will set up view listener now");

        setupListViewListener();
    }

    public void onAddItem(View v){

        Log.d(TAG, "onAddItem() adding a new item to the list");

        //Add item to list
        EditText etNewItem = (EditText) findViewById(R.id.addNewItem);

        // Create an item on SQLite
        TodoItem item = new TodoItem();
        item.priority = (int)Integer.parseInt(etPri.getText().toString());
        item.dueDate = txtDate.getText().toString();
        item.description = etNewItem.getText().toString();
        item.save();

        adapter.add(item);

        //default values
        etNewItem.setText("");
        etPri.setText("3");
        txtDate.setText(todayAsString());
        Log.d(TAG, "onAddItem() A new item added to the list");

    }

    private void  setupListViewListener(){

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "long click event");
                String itemDesc = arrayOfMyItems.get(position).description;
                long idOfItem = arrayOfMyItems.get(position).getId();

                // Deleting item from SQLite
                TodoItem item = TodoItem.load(TodoItem.class, idOfItem);
                item.delete();

                //Delete item from list
                arrayOfMyItems.remove(position);
                adapter.notifyDataSetChanged();

                Log.d(TAG, "Done with long click event");
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "click event");
                showEditDialog(position);
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

    /*
     * Reads all items from DB and populates items Array
     */
    private void readItemsFromDB(){

        Log.d(TAG, "Will read items from DB");
        List<TodoItem> allItems = TodoItem.getAllItems();
        arrayOfMyItems = (ArrayList<TodoItem>)allItems;
        Log.d(TAG, "Done with reading items from DB");

    }

    private void showEditDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();
        String oldDesc = arrayOfMyItems.get(position).description;
        int oldPri = arrayOfMyItems.get(position).priority;
        EditTodoDialog etDialog = EditTodoDialog.newInstance(position, oldDesc, oldPri, "Edit Item");
        etDialog.show(fm, "fragment_edit_todo");
    }

    @Override
    public void onFinishEditDialog(int position, String newDesc, int newPri) {

        Log.d(TAG, "Entering onFinishedEditDialog");

        long idOfItem = arrayOfMyItems.get(position).getId();
        String dued = arrayOfMyItems.get(position).dueDate;

        // Deleting item from SQLite
        TodoItem item = TodoItem.load(TodoItem.class, idOfItem);
        item.delete();

        //!!!this way not working-why?
        //item.description = newText;
        //item.save();

        // Create an item
        item = new TodoItem();
        item.priority = newPri;
        item.dueDate = dued;
        item.description = newDesc;
        item.save();

        arrayOfMyItems.set(position, item);
        adapter.notifyDataSetChanged();

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
                        txtDate.setText(dateToString(year, monthOfYear, dayOfMonth));
                    }
                }, mYear, mMonth, mDay);

        Log.d(TAG, "mYear: " + mYear + ", mMonth: " + mMonth + ", mDay: " + mDay);
        Log.d(TAG, "Done with showTimePickerDialog");
        dpd.show();
    }

    //gets String version of current date
    private String todayAsString(){

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return dateToString(year, month, day);
    }

    //gets String version of given date
    private String dateToString(int year, int month, int day){
        return year + "-" + (month+1) + "-" + day;
    }
}
