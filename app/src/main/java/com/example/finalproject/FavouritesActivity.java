package com.example.finalproject;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;


import android.view.MenuItem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavouritesActivity extends BaseActivity {
    //All articles
    private ArrayList<Article> allArticles;

    //List of articles that are not hidden
    private ArrayList<Article> unHiddenArticles;

    private ArrayList<String> searchHistory;

    private boolean tracking;

    private SharedPreferences sharedPreferences;

    AutoCompleteTextView searchEditText;

    private final String SEARCH = "SEARCH";

    //Listview that stores the articles
    ListView newsArticleList;

    Database database;

    ImageView imageView;

    Bitmap image;

    AlertDialog alert;

    Article clickedArticle;

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
    private ArrayList<Article> getUnhiddenArticles() {

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
    private void updateArticles() {

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
            clickedArticle = unHiddenArticles.get(position);

            //execute the image query
            new ImageQuery().execute();
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

    // this method removes the favourite Article passed through
    public void removeFavourite (Article article) {

        //gets the article link
        String link = article.getLinkToArticle();

        //removes article from database
        database.removeArticle(link);

        //updates allArticles
        allArticles = database.getFavouriteArticles();

        //update articles
        updateArticles();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        //displays the toolbar
        displayToolbar();
        setTitle("Favourites");
        //initialize the articles
        allArticles = new ArrayList<>();

        database = new Database(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        searchEditText = findViewById(R.id.search_edit_text);

        updateAutoComplete();

        //gets favourites articles from database
        allArticles = database.getFavouriteArticles();

        tracking = getTrackHistory();

        //update the articles
        updateArticles();

        //The search button
        Button searchButton = findViewById(R.id.search_button);

        //This listener waits for the search button to be clicked, and the searches for articles
        searchButton.setOnClickListener(v -> {
            //Gets the edit text where the search text is stored
            EditText searchEditText = findViewById(R.id.search_edit_text);

            //Gets the search text
            String searchText = searchEditText.getText().toString();

            if (tracking) {
                addSearchHistory(searchText);
            }
            //Calls the search articles method and passes it the search text
            searchArticles(searchText);

            //updates the articles once the search has ran
            updateArticles();
        });
    }

    // help menu alert dialog
    public boolean onOptionsItemSelected(MenuItem item) {

        //if the help item was selected, display an alertdialogue with help information
        if (item.getItemId() == R.id.help_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FavouritesActivity.this);
            builder.setTitle("Help")
                    .setMessage("Enter article and press search")
                    .setPositiveButton("OK", null);

            //Show and create the alert dialogue
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean getTrackHistory () {
        return sharedPreferences.getBoolean("TRACK", false);
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

        adapter = new ArrayAdapter<>(FavouritesActivity.this, android.R.layout.simple_dropdown_item_1line, searchHistory);

        searchEditText.setAdapter(adapter);
        searchEditText.setThreshold(0);
    }

    //This method created an alert dialogue in cooperation with the async task
    private void showAlert () {

        //Gets the alert dialogue view
        LayoutInflater factory = LayoutInflater.from(FavouritesActivity.this);
        final View view = factory.inflate(R.layout.alert_dialogue_image, null);

        //sets the image to be shown
        imageView = view.findViewById(R.id.alert_image);
        imageView.setImageBitmap(image);

        //Text views for the article information
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);
        TextView date = view.findViewById(R.id.date);
        TextView link = view.findViewById(R.id.link);

        //Sets the text views text to the article information
        title.setText(clickedArticle.getTitle());
        description.setText(clickedArticle.getDescription());
        date.setText(clickedArticle.getDate());

        //link to article
        link.setText(Html.fromHtml("<a href=\"" + clickedArticle.getLinkToArticle() + "\">" + clickedArticle.getLinkToArticle()));

        //makes the link work
        link.setMovementMethod(LinkMovementMethod.getInstance());

        //alert dialgoue shows article information, image from search term, and has a button to add article to favourites database table
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavouritesActivity.this);
        alertDialogBuilder.setTitle("Article Information")
                .setView(view)
                .setPositiveButton("Remove from favourites", (dialog, which) -> removeFavourite(clickedArticle));
        alert = alertDialogBuilder.create();
        alert.show();
    }

    //This async task gets an image using the last word of the article title as a search term
    private class ImageQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                publishProgress(10);

                //Gets the search term as the last word in the article title
                String searchTerm = clickedArticle.getTitle().substring(clickedArticle.getTitle().lastIndexOf(" ")+1);

                //access key for the api
                String ACCESS_KEY = "8Rb5ana9LDe_4_n78eZ_gciKw-HURz34SSdLKjoD-kM";

                //url to the api, uses access key and search term as parameters
                URL url = new URL("https://api.unsplash.com/search/photos?query=" + searchTerm + "&client_id=" + ACCESS_KEY);
                //url connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //uses the GET method
                urlConnection.setRequestMethod("GET");

                //input stream for the url
                InputStream response = urlConnection.getInputStream();

                //reader to read the input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8));
                publishProgress(20);

                //this string builder is used to build the JSON string
                StringBuilder sb = new StringBuilder();
                publishProgress(30);
                String line;
                publishProgress(40);
                while ((line = reader.readLine()) != null) {
                    //loops through adding to the string
                    sb.append(line).append("\n");
                }

                //JSONObject of the whole string
                JSONObject jObject = new JSONObject(sb.toString());
                publishProgress(50);

                //Image array JSON that is returned from the search query
                JSONArray images = jObject.getJSONArray("results");
                publishProgress(60);

                //uses the first image in the array
                JSONObject jImage = images.getJSONObject(0);

                //urls of the image
                JSONObject urls = jImage.getJSONObject("urls");

                //raw url of the image
                URL imageURL = new URL(urls.getString("raw"));
                publishProgress(70);
                HttpURLConnection imageURLConnection = (HttpURLConnection) imageURL.openConnection();
                publishProgress(80);
                InputStream imageInputStream = imageURLConnection.getInputStream();
                publishProgress(90);

                //creates a bitmap from the image url
                image = BitmapFactory.decodeStream(imageInputStream);

            } catch (Exception e) {
                e.printStackTrace();
            }

            publishProgress(100);
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
            //Make progress bar invisible
            final ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            showAlert();
        }
    }
}