package com.example.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;


import android.view.MenuItem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Objects;
import android.app.AlertDialog;

public class FavouritesActivity extends BaseActivity {
    //All articles
    private ArrayList<Article> allArticles;

    //List of articles that are not hidden
    private ArrayList<Article> unHiddenArticles;

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

            new ImageQuery().execute();
            //This alert opens when a news article title is clicked. It shows the articles title, description, date, and link



            //Source from: https://essential-android.programming-books.io/alert-dialog-containing-a-clickable-link-9db7e0c7a32b4f84bb41150d06f418a9

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

    public void removeFavourite (Article article) {
        String link = article.getLinkToArticle();
        database.removeArticle(link);
        allArticles = database.getFavouriteArticles();
        updateArticles();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        displayToolbar();
        setTitle("Favourites");
        //initialize the articles
        allArticles = new ArrayList<>();

        database = new Database(this);
        allArticles = database.getFavouriteArticles();

        updateArticles();
    }

    // help menu alert dialog
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.help_settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(FavouritesActivity.this);
                builder.setTitle("Help")
                        .setMessage("Enter article and press search")
                        .setPositiveButton("OK", (dialog, which) -> FavouritesActivity.super.onBackPressed())
                        .setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();

            default:
                return super.onOptionsItemSelected(item);

        }
    }
  
    private void showAlert () {
        LayoutInflater factory = LayoutInflater.from(FavouritesActivity.this);
        final View view = factory.inflate(R.layout.alert_dialogue_image, null);

        imageView = view.findViewById(R.id.alert_image);
        imageView.setImageBitmap(image);

        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);
        TextView date = view.findViewById(R.id.date);
        TextView link = view.findViewById(R.id.link);

        title.setText(clickedArticle.getTitle());
        description.setText(clickedArticle.getDescription());
        date.setText(clickedArticle.getDate());
        link.setText(Html.fromHtml("<a href=\"" + clickedArticle.getLinkToArticle() + "\">" + clickedArticle.getLinkToArticle()));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavouritesActivity.this);
        alertDialogBuilder.setTitle("Article Information")
                .setView(view)
                .setPositiveButton("Remove from favourites", (dialog, which) -> removeFavourite(clickedArticle));
        alert = alertDialogBuilder.create();
        alert.show();
    }

    private class ImageQuery extends AsyncTask<String, Integer, String> {

        private final String ACCESS_KEY = "8Rb5ana9LDe_4_n78eZ_gciKw-HURz34SSdLKjoD-kM";

        @Override
        protected String doInBackground(String... args) {

            try {
                //url where we get the articles from
                publishProgress(10);
                String searchTerm = clickedArticle.getTitle().substring(clickedArticle.getTitle().lastIndexOf(" ")+1);
                Log.i("test", searchTerm);
                URL url = new URL("https://api.unsplash.com/search/photos?query=" + searchTerm + "&client_id=" + ACCESS_KEY);
                //url connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream response = urlConnection.getInputStream();
                //setProgress(10);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8));
                publishProgress(20);
                StringBuilder sb = new StringBuilder();
                publishProgress(30);
                String line;
                publishProgress(40);
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                JSONObject jObject = new JSONObject(sb.toString());
                publishProgress(50);
                JSONArray images = jObject.getJSONArray("results");
                publishProgress(60);

                JSONObject jImage = images.getJSONObject(0);

                JSONObject urls = jImage.getJSONObject("urls");

                URL imageURL = new URL(urls.getString("raw"));
                publishProgress(70);
                HttpURLConnection imageURLConnection = (HttpURLConnection) imageURL.openConnection();
                publishProgress(80);
                InputStream imageInputStream = imageURLConnection.getInputStream();
                publishProgress(90);
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