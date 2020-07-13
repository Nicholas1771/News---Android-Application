package com.example.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsListAdapter extends BaseAdapter {

    //This variable stores the Activity passed to the constructor
    private Activity context;

    //This list stores all of the articles
    private ArrayList<Article> articles;

    NewsListAdapter(Activity context, ArrayList<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    public int getCount() {
        //returns the size of the arraylist
        return articles.size();
    }

    public Object getItem (int position) {
        //returns the object with index of position
        return articles.get(position);
    }

    public long getItemId (int position) {
        //Database will be created for milestone 3
        //For now returning back the position
        return position;
    }

    //returns the newview after inflating and setting the text
    public View getView (int position, View old, ViewGroup parent) {
        View newView = old;

        if (newView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //inflates the news_article layout to the newView
            newView = inflater.inflate(R.layout.news_article, parent, false);
        }

        //references the news_article layout and its title text
        //then sets the title text to the Article objects title in index position
        TextView newsArticleTitleText = newView.findViewById(R.id.article_title);
        newsArticleTitleText.setText(articles.get(position).getTitle());

        //returns the new view
        return newView;
    }

}
