package com.kakosepise.test.kakosepise;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String m_KAKOSEPISE_TABLE = "KAKOSEPISE_TABLE";
    public static final String m_ID = "ID";
    public static final String m_POST_CONTENT = "post_content";
    public static final String m_POST_TITLE = "post_title";
    public static final String m_POST_NAME = "post_name";

    public DatabaseController(@Nullable Context _context) {
        super(_context, "database.db", null, 1);
    }

    // Called when the database is first generated, contains sqlite statements that
    // generate the base tables
    @Override
    public void onCreate(SQLiteDatabase _sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + m_KAKOSEPISE_TABLE + " (\n" +
                "\t\"" + m_ID + "\"\tINTEGER NOT NULL UNIQUE,\n" +
                "\t\"" + m_POST_CONTENT + "\"\tTEXT NOT NULL,\n" +
                "\t\"" + m_POST_TITLE + "\"\tTEXT NOT NULL,\n" +
                "\t\"" + m_POST_NAME + "\"\tTEXT NOT NULL DEFAULT '',\n" +
                "\tPRIMARY KEY(\"" + m_ID + "\")\n" +
                ");";
        _sqLiteDatabase.execSQL(createTableStatement);

    }

    // Called when version number of application changes, used for forward compatibility
    @Override
    public void onUpgrade(SQLiteDatabase _sqLiteDatabase, int _i, int _i1) {

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
        return db.insert(m_KAKOSEPISE_TABLE, null, cv) != -1;
    }

    // TODO: Add REST API call for fetching json response
    // Updates database, return value is success indicator
    public boolean updateDatabase() {
        return true;
    }

    public List<Entry> getAllEntries() {
        List<Entry> returnList = new ArrayList<>();

        // Make get-all querry
        String queryString = "select * from " + m_KAKOSEPISE_TABLE;

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
        } else {
            // failure, do not add anything to the list
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

        updates = db.update(m_KAKOSEPISE_TABLE, cv, m_ID + " = ?", new String[]{idString});

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
            updates += db.update(m_KAKOSEPISE_TABLE, cvs.get(i), m_ID + " = ?", idStrings);
        }

        db.close();
        return updates == numExpectedUpdates;
    }

}
