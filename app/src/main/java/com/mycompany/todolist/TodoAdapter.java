package com.mycompany.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ekucukog on 3/24/2015.
 */
public class TodoAdapter extends ArrayAdapter<TodoItem> {

    public TodoAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        TextView tv_desc = (TextView) convertView.findViewById(R.id.tvdesc);
        TextView tv_pri = (TextView) convertView.findViewById(R.id.tvpri);
        TextView tv_due = (TextView) convertView.findViewById(R.id.tvdue);

        String pri = item.priority + "";
        // Populate the data into the template view using the data object
        tv_desc.setText(item.description);
        tv_pri.setText(pri);
        tv_due.setText(item.dueDate + "");

        // Return the completed view to render on screen
        return convertView;
    }
}
