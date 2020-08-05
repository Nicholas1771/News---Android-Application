package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SettingsActivity extends BaseActivity {

    private final String TRACK = "TRACK";
    // delete, about and send button
    Button deleteSearchButton;
    Button sendButton;
    Button viewSearchButton;

    private CheckBox trackCheckBox;

    private EditText feedbackEditText;

    ProgressBar progressBar;
    TextView currentTempTextView;
    TextView maxTempTextView;
    TextView minTempTextView;

    private SharedPreferences sharedPreferences;

    private final String SEARCH = "SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        displayToolbar();
        setTitle("Settings");

        deleteSearchButton = findViewById((R.id.delete_search_button));
        sendButton = findViewById((R.id.send_button));
        viewSearchButton = findViewById((R.id.view_search_button));

        feedbackEditText = findViewById(R.id.feedback_edit_text);

        // click listener for delete search history button
        deleteSearchButton.setOnClickListener(v -> showSnackbar());

        currentTempTextView = findViewById(R.id.currentTemp);
        maxTempTextView = findViewById(R.id.maxTemp);
        minTempTextView = findViewById(R.id.minTemp);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        trackCheckBox = findViewById(R.id.search_checkbox);
        trackCheckBox.setChecked(getTrackHistory());
        trackCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setTrackHistory(isChecked);
            String toastMessage;

            if (isChecked) {
                toastMessage = "Now tracking search history";
            } else {
                toastMessage = "Now not tracking search history";
            }

            Toast.makeText(SettingsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        });

        viewSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                Fragment historyFragment = new HistoryFragment();

                transaction.add(R.id.search_container, historyFragment);
                transaction.commit();
            }
        });

        sendButton.setOnClickListener(v -> {
            String feedback = feedbackEditText.getText().toString();
            sendFeedback(feedback);
        });

        // Forecast query
        progressBar = findViewById(R.id.progressBar_settings);
        progressBar.setVisibility(View.VISIBLE);

        ForecastQuery obj = new ForecastQuery();
        obj.execute();
    }

    private boolean getTrackHistory () {
        return sharedPreferences.getBoolean("TRACK", false);
    }

    private void setTrackHistory (boolean trackHistory) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(TRACK, trackHistory);

        editor.apply();
    }

    public void sendFeedback(String feedback) {
        SendFeedback sendFeedback = new SendFeedback();
        sendFeedback.execute(feedback);
    }

    private void deleteSearchHistory() {

        Set<String> searchHistorySet = sharedPreferences.getStringSet(SEARCH, new HashSet<>());

        searchHistorySet.clear();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet(SEARCH, searchHistorySet);

        editor.apply();

        Toast.makeText(SettingsActivity.this, "Search history has been deleted", Toast.LENGTH_SHORT).show();
    }

    // snackbar for the delete button
    private void showSnackbar() {
        Snackbar.make(deleteSearchButton, "Do you want to delete the search history?" , Snackbar.LENGTH_LONG)
                .setAction("Delete", v -> deleteSearchHistory()).show();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

    // when user clicks help menu
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

    class SendFeedback extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... feedback) {
            String to = "capstoneprojectwebapp@gmail.com";
            String from = "capstoneprojectwebapp@gmail.com";

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", 587);

            Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication () {
                    return new PasswordAuthentication("capstoneprojectwebapp@gmail.com", "Capstone.34");
                }
            });

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject("Feedback");
                message.setText(feedback[0]);
                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            String toastMessage = "Thank you for your feedback :)";
            Toast.makeText(SettingsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // ASync task for weather api

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
            //Make the progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            //Set the progress
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute (String result) {
            currentTempTextView.setText(getString(R.string.current_temp) + currentTemp);
            maxTempTextView.setText(getString(R.string.max_temp) + maxTemp);
            minTempTextView.setText(getString(R.string.min_temp) + minTemp);

            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
