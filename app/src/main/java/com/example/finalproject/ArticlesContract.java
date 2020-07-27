package com.example.finalproject;

import android.provider.BaseColumns;

public class ArticlesContract {

    private ArticlesContract () {}

    public static class ArticlesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourite_articles";
        public static final String _ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LINK = "link";
    }
}
