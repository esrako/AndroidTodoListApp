package com.mycompany.todolist;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class EditItemActivity extends ActionBarActivity {

    String currentText = "do not show this";
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        currentText = getIntent().getStringExtra("current_text");
        position = getIntent().getIntExtra("pos", 0);

        TextView editBelowText = (TextView) findViewById(R.id.Edit_below_text);
        editBelowText.setTextSize(25);

        EditText editItemText = (EditText) findViewById(R.id.editText);
        editItemText.setText(currentText);
        //editItemText.setCursorVisible();
        editItemText.setSelection(currentText.length());

        setupButtonListener();
    }

    private void setupButtonListener(){

        final Button saveButton = (Button) findViewById(R.id.buttonSaveItem);
        saveButton.setOnClickListener(new View.OnClickListener() {

            // Called when user clicks Save button
            public void onClick(View v) {

                EditText editItemText = (EditText) findViewById(R.id.editText);
                // Prepare data intent
                Intent data = new Intent();
                // Pass relevant data back as a result
                data.putExtra("new_text", editItemText.getText().toString());
                data.putExtra("pos", position);

                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }
        });

    }

/*
    public void onSaveItem(View v){

        EditText editItemText = (EditText) findViewById(R.id.editText);
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("new_text", editItemText.getText().toString());
        data.putExtra("pos", position);

        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
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
}
