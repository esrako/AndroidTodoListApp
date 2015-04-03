package com.mycompany.todolist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by ekucukog on 3/28/2015.
 */
public class TodoCursorAdapter extends CursorAdapter {

    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView tv_desc = (TextView) view.findViewById(R.id.tvdesc);
        TextView tv_pri = (TextView) view.findViewById(R.id.tvpri);
        TextView tv_due = (TextView) view.findViewById(R.id.tvdue);

        // Extract properties from cursor
        String desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
        int pri = cursor.getInt(cursor.getColumnIndexOrThrow("Priority"));
        long time = cursor.getLong(cursor.getColumnIndexOrThrow("Duedate"));
        String due = TodoActivity.longTimeToString(time);

        // Populate fields with extracted properties
        tv_desc.setText(desc);
        tv_pri.setText(String.valueOf(pri));
        tv_due.setText(due);
    }
}