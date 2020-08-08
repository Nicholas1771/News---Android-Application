package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

class Database {

  // ArticlesDBHelper object
    private ArticlesDbHelper dbHelper;

    //database
    private SQLiteDatabase db;

    Database(Context context) {
        dbHelper = new ArticlesDbHelper(context);
    }

    // this method checks if Database has article
    boolean hasArticle(Article article) {

        //gets a readable database
        db = dbHelper.getReadableDatabase();

        //link to the article
        String link = article.getLinkToArticle();

        //gets articles from database that matches the link
        Cursor cursor = db.rawQuery("select * from " + ArticlesContract.ArticlesEntry.TABLE_NAME + " where link='" + link + "';", null);

        //if there is no articles matching, return false otherwise return true
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    // this method removes the Article with the link given from the database
    void removeArticle (String link) {

        //gets a writable database
        db = dbHelper.getWritableDatabase();

        //SQL to remove the article containing that link
        String SQL = "delete from " + ArticlesContract.ArticlesEntry.TABLE_NAME + " where link='" + link + "';";
        db.execSQL(SQL);
        db.close();
    }

    // this method inserts Articles to the database
    void insertArticle (Article article) {

        //content values to insert into database
        ContentValues values = new ContentValues();

        // stores all in the ContentValues
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_TITLE, article.getTitle());
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_DESCRIPTION, article.getDescription());
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_DATE, article.getDate());
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_LINK, article.getLinkToArticle());

        // inserts row to database
        db.insert(ArticlesContract.ArticlesEntry.TABLE_NAME, null, values);
    }

    // this method returns the favorite articles in an ArrayList
    ArrayList<Article> getFavouriteArticles () {

        // ArrayList of favorite Articles
        ArrayList<Article> favouriteArticles = new ArrayList<>();

        //gets readable database
        db = dbHelper.getReadableDatabase();

        //gets all rows from the database
        String ALL_FAVOURITE_ROWS_SQL = "select * from " + ArticlesContract.ArticlesEntry.TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(ALL_FAVOURITE_ROWS_SQL, null);

        //loops while there are more articles
        while (cursor.moveToNext()) {

            //gets article information from the database
            String title = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_DATE));
            String link = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_LINK));

            // creates article
            Article article = new Article(title, description, date, link);

            // adds article to to ArrayList
            favouriteArticles.add(article);
        }

        cursor.close();

        // returns the ArrayList of favorite Articles
        return favouriteArticles;
    }
}
