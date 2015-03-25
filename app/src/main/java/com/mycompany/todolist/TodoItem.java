package com.mycompany.todolist;

/**
 * Created by ekucukog on 3/20/2015.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Todoitems")
public class TodoItem extends Model {

    @Column(name = "Description")
    public String description;

    @Column(name = "Priority")
    public int priority;

    // Make sure to have a default constructor for every ActiveAndroid model
    public TodoItem(){
        super();
    }

    public TodoItem(String description, int priority){
        super();
        this.description = description;
        this.priority = priority;
    }

    //do not use this one - for reference purposes only
    public static List<TodoItem> getAll(int priority) {
        // This is how you execute a query
        return new Select()
                .from(TodoItem.class)
                .where("Priority = ?", priority)
                .orderBy("Description ASC")
                .execute();
    }

    public static List<TodoItem> getAllItems() {
        return new Select()
                .from(TodoItem.class)
                .execute();
    }
}
