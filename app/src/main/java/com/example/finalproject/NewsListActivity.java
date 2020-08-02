package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NewsListActivity extends BaseActivity {
    //All articles
    private ArrayList<Article> allArticles;

    //List of articles that are not hidden
    private ArrayList<Article> unHiddenArticles;

    private Database database;

    private ArrayList<String> searchHistory;

    private SharedPreferences sharedPreferences;

    AutoCompleteTextView searchEditText;

    private final String SEARCH = "NEWS_SEARCH";

    //Listview that stores the articles
    ListView newsArticleList;

    @Override
    public int getLayoutResource() {
        return R.layout.news_list;
    }

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

    private void addArticleToFavourites (Article article) {
        if (database.hasArticle(article)) {
            Log.i("false", "false");
        } else {
            database.insertArticle(article);
            Log.i("true", "true");
        }
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
                            + "<br><br>Link: <a href=\"" + link + "\">" + link + "</a>"))
                    .setPositiveButton("Add to favourites", (dialog, which) -> addArticleToFavourites(clickedArticle));
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
    }

    private void addSearchHistory (String search) {
        Set<String> searchHistorySet = sharedPreferences.getStringSet(SEARCH, new HashSet<>());

        searchHistorySet.add(search);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet(SEARCH, searchHistorySet);

        editor.apply();

        updateAutoComplete();
    }

    private ArrayList<String> getSearchHistory () {

        HashSet<String> searchHistorySet = (HashSet<String>) sharedPreferences.getStringSet(SEARCH, new HashSet<>());

        return new ArrayList<>(searchHistorySet);
    }

    private void updateAutoComplete () {
        ArrayAdapter<String> adapter;

        searchHistory = getSearchHistory();

        adapter = new ArrayAdapter<>(NewsListActivity.this, android.R.layout.simple_dropdown_item_1line, searchHistory);

        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        database = new Database(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        searchEditText = findViewById(R.id.search_edit_text);

        updateAutoComplete();

        displayToolbar();
        setTitle("News");
        //initialize the articles
        allArticles = new ArrayList<>();
        //Manually add articles for now
        //TODO pull articles from http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml instead of manually creating them
        new ArticleQuery().execute();
        //List View object for the list view from news_list.xml
        updateArticles();

        //The search button
        Button searchButton = findViewById(R.id.search_button);

        //This listener waits for the search button to be clicked, and the searches for articles
        searchButton.setOnClickListener(v -> {
            //Gets the search text
            String searchText = searchEditText.getText().toString();

            addSearchHistory(searchText);

            //Calls the search articles method and passes it the search text
            searchArticles(searchText);

            //updates the articles once the search has ran
            updateArticles();
        });
    }
    //Async class for making a query
    private class ArticleQuery extends AsyncTask<String, Integer, String> {

        //articles pulled from the xml page
        ArrayList<Article> articles = new ArrayList<>();

        @Override
        protected String doInBackground(String... args) {

            try {
                //url where we get the articles from
                URL url = new URL("http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

                //url connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //input stream
                InputStream response = urlConnection.getInputStream();

                //Sets up the xml pull parse factory and pull parser
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                //declare and initialize eventType
                int eventType = xpp.getEventType();

                //true if currently inside an article item
                boolean insideItem = false;

                //Temporary variables to store article information
                String title = "";
                String description = "";
                String date = "";
                String link = "";

                // looping document to extract title, description, link, date
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    //At a start tag
                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            //At the start of an article item, set inside item to true
                            insideItem = true;
                        }

                        //If were inside an article item, check whether were on the title, desc, date or link
                        if (insideItem) {
                            if (xpp.getName().equalsIgnoreCase("title")) {
                                //At the title tag, save title text to temp variable
                                title = xpp.nextText();
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                //At the description tag, save description text to temp variable
                                description = xpp.nextText();
                            } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                //At the date tag, save text to temp variable
                                date = xpp.nextText();
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                //At the link tag, save text to temp variable
                                link = xpp.nextText();
                            }
                        }
                    }

                    if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        //At the end of an article item set inside item to false
                        insideItem = false;

                        //Add the temp variables together to create a new Article object
                        articles.add(new Article(title, description, date, link));

                        //Publish progress to the progress bar (1 percent for every article)
                        publishProgress(articles.size());
                    }

                    //Go to next
                    eventType = xpp.next();

                }
                //Done getting articles publish progress to 100 percent
                publishProgress(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Done";
        }

        @Override
        protected void onProgressUpdate (Integer... values) {

            //Get the progress bar
            final ProgressBar progressBar = findViewById(R.id.progressBar);

            //Make the progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            //Set the progress
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Done getting all articles

            //add new articles to allArticle array list
            allArticles.addAll(articles);

            //Update the articles
            updateArticles();

            //Make progress bar invisible
            final ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //help menu for the activity
        if (item.getItemId() == R.id.help_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewsListActivity.this);
            builder.setTitle("Help")
                    .setMessage("Enter article and press search")
                    .setPositiveButton("OK", null);

            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
