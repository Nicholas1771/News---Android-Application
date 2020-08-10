package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

public class ProfileActiviy extends BaseActivity {

    //used when taking profile picture
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //edit text for the profile information: first name, last name and email
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;

    //strings used to access the sharedpreferences variables
    private final String FIRST_NAME = "FIRST_NAME";
    private final String LAST_NAME = "LAST_NAME";
    private final String EMAIL = "EMAIL";
    private final String IMAGE = "IMAGE";

    //shared preferences
    private SharedPreferences sharedPreferences;

    //imageview which contains the random image retrieved from the async task
    ImageView randomImageView;

    //random image retrieved from the async task
    private Bitmap randomImage;

    //profile picture image
    private Bitmap profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activiy);

        //displays the toolbar and sets the title
        displayToolbar();
        setTitle("Profile");

        //gets the image view
        randomImageView = findViewById(R.id.random_image);

        //execute the async task to get the background image
        new BackgroundImageQuery().execute();

        //gets the edit texts for the profile information
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);

        //gets the picture button
        ImageButton pictureButton = findViewById(R.id.picture_button);

        //gets the default sharedpreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //gets the user information from the sharedpreferences
        firstNameEditText.setText(sharedPreferences.getString(FIRST_NAME, ""));
        lastNameEditText.setText(sharedPreferences.getString(LAST_NAME, ""));
        emailEditText.setText(sharedPreferences.getString(EMAIL, ""));

        //get the profile image bitmap
        profileImage = getImageFromEncodedString(sharedPreferences.getString(IMAGE, ""));

        //gets the encoded string and sets its as the image bitmap for the profile picture
        pictureButton.setImageBitmap(profileImage);

        //sets the picture button on click listener to open the take picture intent
        pictureButton.setOnClickListener(v -> dispatchTakePictureIntent());

        //gets save button
        final Button saveButton = findViewById(R.id.save_button);

        // click listener for the Save button
        saveButton.setOnClickListener(v -> {

            // toast message for saving data
            String toastMessage = "Saved profile information";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getApplicationContext(), toastMessage, duration).show();

            // grabs the firstName,lastName, and email text from user
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailEditText.getText().toString();

            //save the new profile data
            saveProfileData(firstName, lastName, email, getEncodedImage(profileImage));
        });
    }

    //this method saves the profile data taken as parameters
    private void saveProfileData (String firstName, String lastName, String email, String encodedImage) {

        //gets the editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //puts the new profile data into the shared preferences
        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);

        //puts the encoded image string
        editor.putString(IMAGE, encodedImage);

        //apply the changes
        editor.apply();
    }

    // this method sets the background image
    public void updateBackgroundImage () {
        randomImageView.setImageBitmap(randomImage);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_profile_activiy;
    }

    private void dispatchTakePictureIntent() {

        //created the take picture intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //starts the intent to take profile picture
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //This method takes a bitmap image and encodes it to a String and returns the string
    //Source: https://stackoverflow.com/questions/17268519/how-to-store-bitmap-object-in-sharedpreferences-in-android
    private String getEncodedImage (Bitmap profileImage) {

        //initialize the baso
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //compress the profile image using to baos
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, baos);

        //convert the compressed image to a byte array
        byte[] b = baos.toByteArray();

        //encode the byte array to a string and return it
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    //This method gets the encoded string and converts it to a bitmap image and returns the image
    //Source: https://stackoverflow.com/questions/17268519/how-to-store-bitmap-object-in-sharedpreferences-in-android
    private Bitmap getImageFromEncodedString (String encodedImage) {

        //decodes the string to a byte array
        byte[] imageAsBytes = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);

        //convert the byte array to an image and returns it
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //gets the picture button
        final ImageButton pictureButton = findViewById(R.id.picture_button);

        //gets the image from the extras and sets it the picture button bitmap image
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            profileImage = (Bitmap) extras.get("data");
            pictureButton.setImageBitmap(profileImage);
        }

        //Creates snackbar to retake picture
        View profileView = findViewById(R.id.profile);

        String snackbarMessage = "Retake picture?";
        int duration = Snackbar.LENGTH_LONG;

        Snackbar snackbar = Snackbar.make(profileView, snackbarMessage, duration);

        //This action will hide the article if confirmed
        snackbar.setAction("Retake", v -> dispatchTakePictureIntent());

        //Shows the snackbar
        snackbar.show();
    }

    //this async task uses an api to get a random background image
    private class BackgroundImageQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                //url where we get the articles from
                publishProgress(10);

                //access key for api
                String ACCESS_KEY = "8Rb5ana9LDe_4_n78eZ_gciKw-HURz34SSdLKjoD-kM";

                //url of api
                URL url = new URL("https://api.unsplash.com/photos/random?client_id=" + ACCESS_KEY);

                //url connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //uses get request method
                urlConnection.setRequestMethod("GET");

                //input stream
                InputStream response = urlConnection.getInputStream();

                //reader for the input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8));
                publishProgress(20);

                //builds the string from the JSON
                StringBuilder sb = new StringBuilder();
                publishProgress(30);
                String line;
                publishProgress(40);
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                //JSON object
                JSONObject jObject = new JSONObject(sb.toString());
                publishProgress(50);

                //All urls for the random image
                JSONObject urls = jObject.getJSONObject("urls");
                publishProgress(60);

                //raw image url
                URL imageURL = new URL(urls.getString("raw"));
                publishProgress(70);
                HttpURLConnection imageURLConnection = (HttpURLConnection) imageURL.openConnection();
                publishProgress(80);
                InputStream imageInputStream = imageURLConnection.getInputStream();
                publishProgress(90);

                //creates a bitmap from the image url
                randomImage = BitmapFactory.decodeStream(imageInputStream);

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
            updateBackgroundImage();
        }
    }


    // help menu alert dialog
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

      // when user clicks help menu

        if (item.getItemId() == R.id.nav_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActiviy.this);
            builder.setTitle("Help")
                    .setMessage("On this page you can save your name, email and profile picture.")
                    .setPositiveButton("OK", null);


            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
