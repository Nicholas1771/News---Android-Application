package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends BaseActivity {

    // delete, about and send button
    Button delete_search_button, about_button, send_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        displayToolbar();
        setTitle("Settings");

        delete_search_button = (Button)findViewById((R.id.delete_search_button));
        about_button = (Button) findViewById((R.id.about_button));
        send_button = (Button) findViewById((R.id.send_button));

        // click listener for delete search history button
        delete_search_button.setOnClickListener(v -> showSnackbar());

        // Forecast query
        final ProgressBar progressBar = findViewById(R.id.progressBar_settings);
        progressBar.setVisibility(View.VISIBLE);

        ForecastQuery obj = new ForecastQuery();
        //obj.execute();
    }

    // snackbar for the delete button
    private void showSnackbar() {
        Snackbar.make(delete_search_button, "Do you want to delete the search history?" , Snackbar.LENGTH_LONG)
                .setAction("Delete", v -> Toast.makeText(SettingsActivity.this, "Search history has been deleted", Toast.LENGTH_SHORT).show()).show();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // when user clicks help menu
            case R.id.help_settings:
              AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
              builder.setTitle("Help")
                      .setMessage("Delete your search history or click the About Button")
                      .setPositiveButton("OK", null);

              AlertDialog alert = builder.create();
              alert.show();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // ASync task for weather api

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        // Strings fpr temperature and bitMap
        private String UVRating;
        private String minTemp;
        private String maxTemp;
        private String currentTemp;
        private Bitmap bitmap;
        private String iconName;

        @Override
        protected String doInBackground(String... args) {
            try {

                //create a URL object of what server to contact:
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();



                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");



                String parameter = null;

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        //If you get here, then you are pointing at a start tag
                        if (xpp.getName().equals("temperature")) {
                            //If you get here, then you are pointing to a <Weather> start tag
                            currentTemp = xpp.getAttributeValue(null, "value");
                            publishProgress(25);
                            minTemp = xpp.getAttributeValue(null, "min");
                            publishProgress(50);
                            maxTemp = xpp.getAttributeValue(null, "max");
                            publishProgress(75);
                        } else if (xpp.getName().equals("weather")) {
                            iconName = xpp.getAttributeValue(null, "icon");
                        }
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }

                if (fileExistance(iconName + ".png")) {
                    Log.i("file", "file exists - reading from file");
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(iconName + ".png");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(fis);
                } else {
                    Log.i("file", "file does not exist - downloading now");
                    String urlString = "http://openweathermap.org/img/w/" + iconName + ".png";
                    URL imageUrl = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                        Log.i("uh oh", urlString);
                    }
                    FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

                publishProgress(100);

                URL UVUrl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");

                HttpURLConnection urlConnection2 = (HttpURLConnection) UVUrl.openConnection();

                InputStream is = urlConnection2.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject jObject = new JSONObject(result);

                UVRating = Double.toString(jObject.getDouble("value"));

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return "Done";
        }

        // this method checks if file exists
        public boolean fileExistance (String fname){
            Log.i("file", "Looking for file with name: " + fname);
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        @Override
        protected void onProgressUpdate (Integer... values) {
            final ProgressBar progressBar = findViewById(R.id.progressBar);

            //Make the progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            //Set the progress
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute (String result) {

            // grabs progressBar, temperatures, uv rating and weather image
            final ProgressBar progressBar = findViewById(R.id.progressBar);
            final TextView currentTemp = findViewById(R.id.currentTemp);
            final TextView maxTemp = findViewById(R.id.maxTemp);
            final TextView minTemp = findViewById(R.id.minTemp);
            final TextView uvRating = findViewById(R.id.UVRating);
            final ImageView weatherImage = findViewById(R.id.weatherImage);

            // sets the text for weather and image
            currentTemp.setText("Current Temperature: " + this.currentTemp);
            maxTemp.setText("Maximum Temperature: " + this.maxTemp);
            minTemp.setText("Minimum Temperature: " + this.minTemp);
            uvRating.setText("UV Rating: " + this.UVRating);
            weatherImage.setImageBitmap(bitmap);

            // sets the progress bar to invisible
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
