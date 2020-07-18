package com.kakosepise.test.kakosepise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String m_ENTRY_TABLE_NAME = "kakosepise";
    public static final String m_ID = "ID";
    public static final String m_DATE = "date";
    public static final String m_MODIFY_DATE = "modified";
    public static final String m_POST_CONTENT = "post_content";
    public static final String m_POST_TITLE = "post_title";
    public static final String m_POST_NAME = "post_name";
    public static final String m_INIT_PATH = "database/dataInit.sql";
    public static final String m_DB_FILE_PATH = "kakosepise.db";
    public static final String LOCAL_DB_FILE_PATH = "localDatabase.db";
    public static final Date m_LAST_UPDATE = new Date();

    public DatabaseController(@Nullable Context _context) {
        super(_context, m_DB_FILE_PATH, null, 1);
    }


    // Called when the database is first generated, contains sqlite statements that
    // generate the base tables
    @Override
    public void onCreate(SQLiteDatabase _sqLiteDatabase) {
        // Making the kakosepise table
        String createTableStatement = "CREATE TABLE " + m_ENTRY_TABLE_NAME + " (\n" +
                "\t\"" + m_ID + "\"\tINTEGER NOT NULL UNIQUE,\n" +
                "\t\"" + m_POST_CONTENT + "\"\tTEXT NOT NULL,\n" +
                "\t\"" + m_POST_TITLE + "\"\tTEXT NOT NULL,\n" +
                "\t\"" + m_POST_NAME + "\"\tTEXT NOT NULL DEFAULT '',\n" +
                "\tPRIMARY KEY(\"" + m_ID + "\")\n" +
                ");";
        _sqLiteDatabase.execSQL(createTableStatement);


        // Injecting starting values stored in dataInit.sql
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(m_INIT_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            while (scanner.hasNextLine()) {
                String nextSqlStatement = scanner.nextLine().trim();
                _sqLiteDatabase.execSQL(nextSqlStatement);
            }
        }

    }

    // Called when version number of application changes, used for forward compatibility
    @Override
    public void onUpgrade(SQLiteDatabase _sqLiteDatabase, int _oldVersion, int _newVersion) {
//        String sql = "DROP TABLE IF EXISTS " + m_ENTRY_TABLE_NAME;
//        _sqLiteDatabase.execSQL(sql);
//        onCreate(_sqLiteDatabase);
    }


    // Adds a single row into the database
    public boolean addEntry(Entry _entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(m_ID, _entry.getM_ID());
        cv.put(m_POST_CONTENT, _entry.getM_post_content());
        cv.put(m_POST_NAME, _entry.getM_post_name());
        cv.put(m_POST_TITLE, _entry.getM_post_title());

        // Returns true if the insert was successful
        return db.insert(m_ENTRY_TABLE_NAME, null, cv) != -1;
    }

    // TODO: Add REST API call for fetching json response
    // Updates database, return value is success indicator
    public boolean updateDatabase() {
        return true;
    }

    public boolean execCommand(String sqlCommand) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlCommand);
        db.close();
        return true;
    }


    public List<Entry> getAllEntries() {
        List<Entry> returnList = new ArrayList<>();

        // Make get-all querry
        String queryString = "select * from " + m_ENTRY_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        // If there are results, loop while there is a next entry in the database
        if (cursor.moveToFirst()) {
            do {
                // Save
                int id = cursor.getInt(0);
                String content = cursor.getString(1);
                String title = cursor.getString(2);
                String name = cursor.getString(3);

                Entry entry = new Entry(id, content, title, name);
                returnList.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    // Method that updates existing entry in local db with new values
    public boolean updateEntry(Entry _newEntry) {
        int updates = 0;
        ContentValues cv = new ContentValues();
        cv.put(m_POST_CONTENT, _newEntry.getM_post_content());
        cv.put(m_POST_TITLE, _newEntry.getM_post_title());
        cv.put(m_POST_NAME, _newEntry.getM_post_name());

        SQLiteDatabase db = this.getWritableDatabase();
        String idString = Integer.toString(_newEntry.getM_ID());

        updates = db.update(m_ENTRY_TABLE_NAME, cv, m_ID + " = ?", new String[]{idString});

        db.close();
        return updates > 0;
    }

    public boolean processJson(String _jsonString) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        jsonParser.parse(_jsonString);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            Object obj = jsonParser.parse(_jsonString);
            JSONArray array = (JSONArray) obj;
            int arraySize = array.length();
            for (int i = 0; i < arraySize; i++) {
                JSONObject tmpObj = (JSONObject) array.get(i);

                // Getting and formatting the next element of JSON array
                int tmpID = (int) tmpObj.get(m_ID);
                String tmpContent = (String) tmpObj.get(m_POST_CONTENT);
                String tmpTitle = (String) tmpObj.get(m_POST_TITLE);
                String tmpName = (String) tmpObj.get(m_POST_NAME);

                // Creating entry
                Entry tmpEntry = new Entry(tmpID, tmpContent, tmpTitle, tmpName);

                String tmpPostDateString = (String) tmpObj.get(m_DATE);
                String tmpModifyDateString = (String) tmpObj.get(m_MODIFY_DATE);
                Date tmpPostDate = format.parse(tmpPostDateString);
                Date tmpModifyDate = format.parse(tmpModifyDateString);

                // Current date - date of update
                Date currentDate = new Date();

                // Check if the entry needs to be updated or added
                if (tmpPostDate.after(m_LAST_UPDATE)) {
                    addEntry(tmpEntry);
                } else {
                    updateEntry(tmpEntry);
                }


            }

        } catch (ParseException | JSONException | java.text.ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    // Method that updates multiple existing entries in local db with new values
    // TODO: Second loop a bit janky, implement in one
    public boolean updateEntries(List<Entry> _newEntries) {
        int updates = 0;
        List<ContentValues> cvs = new ArrayList<ContentValues>();
        int numExpectedUpdates = _newEntries.size();
        String[] idStrings = new String[numExpectedUpdates];


        for (int i = 0; i < numExpectedUpdates; i++) {
            cvs.get(i).put(m_POST_CONTENT, _newEntries.get(i).getM_post_content());
            cvs.get(i).put(m_POST_TITLE, _newEntries.get(i).getM_post_title());
            cvs.get(i).put(m_POST_NAME, _newEntries.get(i).getM_post_name());
            idStrings[i] = Integer.toString(_newEntries.get(i).getM_ID());
        }

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < numExpectedUpdates; i++) {
            updates += db.update(m_ENTRY_TABLE_NAME, cvs.get(i), m_ID + " = ?", idStrings);
        }

        db.close();
        return updates == numExpectedUpdates;
    }

    // Method that deletes an existing entry in local db
    public boolean deleteEntry(Entry _newEntry) {
        int deletes = 0;
        ContentValues cv = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();

        String stringId = Integer.toString(_newEntry.getM_ID());
        deletes += db.delete(m_ENTRY_TABLE_NAME, m_ID + " = ?", new String[]{stringId});

        db.close();
        return deletes == 1;
    }

    // Method that deletes an existing entry in local db
    public List<Entry> searchEntries(String searchWord) {
        SQLiteDatabase db = this.getReadableDatabase();

        String searchSql = "SELECT *\n" +
                "FROM " + m_ENTRY_TABLE_NAME + "\n" +
                "WHERE " + m_POST_NAME + " LIKE '%" + searchWord + "%'\n" +
                "ORDER BY\n" +
                "  CASE\n" +
                "    WHEN " + m_POST_NAME + " LIKE '" + searchWord + "%' THEN 1\n" +
                "    WHEN post_name LIKE '%" + searchWord + "' THEN 3\n" +
                "    ELSE 2\n" +
                "  END";

        Cursor cursor = db.rawQuery(searchSql, null);
        List<Entry> returnList = new ArrayList<>();
        // If there are results, loop while there is a next entry in the database
        if (cursor.moveToFirst()) {
            do {
                // Save
                int id = cursor.getInt(0);
                String content = cursor.getString(1);
                String title = cursor.getString(2);
                String name = cursor.getString(3);

                Entry entry = new Entry(id, content, title, name);
                returnList.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return returnList;
    }

    // Method that deletes multiple existing entries in local db
    public boolean deleteEntries(List<Entry> _newEntries) {
        int updates = 0;
        List<ContentValues> cvs = new ArrayList<ContentValues>();
        int numExpectedUpdates = _newEntries.size();
        String[] idStrings = new String[numExpectedUpdates];


        for (int i = 0; i < numExpectedUpdates; i++) {
            idStrings[i] = Integer.toString(_newEntries.get(i).getM_ID());
        }

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < numExpectedUpdates; i++) {
            updates += db.delete(m_ENTRY_TABLE_NAME, m_ID + " = ?", idStrings);
        }

        db.close();
        return updates == numExpectedUpdates;
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    public boolean isFilled() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.getPageSize();
        long count = DatabaseUtils.queryNumEntries(db, m_ENTRY_TABLE_NAME);
        db.close();

        return count > 2;
    }
}
