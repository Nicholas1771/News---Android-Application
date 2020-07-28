package com.example.finalproject;

import android.provider.BaseColumns;

class ArticlesContract {

    //private constructor for contract
    private ArticlesContract () {}

    //this class represents the table structure
    static class ArticlesEntry implements BaseColumns {

        //table name
        static final String TABLE_NAME = "favourite_articles";

        //table columns
        static final String _ID = "id";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DESCRIPTION = "description";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_LINK = "link";
    }
}
