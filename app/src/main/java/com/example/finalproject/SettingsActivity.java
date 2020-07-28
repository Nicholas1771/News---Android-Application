package com.example.finalproject;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends BaseActivity {

    Button delete_search_button, about_button, send_button;

    ProgressBar progressBar;
    TextView currentTempTextView;
    TextView maxTempTextView;
    TextView minTempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        displayToolbar();
        setTitle("Settings");

        delete_search_button = findViewById((R.id.delete_search_button));
        about_button = findViewById((R.id.about_button));
        send_button = findViewById((R.id.send_button));

        delete_search_button.setOnClickListener(v -> showSnackbar());

        currentTempTextView = findViewById(R.id.currentTemp);
        maxTempTextView = findViewById(R.id.maxTemp);
        minTempTextView = findViewById(R.id.minTemp);


        // Forecast query
        progressBar = findViewById(R.id.progressBar_settings);
        progressBar.setVisibility(View.VISIBLE);

        ForecastQuery obj = new ForecastQuery();
        obj.execute();
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

        if (item.getItemId() == R.id.help_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Help")
                    .setMessage("Delete your search history or click the About Button")
                    .setPositiveButton("OK", null);

            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        private String minTemp;
        private String maxTemp;
        private String currentTemp;

        @Override
        protected String doInBackground(String... args) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");
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
                        }
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
                publishProgress(100);

            } catch (Exception ignored) {
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate (Integer... values) {
            progressBar.setVisibility(View.VISIBLE);

            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute (String result) {
            currentTempTextView.setText(getString(R.string.current_temperature) + currentTemp);
            maxTempTextView.setText(getString(R.string.maximum_temperature) + maxTemp);
            minTempTextView.setText(getString(R.string.minimum_temperature) + minTemp);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
