package com.kakosepise.test.kakosepise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String m_ENTRY_TABLE_NAME = "kakosepise";
    public static final String m_ID = "ID";
    public static final String m_POST_CONTENT = "post_content";
    public static final String m_POST_TITLE = "post_title";
    public static final String m_POST_NAME = "post_name";
    public static final String m_INIT_PATH = "database/dataInit.sql";
    public static final String DB_FILE_PATH = "kakosepise.db";
    public static final String LOCAL_DB_FILE_PATH = "localDatabase.db";

    public DatabaseController(@Nullable Context _context) {
        super(_context, DB_FILE_PATH, null, 1);
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
        if (scanner!=null) {
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

        return count>2;
    }
}
