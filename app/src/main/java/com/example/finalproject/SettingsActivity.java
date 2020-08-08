package com.example.finalproject;

import androidx.annotation.NonNull;
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

    //Strings used to get the TRACK and SEARCH information from the shared preferences
    private final String TRACK = "TRACK";
    private final String SEARCH = "SEARCH";

    // delete, about and send button
    Button deleteSearchButton;
    Button sendButton;
    Button viewSearchButton;

    //edit text for the user feedback
    private EditText feedbackEditText;

    //progress bar for the weather information
    ProgressBar progressBar;

    //weather textviews
    TextView currentTempTextView;
    TextView maxTempTextView;
    TextView minTempTextView;

    //shared preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //display the toolbar and set the title
        displayToolbar();
        setTitle("Settings");

        //get the delete history button
        deleteSearchButton = findViewById((R.id.delete_search_button));

        //get the send feedback button
        sendButton = findViewById((R.id.send_button));

        //get the view history button
        viewSearchButton = findViewById((R.id.view_search_button));

        //get the feedback edit text
        feedbackEditText = findViewById(R.id.feedback_edit_text);

        // click listener for delete search history button
        deleteSearchButton.setOnClickListener(v -> showSnackbar());

        //get the textviews for the weather information
        currentTempTextView = findViewById(R.id.currentTemp);
        maxTempTextView = findViewById(R.id.maxTemp);
        minTempTextView = findViewById(R.id.minTemp);

        //get the default shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //get the tracking checkbox
        //check box for tracking history
        CheckBox trackCheckBox = findViewById(R.id.search_checkbox);

        //gets the track boolean from shared preferences and sets the checkbox isChecked to that boolean
        trackCheckBox.setChecked(getTrackHistory());

        //set the on checked listener to change the boolean in the shared preferences
        trackCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            //sets the new boolean data in the shared preferences
            setTrackHistory(isChecked);

            //String used for the toast message
            String toastMessage;

            //message changes based on the checked boolean
            if (isChecked) {
                toastMessage = "Now tracking search history";
            } else {
                toastMessage = "Now not tracking search history";
            }

            //make and show the toast
            Toast.makeText(SettingsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        });

        //sets the on click listener for the view search button to create the history fragment
        viewSearchButton.setOnClickListener(v -> {

            //gets the support fragment manager
            FragmentManager manager = getSupportFragmentManager();

            // initialize the transaction
            FragmentTransaction transaction = manager.beginTransaction();

            //initialize the fragment
            Fragment historyFragment = new HistoryFragment();

            //add the fragment to the transaction and commit
            transaction.add(R.id.search_container, historyFragment);
            transaction.commit();
        });

        //set the on click listener for the send button to get the feedback string and send the feedback email
        sendButton.setOnClickListener(v -> {
            String feedback = feedbackEditText.getText().toString();
            sendFeedback(feedback);
        });

        //gets the progress bar and makes it visible
        progressBar = findViewById(R.id.progressBar_settings);
        progressBar.setVisibility(View.VISIBLE);

        //executes a forecast async task to update the weather information
        new ForecastQuery().execute();
    }

    //gets and returns the track boolean variable from the shared preferences
    private boolean getTrackHistory () {
        return sharedPreferences.getBoolean("TRACK", false);
    }

    //sets the track boolean variable in the shared preferences
    private void setTrackHistory (boolean trackHistory) {

        //gets the editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //put the boolean variable in the shared preferences
        editor.putBoolean(TRACK, trackHistory);

        //apply the changes
        editor.apply();
    }

    //send the feedback by executing the send feedback async task
    public void sendFeedback(String feedback) {
        SendFeedback sendFeedback = new SendFeedback();
        sendFeedback.execute(feedback);
    }

    //this method deletes all search history in the shared preferences
    private void deleteSearchHistory() {

        //gets the search history hashset from the shared preferences
        Set<String> searchHistorySet = sharedPreferences.getStringSet(SEARCH, new HashSet<>());

        //clears the set
        searchHistorySet.clear();

        //get the editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //puts the cleared set into the shared preferences
        editor.putStringSet(SEARCH, searchHistorySet);

        //apply the changes
        editor.apply();

        //show a toast that the history has been deleted
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

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Shows the help menu when help is clicked
        if (item.getItemId() == R.id.nav_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Help")
                    .setMessage("On this page you can view your search history and delete your search history. You can also choose whether your searches are tracked or not. You can also send feedback about the application")
                    .setPositiveButton("OK", null);


            //create and show the alert dialgoue
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //This async task is used to send feedback from the user to our email
    class SendFeedback extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... feedback) {

            //the feedback is sent to and from our email address
            String to = "capstoneprojectwebapp@gmail.com";
            String from = "capstoneprojectwebapp@gmail.com";

            //set up the properties
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", 587);

            //create the session using the email credentials
            Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication () {
                    return new PasswordAuthentication("capstoneprojectwebapp@gmail.com", "Capstone.34");
                }
            });

            //try to send the email containing the feedback
            try {

                //initialize the MimeMessage
                MimeMessage message = new MimeMessage(session);

                //set the from email
                message.setFrom(new InternetAddress(from));

                //add the to email as a recipient
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                //set the email subject as Feedback
                message.setSubject("Feedback");

                //set the feedback text that was passed as a parameter
                message.setText(feedback[0]);

                //send the message
                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            //Display a toast message thanking the user for the feedback
            String toastMessage = "Thank you for your feedback :)";
            Toast.makeText(SettingsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // This async task gets the weather data from the weather api

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        //strings for the weather data
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
            //set the weather data text
            currentTempTextView.setText(getString(R.string.current_temp) + currentTemp);
            maxTempTextView.setText(getString(R.string.max_temp) + maxTemp);
            minTempTextView.setText(getString(R.string.min_temp) + minTemp);

            //make the progress bar invisible
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
