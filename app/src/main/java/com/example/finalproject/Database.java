package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class Database {

    ArticlesDbHelper dbHelper;

    SQLiteDatabase db;

    private final String ALL_FAVOURITE_ROWS_SQL = "select * from " + ArticlesContract.ArticlesEntry.TABLE_NAME + ";";

    public Database (Context context) {
        dbHelper = new ArticlesDbHelper(context);
    }

    public boolean hasArticle (Article article) {

        db = dbHelper.getReadableDatabase();

        String link = article.getLinkToArticle();

        Cursor cursor = db.rawQuery("select * from " + ArticlesContract.ArticlesEntry.TABLE_NAME + " where link='" + link + "';", null);

        if (cursor.getCount() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void removeArticle (String link) {
        db = dbHelper.getWritableDatabase();
        String SQL = "delete from " + ArticlesContract.ArticlesEntry.TABLE_NAME + " where link='" + link + "';";
        db.execSQL(SQL);
        db.close();
    }

    public void insertArticle (Article article) {
        ContentValues values = new ContentValues();

        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_TITLE, article.getTitle());
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_DESCRIPTION, article.getDescription());
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_DATE, article.getDate());
        values.put(ArticlesContract.ArticlesEntry.COLUMN_NAME_LINK, article.getLinkToArticle());

        db.insert(ArticlesContract.ArticlesEntry.TABLE_NAME, null, values);
    }

    public ArrayList<Article> getFavouriteArticles () {

        ArrayList<Article> favouriteArticles = new ArrayList<>();

        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(ALL_FAVOURITE_ROWS_SQL, null);

        Log.i("test", "" + cursor.getCount());

        while (cursor.moveToNext()) {

            String title = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_DATE));
            String link = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_NAME_LINK));

            Article article = new Article(title, description, date, link);

            favouriteArticles.add(article);
            Log.i("test", "added article");
        }

        cursor.close();

        return favouriteArticles;
    }
}
