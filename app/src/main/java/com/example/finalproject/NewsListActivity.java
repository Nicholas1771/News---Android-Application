package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {

    //Mock articles used to test the list view
    private ArrayList<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        //initialize the articles
        articles = new ArrayList<>();
        //Manually add articles for now
        //TODO pull articles from http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml instead of manually creating them
        articles.add(new Article(
                "Naya Rivera: Police identify body as missing Glee star",
                "The actress went missing on Wednesday after going boating with her son at a lake in California.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/entertainment-arts-53397318"));
        articles.add(new Article("Coronavirus: White House targets US disease chief Dr Anthony Fauci",
                "A memo leaked over the weekend said several officials were \"concerned\" by Dr Fauci's past comments.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53392817"));
        articles.add(new Article("Trudeau admits 'mistake' while facing third ethics inquiry in office",
                "Canada PM says he should have recused himself from contract talks with charity that paid his family.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53394272"));
        articles.add(new Article("Roger Stone: President Trump's clemency wipes fine and supervised release",
                "The president's former adviser was due to begin a prison term on 14 July after lying to Congress.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53398575"));
        articles.add(new Article("South China Sea dispute: China's pursuit of resources 'unlawful', says US",
                "Secretary of State Mike Pompeo says China is treating the disputed waters as its \"maritime empire\".",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53397673"));
        articles.add(new Article("Washington Redskins to drop controversial team name following review",
                "The Washington team's decision follows a wave of calls to scrap the name long-criticised as racist.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53390944"));
        articles.add(new Article("Daniel Lewis Lee: US judge delays first federal execution in 17 years",
                "The execution of Daniel Lewis Lee in Indiana is stopped just hours before it was due to go ahead.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53385642"));

        //List View object for the list view from news_list.xml
        ListView newsArticleList = findViewById(R.id.list_view);
        NewsListAdapter newsListAdapter = new NewsListAdapter(this, articles);
        newsArticleList.setAdapter(newsListAdapter);

        //Runs when an item is clicked
        newsArticleList.setOnItemClickListener((list, item, position, id) -> {

            //Gets the currently clicked Article object and saves it for use
            Article clickedArticle = articles.get(position);

            //builder for an alert
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewsListActivity.this);

            //TODO open up web browser when link is clicked
            //This alert opens when a news article title is clicked. It shows the articles title, description, date, and link
            alertDialogBuilder.setTitle("Article Information")
                    .setMessage("Title: " + clickedArticle.getTitle()
                    + "\n\nDescription: " + clickedArticle.getDescription()
                    + "\n\nDate: " + clickedArticle.getDate()
                    + "\n\nLink: " + clickedArticle.getLinkToArticle())
                    .create()
                    .show();
        });
    }

}
