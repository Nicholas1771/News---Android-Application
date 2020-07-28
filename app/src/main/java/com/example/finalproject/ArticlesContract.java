package com.example.finalproject;

import android.provider.BaseColumns;

class ArticlesContract {

    private ArticlesContract () {}

    static class ArticlesEntry implements BaseColumns {
        static final String TABLE_NAME = "favourite_articles";
        static final String _ID = "id";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DESCRIPTION = "description";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_LINK = "link";
    }
}
