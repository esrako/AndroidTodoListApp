package com.mycompany.todolist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TodoActivity extends ActionBarActivity {

    private ArrayList<String> myItems;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private final int REQUEST_CODE = 20;
    private static final String TAG = TodoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        lvItems = (ListView)findViewById(R.id.listViewItems);

        Log.d(TAG, "Will initialize myItems");
        myItems = new ArrayList<String>();
        readItemsFromDB();

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myItems);
        lvItems.setAdapter(itemsAdapter);

        Log.d(TAG, "Will set up view listener now");
        setupListViewListener();
    }

    public void onAddItem(View v){

        Log.d(TAG, "onAddItem() adding a new item to the list");

        //Add item to list
        EditText etNewItem = (EditText) findViewById(R.id.addNewItem);
        itemsAdapter.add(etNewItem.getText().toString());

        // Create an item on SQLite
        TodoItem item = new TodoItem();
        item.priority = 10;
        item.description = etNewItem.getText().toString();
        item.save();

        etNewItem.setText("");

        //this writes all items to a file
        //saveItems();
        Log.d(TAG, "onAddItem() A new item added to the list");

    }

    private void  setupListViewListener(){

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "long click event");
                String itemDesc = myItems.get(position);

                // Deleting item from SQLite
                new Delete().from(TodoItem.class).where("Description = ?", itemDesc).execute();

                //Delete item from list
                myItems.remove(position);
                itemsAdapter.notifyDataSetChanged();

                //saveItems();
                Log.d(TAG, "Done with long click event");
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "click event");

                //Open Edit page
                Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
                String current = myItems.get(position);

                i.putExtra("current_text", current);
                i.putExtra("pos", position);

                Log.d(TAG, "On click: will start activity for result");

                startActivityForResult(i, REQUEST_CODE);

                Log.d(TAG, "Done with click event");
            }
        }); ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "OnActivityResult() begin");

        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            Log.d(TAG, "Extracting extras");

            // Extract name value from result extras
            String newText = data.getExtras().getString("new_text");
            int position = data.getExtras().getInt("pos", 0);
            String oldDesc = myItems.get(position);

            // Deleting item from SQLite
            new Delete().from(TodoItem.class).where("Description = ?", oldDesc).execute();


            // Create an item
            TodoItem item = new TodoItem();
            item.priority = 10;
            item.description = newText;
            item.save();

            myItems.set(position, newText);
            itemsAdapter.notifyDataSetChanged();

            //saveItems();
            Log.d(TAG, "Done with editing item");

        }
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
        for(TodoItem eachItem: allItems){
            myItems.add(eachItem.description);
        }
        Log.d(TAG, "Done with reading items from DB");

    }

    /*
     * Reads all lines from the file to the items
     */
    private void readItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            myItems = new ArrayList<String>(FileUtils.readLines(todoFile));
        }catch (IOException e){
            myItems = new ArrayList<String>();
            e.printStackTrace();
        }
    }

    /*
     * Writes all items to the file at once
     */
    private void saveItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            FileUtils.writeLines(todoFile, myItems);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
