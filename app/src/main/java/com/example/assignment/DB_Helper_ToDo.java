package com.example.assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DB_Helper_ToDo extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ToDo_DB";
    private static final String TABLE_NAME = "Todos";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DUE_DATE = "dueDate";
    private static final String COLUMN_DUE_TIME = "dueTime";

    public DB_Helper_ToDo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating a table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_DUE_DATE + " TEXT," +
                COLUMN_DUE_TIME + " TEXT" + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP Table IF EXISTS TABLE_NAME");

        // Create tables again
        onCreate(db);
    }

    // Create a to-do into the table
    public long create_todo(ToDo new_todo){

        // To check if insertion of data was successful
        long isSuccessful = -1;

        try {
            // Opens the database in writable mode using getWritableDatabase(),
            // allowing data to be inserted or updated. In this case, inserted.
            SQLiteDatabase db = getWritableDatabase();

            // Preparing the data
            // A ContentValues object (values) is created
            // to store a to-do
            ContentValues values = new ContentValues();

            // Extract the values from the new_todo object,
            // and put them into the values object, by using the put() method.
            values.put(COLUMN_EMAIL, new_todo.getEmail()); // Email
            values.put(COLUMN_TITLE, new_todo.getTitle()); // Title
            values.put(COLUMN_DESCRIPTION, new_todo.getDescription()); // Description
            values.put(COLUMN_DUE_DATE, new_todo.getDueDate()); // Due Date
            values.put(COLUMN_DUE_TIME, new_todo.getDueTime()); // Due Time

            // Attempts to insert the data into the table
            try{
                // If successful, the insert() method returns a positive number
                isSuccessful = db.insert(TABLE_NAME, null, values);
            }
            catch (SQLiteException e)
            {
                // If insertion not successful,
                // isSuccessful is still -1.
                // -1 means insertion failed
                e.printStackTrace();
            }

            // Whether the data insertion was successful or not, close the database afterwards
            db.close();

        }
        catch (Exception exp) {
            exp.printStackTrace();
        }

        return isSuccessful;

    }

    // Read all to-dos from the table
    public ArrayList<ToDo> read_all_todos(String user_email){

        // Create an array list of ToDo objects
        ArrayList<ToDo> list_of_todos = new ArrayList<>();

        // An SQL query that reads all the data from the table
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " =?";

        // Initialize cursor
        Cursor cursor = null;

        // Opens the database in readable mode using getReadableDatabase(),
        // allowing data to be read.
        SQLiteDatabase db = this.getReadableDatabase();

        // Attempt to read data from the database
        try {
            // Execute the query without parameters
            cursor = db.rawQuery(query, new String[]{user_email});

            // Check if the cursor has any results
            if (cursor.moveToFirst()) {

                do {
                    // Create a new ToDo object
                    ToDo todo = new ToDo();
                    todo.setEmail(cursor.getString(0)); // 0 means the 1st column of the table (email)
                    todo.setTitle(cursor.getString(1)); // 1 means the 2nd column of the table (title)
                    todo.setDescription(cursor.getString(2)); // 2 means the 3rd column of the table (description)
                    todo.setDueDate(cursor.getString(3)); // 3 means the 4th column of the table (dueDate)
                    todo.setDueTime(cursor.getString(4)); // 4 means the 5th column of the table (dueTime)

                    list_of_todos.add(todo); // Add the to-do to the list

                } while (cursor.moveToNext()); // Move to the next record
            }

        }

        // If something went wrong when reading data from the table
        catch (SQLiteException e) {
            e.printStackTrace(); // Handle any exceptions
        }

        // finally block will always run after the try and catch blocks,
        // regardless of whether an exception occurred or not.
        finally {

            if (cursor != null) {
                // Close the cursor if it was opened, by using the close() method
                // cursor can only be closed when it is not null
                // If cursor is null, and you try to close it, it will return NullPointerException.
                cursor.close();
            }

            // Close the database connection when you finish using it
            db.close();

        }

        return list_of_todos; // Return the list of tasks

    }

    // Read all and sort by title (A-Z or Z-A)
    public ArrayList<ToDo> read_all_and_sort_title(String query_order, String user_email){

        ArrayList<ToDo> list_of_todos_sorted = new ArrayList<>();

        // query_order variable can either be ASC or DESC only.
        // If it is neither, send back an empty array list
        if(!query_order.equals("ASC") && !query_order.equals("DESC")){
            return list_of_todos_sorted;
        }

        // Opens the database in readable mode using getReadableDatabase(), allowing data to be read.
        SQLiteDatabase db = this.getReadableDatabase();

        // Initialize cursor
        Cursor cursor = null;

        // An SQL query that reads all the data from the table, and it is sorted by title in ascending order
        // String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " =?";
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ? " + "ORDER BY " + COLUMN_TITLE + " " + query_order;

        // Attempt to read data from the database
        try {
            // Execute the query without parameters
            cursor = db.rawQuery(query, new String[]{user_email});

            // Check if the cursor has any results
            if (cursor.moveToFirst()) {

                do {
                    // Create a new ToDo object
                    ToDo todo = new ToDo();
                    todo.setEmail(cursor.getString(0));       // 0 means the 1st column of the table (email)
                    todo.setTitle(cursor.getString(1));       // 1 means the 2nd column of the table (title)
                    todo.setDescription(cursor.getString(2)); // 2 means the 3rd column of the table (description)
                    todo.setDueDate(cursor.getString(3));     // 3 means the 4th column of the table (dueDate)
                    todo.setDueTime(cursor.getString(4));     // 4 means the 5th column of the table (dueTime)

                    list_of_todos_sorted.add(todo); // Add the to-do to the list

                } while (cursor.moveToNext()); // Move to the next record
            }

        }

        // If something went wrong when reading data from the table
        catch (SQLiteException e) {
            e.printStackTrace(); // Handle any exceptions
        }

        // finally block will always run after the try and catch blocks, regardless of whether an exception occurred or not.
        finally {

            if (cursor != null) {
                // Close the cursor if it was opened, by using the close() method
                // cursor can only be closed when it is not null
                // If cursor is null, and you try to close it, it will return NullPointerException.
                cursor.close();
            }

            // Close the database connection when you finish using it
            db.close();

        }

        return list_of_todos_sorted; // Return the list of tasks

    }

    // Read all and sort by date
    public ArrayList<ToDo> read_all_and_sort_date(String query_order, String user_email){

        ArrayList<ToDo> list_of_todos_sorted = new ArrayList<>();

        // query_order variable can either be ASC or DESC only.
        // If it is neither, send back an empty array list
        if(!query_order.equals("ASC") && !query_order.equals("DESC")){
            return list_of_todos_sorted;
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " =?";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{user_email});

        if (cursor.moveToFirst()) {
            do {
                ToDo todo = new ToDo();
                todo.setEmail(cursor.getString(0));       // 0 means the 1st column of the table (email)
                todo.setTitle(cursor.getString(1));       // 1 means the 2nd column of the table (title)
                todo.setDescription(cursor.getString(2)); // 2 means the 3rd column of the table (description)
                todo.setDueDate(cursor.getString(3));     // 3 means the 4th column of the table (dueDate)
                todo.setDueTime(cursor.getString(4));     // 4 means the 5th column of the table (dueTime)

                list_of_todos_sorted.add(todo);

            } while (cursor.moveToNext());
        }

        cursor.close(); //Don't forget to close your cursor
        db.close();


        for (int i = 0; i < list_of_todos_sorted.size(); i++) {
            // find position of smallest num between (i + 1)th element and last element
            int pos = i;

            for (int j = i; j < list_of_todos_sorted.size(); j++) {

                if (isDateInThePast(list_of_todos_sorted.get(j).getDueDate(), list_of_todos_sorted.get(pos).getDueDate())) {
                    pos = j;
                }
                else if (list_of_todos_sorted.get(j).getDueDate().matches(list_of_todos_sorted.get(pos).getDueDate())) {
                    if (isTimeInThePast(list_of_todos_sorted.get(j).getDueTime(), list_of_todos_sorted.get(pos).getDueTime())) {
                        pos = j;
                    }
                }
            }

            // Swap min (smallest num) to current position on array
            ToDo min = list_of_todos_sorted.get(pos);
            list_of_todos_sorted.set(pos, list_of_todos_sorted.get(i));
            list_of_todos_sorted.set(i, min);
        }

        if(query_order.equals("DESC")){
            Collections.reverse(list_of_todos_sorted);
        }

        return list_of_todos_sorted;
    }

    // Update one to-do from the table
    public boolean update_todo(ToDo old_todo, ToDo updated_todo){

        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a ContentValues object to hold the new data
        ContentValues values = new ContentValues();

        // Put the updated values into the ContentValues object
        values.put(COLUMN_EMAIL, updated_todo.getEmail());
        values.put(COLUMN_TITLE, updated_todo.getTitle());
        values.put(COLUMN_DESCRIPTION, updated_todo.getDescription());
        values.put(COLUMN_DUE_DATE, updated_todo.getDueDate());
        values.put(COLUMN_DUE_TIME, updated_todo.getDueTime());

        // Define the selection criteria for the record to be updated
        String selection = COLUMN_EMAIL + " = ? AND " +
                COLUMN_TITLE + " = ? AND " +
                COLUMN_DESCRIPTION + " = ? AND " +
                COLUMN_DUE_DATE + " = ? AND " +
                COLUMN_DUE_TIME + " = ?";

        // Define the selection arguments based on the old_to_do values
        String[] selectionArgs = {
                old_todo.getEmail(),
                old_todo.getTitle(),
                old_todo.getDescription(),
                old_todo.getDueDate(),
                old_todo.getDueTime()
        };

        // Execute the update and get the number of rows affected
        int count = db.update(TABLE_NAME, values, selection, selectionArgs);

        db.close(); // Close the database

        // Check if the update was successful
        // Return true if at least one record was updated, otherwise false
        return count > 0;

    }

    // Delete one to-do from the table{
    public boolean delete_todo(ToDo todo_toBeDeleted){

        boolean isDeleted = true;

        // Opens the database in writable mode using getWritableDatabase(),
        // allowing data to be inserted, updated or deleted. In this case, deleted.
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            // Define the where clause for multiple attributes (AND condition)
            String whereClause = COLUMN_EMAIL + " = ? AND " +
                    COLUMN_TITLE + " = ? AND " +
                    COLUMN_DESCRIPTION + " = ? AND " +
                    COLUMN_DUE_DATE + " = ? AND " +
                    COLUMN_DUE_TIME + " = ?";

            // Define the arguments for the where clause (values from the To-Do item to delete)
            String[] whereArgs = {
                    todo_toBeDeleted.getEmail(),       // Email
                    todo_toBeDeleted.getTitle(),       // Title
                    todo_toBeDeleted.getDescription(), // Description
                    todo_toBeDeleted.getDueDate(),     // Due Date
                    todo_toBeDeleted.getDueTime()      // Due Time
            };

            // Perform the delete operation
            int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);

            // Check if the deletion was successful
            if (rowsDeleted > 0) {
                System.out.println("To-do item deleted successfully.");
            }
            else {
                isDeleted = false;
                System.out.println("To-do item not found or deletion failed.");
            }
        }
        catch (SQLiteException e) {
            isDeleted = false;
            System.out.println("Error occurred when deleting to-do.");
            e.printStackTrace();
        }
        finally {
            db.close(); // Always close the database to free up resources
        }

        return isDeleted;
    }

    private boolean isDateInThePast(String date1, String date2) {
        //String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date convertDate1 = dateFormat.parse(date1);
            Date convertDate2 = dateFormat.parse(date2);
            Log.i("Your Tag for identy", "Your String ");
            if(convertDate1.after(convertDate2)){
                Log.v("Lol", String.valueOf(convertDate1) + " is after " + convertDate2);
            }
            else{
                Log.v("Lol", String.valueOf(convertDate2) + " is after " + convertDate1);
            }
            
            return convertDate1.after(convertDate2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isTimeInThePast(String time, String endTime) {
        String pattern = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date convertedTime1 = sdf.parse(time);
            Date convertedTime2 = sdf.parse(endTime);

            return convertedTime1.after(convertedTime2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
