package com.example.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
        //This variable stores the Activity passed to the constructor
        private Activity context;

        //This list stores all of the articles
        private ArrayList<String> searchHistory;

        HistoryAdapter (Activity context, ArrayList<String> searchHistory) {
            this.context = context;
            this.searchHistory = searchHistory;
        }

        public int getCount() {
            //returns the size of the arraylist
            return searchHistory.size();
        }

        public Object getItem (int position) {
            //returns the object with index of position
            return searchHistory.get(position);
        }

        public long getItemId (int position) {
            return position;
        }

        //returns the newview after inflating and setting the text
        public View getView (int position, View old, ViewGroup parent) {
            View newView = old;

            if (newView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                //inflates the news_article layout to the newView
                newView = inflater.inflate(R.layout.search_history, parent, false);
            }

            //references the news_article layout and its title text
            //then sets the title text to the Article objects title in index position
            TextView searchText = newView.findViewById(R.id.search_text);
            searchText.setText(searchHistory.get(position));

            //returns the new view
            return newView;
        }


}
