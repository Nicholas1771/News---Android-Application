package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ArticlesDbHelper extends SQLiteOpenHelper {

    //database version
    private static final int DATABASE_VERSION = 1;

    //database name
    private static final String DATABASE_NAME = "NewsApp.db";

    // create table sql query
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ArticlesContract.ArticlesEntry.TABLE_NAME + " (" +
                    ArticlesContract.ArticlesEntry._ID + " INTEGER PRIMARY KEY," +
                    ArticlesContract.ArticlesEntry.COLUMN_NAME_TITLE + " TEXT," +
                    ArticlesContract.ArticlesEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ArticlesContract.ArticlesEntry.COLUMN_NAME_DATE + " DATE," +
                    ArticlesContract.ArticlesEntry.COLUMN_NAME_LINK + " TEXT)";

    // drop table query
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ArticlesContract.ArticlesEntry.TABLE_NAME;

    //constructor
    ArticlesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create the database
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //called when new version
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
