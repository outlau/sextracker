package com.production.outlau.sextracker;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AppDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "calendarDB";

    //  Table names
    private static final String TABLE_CALENDAR = "calendar";

    // Table Columns names
    private static final String KEY_DATE = "date";
    private static final String KEY_SEX = "sex";

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CALENDAR_TABLE = "CREATE TABLE " + TABLE_CALENDAR + "("
                + KEY_DATE + " TEXT,"
                + KEY_SEX + " INTEGER DEFAULT 0)";

        db.execSQL(CREATE_CALENDAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR);
        // Creating tables again
        onCreate(db);
    }

    /*
     * INSERT QUERIES
     */

    public void insertValueToTable(String date, int sex) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);

        // Which row to update, based on the title
        String selection = KEY_DATE + " LIKE ?";
        String[] selectionArgs = { date };

        int count = db.update(
                TABLE_CALENDAR,
                values,
                selection,
                selectionArgs);
        System.out.println("count "+count);
        if(count == 0){
            values.put(KEY_SEX, sex);

            long newRowId = db.insert(TABLE_CALENDAR, null, values);
            System.out.println("newRowId "+newRowId);
        }
    }

    /*
     * SELECT QUERIES
     */

    public int getValue(String date) {


        SQLiteDatabase db = this.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                KEY_SEX
        };

// Filter results WHERE "title" = 'My Title'
        String selection = KEY_DATE + " = ?";
        String[] selectionArgs = { date };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                KEY_DATE + " DESC";

        Cursor cursor = db.query(
                TABLE_CALENDAR,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            int itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(KEY_SEX));
            itemIds.add(itemId);
        }
        cursor.close();
        for(Object item : itemIds) {
            System.out.println("HEREEEE " + item);
        }
        return 1;
    }


    public String getTableAsString(String tableName) {
        tableName = TABLE_CALENDAR;
        SQLiteDatabase db = this.getReadableDatabase();
        //Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        return tableString;
    }


}




    /*

    public ArrayList<String> getAmtFromName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> returnList = new ArrayList();
        ArrayList<String> tempList = new ArrayList();

        Cursor c = db.rawQuery("SELECT " + KEY_NAME + " FROM " + TABLE_DRINKS, null);

        if(c.moveToFirst()){
            do{
                tempList.add(c.getString(0));
            }while(c.moveToNext());
        }

        c.close();

        String columns = "";
        for(int drink = 0; drink < tempList.size(); drink++){
            columns += tempList.get(drink);
            if(drink < tempList.size() -1 ) {
                columns += ",";
            }
        }

        Cursor d = db.rawQuery("SELECT " + columns + " FROM " + TABLE_MECHANICS + " WHERE " + KEY_NAME + " = 'bob'", null);
        if (d.moveToFirst()) {
            String[] columnNames = d.getColumnNames();
            do {
                for (String col : columnNames) {
                    returnList.add(d.getString(0));
                    System.out.println("get int for " + col + "  " + (d.getInt(d.getColumnIndex(col))));

                }
            } while (d.moveToNext());
        }

        d.close();
        db.close();
        return returnList;
    }

*/



    /*
     * UPDATE QUERIES
     */
/*
    public void updateNameInTable(String table, String id, String newId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, newId);
        db.update(table, values, KEY_NAME + " = '" + id + "'", null);
    }

    public void updateAmtForName(String drink, int amt, String mechanic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("[" + drink + "]", amt);
        db.update(TABLE_MECHANICS, values, KEY_NAME + " = '" + mechanic + "'", null);
    }

    public void updateTotalAmtForDrink(String drink, int amt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMT, amt);
        db.update(TABLE_DRINKS, values, KEY_NAME + " = '" + drink + "'", null);
    }

    public String getTableAsString(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            //do {
            for (String name : columnNames) {
                System.out.println("name" +name);
                //tableString += String.format("%s: %s\n", name,
                ///        allRows.getString(allRows.getColumnIndex(name)));
            }
            tableString += "\n";

            //} while (allRows.moveToNext());
        }
        return tableString;
    }

*/

/*
    public String getTableAsString(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        return tableString;
    }

*/
    /*
     * ALTER TABLE QUERIES
     */
/*

    public void addDrinkColumn(String drink){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("ALTER TABLE " + TABLE_MECHANICS + " ADD COLUMN [" + drink + "] INTEGER DEFAULT 0");
        db.close();
    }


    public void deleteColumnInTable(String columnToEdit, String table ){

        SQLiteDatabase db = this.getWritableDatabase();


        ArrayList<String> columnsList = new ArrayList<>();

        System.out.println("column to edit "+columnToEdit);
        Cursor c = db.rawQuery("SELECT * FROM " + table, null);
        if (c.moveToFirst()) {
            String[] columnNames = c.getColumnNames();

            for (String col : columnNames) {
                System.out.println("column : " +col);
                if(!col.equals(columnToEdit)){
                    columnsList.add(col);
                    System.out.println("not this column: " + col );
                }

            }
        }

        String columns = "";

        for(int i = 0; i<columnsList.size(); i++){
            columns += ("["+columnsList.get(i)+"]");
            if (i < columnsList.size() - 1){
                columns += ",";
            }
        }
        System.out.println("columns : "+ columns);


        String renameTable = "ALTER TABLE + " + table + " + RENAME TO original_table;" ;
        db.execSQL(renameTable);
        String createNewTable = "CREATE TABLE " + table + "(" + columns + ")";
        db.execSQL(createNewTable);
        String copyFromOldTable = "INSERT INTO " + table + "(" + columns + ") SELECT " + columns + "FROM original_table";
        db.execSQL(copyFromOldTable);
        String dropOldTable = "DROP TABLE original_table";
        db.execSQL(dropOldTable);
        db.close();

*/
        /*
        CREATE TABLE team(Name TEXT, Coach TEXT, City TEXT)
        You later realize that the City column ought to instead by called Location.

        Step 1: Rename the original table:
        ALTER TABLE team RENAME TO team_orig;

        Step 2: Create the replacement table with the original name and corrected column name:

        CREATE TABLE team(Name TEXT, Coach TEXT, Location TEXT);
        Step 3: Copy the data from the original table to the new table:

        INSERT INTO team(Name, Coach, Location) SELECT Name, Coach, City FROM team_orig;
        Note: The above command should be all one line.
        Step 4: Drop the original table:

        DROP TABLE team_orig;
        */
        /*

    }

    public void editColumnInTable(String columnToEdit, String newColumnName,String table ){

        SQLiteDatabase db = this.getWritableDatabase();


        ArrayList<String> columnsList = new ArrayList<>();

        System.out.println("column to edit "+columnToEdit);
        Cursor c = db.rawQuery("SELECT * FROM " + table, null);
        if (c.moveToFirst()) {
            String[] columnNames = c.getColumnNames();

            for (String col : columnNames) {
                System.out.println("column : " +col);

                columnsList.add(col);


            }
        }

        String oldColumns = "";
        String newColumns = "";

        for(int i = 0; i<columnsList.size(); i++){

            oldColumns += ("["+columnsList.get(i)+"]");
            if(columnsList.get(i).equals(columnToEdit)){

                newColumns += ("["+newColumnName+"]");
            }
            else{

                newColumns += ("["+columnsList.get(i)+"]");
            }
            if (i < columnsList.size() - 1){
                oldColumns += ",";
                newColumns += ",";
            }
        }
        System.out.println("oldColumns : "+ oldColumns);

        System.out.println("newColumns : "+ newColumns);


        String renameTable = "ALTER TABLE " + table + " RENAME TO original_table;" ;
        db.execSQL(renameTable);
        String createNewTable = "CREATE TABLE " + table + "(" + newColumns + ")";
        db.execSQL(createNewTable);
        String copyFromOldTable = "INSERT INTO " + table + "(" + newColumns + ") SELECT " + oldColumns + "FROM original_table";
        db.execSQL(copyFromOldTable);
        String dropOldTable = "DROP TABLE original_table";
        db.execSQL(dropOldTable);
        db.close();

*/
        /*
        CREATE TABLE team(Name TEXT, Coach TEXT, City TEXT)
        You later realize that the City column ought to instead by called Location.

        Step 1: Rename the original table:
        ALTER TABLE team RENAME TO team_orig;

        Step 2: Create the replacement table with the original name and corrected column name:

        CREATE TABLE team(Name TEXT, Coach TEXT, Location TEXT);
        Step 3: Copy the data from the original table to the new table:

        INSERT INTO team(Name, Coach, Location) SELECT Name, Coach, City FROM team_orig;
        Note: The above command should be all one line.
        Step 4: Drop the original table:

        DROP TABLE team_orig;
        */
        /*
    }


    //TODO MAJOR TODO HANDLE DELETE TABLE
    // HANDLE DELETE TABLE
    public void deleteAllValuesInTable(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + table);
        String CREATE_NEW_TABLE = "CREATE TABLE " + table + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PRICE + " INTEGER DEFAULT 0,"
                + KEY_AMT + " INTEGER DEFAULT 0)";

        db.execSQL(CREATE_NEW_TABLE);


        System.out.println(getTableAsString(table));

        Cursor c = db.rawQuery("SELECT * FROM " + table, null);
        if (c.moveToFirst()) {
            String[] columnNames = c.getColumnNames();

            for (String name : columnNames) {
                System.out.println("name" + name);
                //tableString += String.format("%s: %s\n", name,
                ///        allRows.getString(allRows.getColumnIndex(name)));
            }
        }






        db.close();
*/
        /*

        db.execSQL("CREATE TEMPORARY TABLE t1_backup(a,b);
        INSERT INTO t1_backup SELECT a,b FROM t1;
        DROP TABLE t1;
        CREATE TABLE t1(a,b);
        INSERT INTO t1 SELECT a,b FROM t1_backup;
        DROP TABLE t1_backup;


*/
/*


    }
}

*/