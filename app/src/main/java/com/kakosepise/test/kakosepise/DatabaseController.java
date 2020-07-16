package com.kakosepise.test.kakosepise;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String m_KAKOSEPISE_TABLE = "KAKOSEPISE_TABLE";
    public static final String m_ID = "ID";
    public static final String m_POST_CONTENT = "post_content";
    public static final String m_POST_TITLE = "post_title";
    public static final String m_POST_NAME = "post_name";

    public DatabaseController(@Nullable Context _context) {
        super(_context, "entry.db", null, 1);
    }

    // Called when the database is first generated, contains sqlite statemets that
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

    public boolean addEntry(Entry _entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(m_ID, _entry.getM_ID());
        cv.put(m_POST_CONTENT, _entry.getM_post_content());
        cv.put(m_POST_NAME, _entry.getM_post_name());
        cv.put(m_POST_TITLE, _entry.getM_post_title());

        // Returns true if the insert was successful
        return db.insert(m_KAKOSEPISE_TABLE, null, cv)!=-1;
    }
}
