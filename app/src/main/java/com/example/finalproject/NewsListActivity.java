package com.example.finalproject;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

public class NewsListActivity extends AppCompatActivity {

    //All articles
    private ArrayList<Article> allArticles;

    //List of articles that are not hidden
    private ArrayList<Article> unHiddenArticles;

    //Listview that stores the articles
    ListView newsArticleList;

    //This method takes a string search term and searches through the articles, it returns a list of articles that are matching
    private void searchArticles(String searchText) {

        //converts the search text to all lower case letters
        searchText = searchText.toLowerCase();

        //This loops through all of the articles
        for (int i = 0; i < allArticles.size(); i++) {

            //Saves current article in loop
            Article article = allArticles.get(i);

            //Title of the article
            String title = article.getTitle().toLowerCase();

            //Description of the article
            String description = article.getDescription().toLowerCase();

            //Date of the article
            String date = article.getDate();

            //Checks if the search text matches the articles title, description or date, and adds it to the list if its a match
            if (title.contains(searchText)) {
                article.unhide();
            } else if (description.contains(searchText)) {
                article.unhide();
            } else if (date.contains(searchText)) {
                article.unhide();
            } else {
                article.hide();
            }
        }

        //updates the unhiddenArticles
        unHiddenArticles = getUnhiddenArticles();

        //Toast message
        String toastMessage = "Found " + unHiddenArticles.size() + " search results";
        int duration = Toast.LENGTH_SHORT;

        //Toast displays the number of search results
        Toast.makeText(getApplicationContext(), toastMessage, duration).show();
    }

    //This loops through all articles and returns a list of unhidden articles
    private ArrayList<Article> getUnhiddenArticles () {

        //list to be returned
        ArrayList<Article> unhiddenArticles = new ArrayList<>();

        //loops through all articles
        for (int i = 0; i < allArticles.size(); i++) {

            //Gets current article in the loop
            Article article = allArticles.get(i);

            //Checks if the article is hidden
            if (!article.getHidden()) {
                //adds the unhidden article to the list
                unhiddenArticles.add(article);
            }
        }

        //returns the list of unhidden articles
        return unhiddenArticles;
    }

    //This method takes an arraylist of articles and initialized the articles and displays them
    private void updateArticles () {

        //Updates the unhidden articles
        unHiddenArticles = getUnhiddenArticles();

        //News article list view
        newsArticleList = findViewById(R.id.list_view);

        //news list adapter object
        NewsListAdapter newsListAdapter = new NewsListAdapter(this, unHiddenArticles);
        newsArticleList.setAdapter(newsListAdapter);
        //Runs when an item is clicked
        newsArticleList.setOnItemClickListener((list, item, position, id) -> {

            //Gets the currently clicked Article object and saves it for use
            Article clickedArticle = unHiddenArticles.get(position);

            //builder for an alert
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewsListActivity.this);

            //Link to article
            String link = clickedArticle.getLinkToArticle();

            //This alert opens when a news article title is clicked. It shows the articles title, description, date, and link
            alertDialogBuilder.setTitle("Article Information")
                    .setMessage(Html.fromHtml("Title: " + clickedArticle.getTitle()
                            + "<br><br>Description: " + clickedArticle.getDescription()
                            + "<br><br>Date: " + clickedArticle.getDate()
                            + "<br><br>Link: <a href=\"" + link + "\">" + link + "</a>"));
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

            //Source from: https://essential-android.programming-books.io/alert-dialog-containing-a-clickable-link-9db7e0c7a32b4f84bb41150d06f418a9
            ((TextView) Objects.requireNonNull(alert.findViewById(android.R.id.message))).setMovementMethod(LinkMovementMethod.getInstance());
        });

        //Runs on a long click of a news article, this will be used to show the snackbar to hide the article, or favourite the article
        newsArticleList.setOnItemLongClickListener((list, item, position, id) -> {

            //Creates snackbar to hide the article
            Snackbar snackbar = Snackbar.make(newsArticleList, "Hide this article?", Snackbar.LENGTH_LONG);

            //This action will hide the article if confirmed
            snackbar.setAction("Confirm", v -> {
                unHiddenArticles.get(position).hidePermanently();
                updateArticles();
            });

            //Shows the snackbar
            snackbar.show();

            //returns true so that the clickListener does not trigger
            return true;
        });

        //The search button
        Button searchButton = findViewById(R.id.search_button);

        //This listener waits for the search button to be clicked, and the searches for articles
        searchButton.setOnClickListener(v -> {

            //Gets the edit text where the search text is stored
            EditText searchEditText = findViewById(R.id.search_edit_text);

            //Gets the search text
            String searchText = searchEditText.getText().toString();

            //Calls the search articles method and passes it the search text
            searchArticles(searchText);

            //updates the articles once the search has ran
            updateArticles();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        //initialize the articles
        allArticles = new ArrayList<>();
        //Manually add articles for now
        //TODO pull articles from http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml instead of manually creating them
        allArticles.add(new Article(
                "Naya Rivera: Police identify body as missing Glee star",
                "The actress went missing on Wednesday after going boating with her son at a lake in California.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/entertainment-arts-53397318"));
        allArticles.add(new Article("Coronavirus: White House targets US disease chief Dr Anthony Fauci",
                "A memo leaked over the weekend said several officials were \"concerned\" by Dr Fauci's past comments.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53392817"));
        allArticles.add(new Article("Trudeau admits 'mistake' while facing third ethics inquiry in office",
                "Canada PM says he should have recused himself from contract talks with charity that paid his family.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53394272"));
        allArticles.add(new Article("Roger Stone: President Trump's clemency wipes fine and supervised release",
                "The president's former adviser was due to begin a prison term on 14 July after lying to Congress.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53398575"));
        allArticles.add(new Article("South China Sea dispute: China's pursuit of resources 'unlawful', says US",
                "Secretary of State Mike Pompeo says China is treating the disputed waters as its \"maritime empire\".",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53397673"));
        allArticles.add(new Article("Washington Redskins to drop controversial team name following review",
                "The Washington team's decision follows a wave of calls to scrap the name long-criticised as racist.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53390944"));
        allArticles.add(new Article("Daniel Lewis Lee: US judge delays first federal execution in 17 years",
                "The execution of Daniel Lewis Lee in Indiana is stopped just hours before it was due to go ahead.",
                "2020-07-13",
                "https://www.bbc.co.uk/news/world-us-canada-53385642"));

        //List View object for the list view from news_list.xml
        updateArticles();
    }
}
