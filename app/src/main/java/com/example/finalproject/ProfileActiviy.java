package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private ImageButton pictureButton;

    private final String FIRST_NAME = "FIRST_NAME";
    private final String LAST_NAME = "LAST_NAME";
    private final String EMAIL = "EMAIL";
    private final String IMAGE = "IMAGE";

    private SharedPreferences sharedPreferences;

    ImageView randomImageView;

    private Bitmap randomImage;

    private Bitmap profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activiy);

        displayToolbar();
        setTitle("Profile");

        randomImageView = findViewById(R.id.random_image);

        new BackgroundImageQuery().execute();

        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        pictureButton = findViewById(R.id.picture_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        firstNameEditText.setText(sharedPreferences.getString(FIRST_NAME, ""));
        lastNameEditText.setText(sharedPreferences.getString(LAST_NAME, ""));
        emailEditText.setText(sharedPreferences.getString(EMAIL, ""));
        pictureButton.setImageBitmap(getImageFromEncodedString(sharedPreferences.getString(IMAGE, "")));

        pictureButton.setOnClickListener(v -> dispatchTakePictureIntent());

        final Button saveButton = findViewById(R.id.save_button);

        // click listener for the Save button
        saveButton.setOnClickListener(v -> {

            // toast message for save
            String toastMessage = "Saved profile picture (Fake message - data will be saved in milestone 3)";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getApplicationContext(), toastMessage, duration).show();

            // grabs the firstName,lastName, and email text from user
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailEditText.getText().toString();

            saveProfileData(firstName, lastName, email, getEncodedImage(profileImage));

            //TO-DO save the data as shared preference
        });
    }

    private void saveProfileData (String firstName, String lastName, String email, String encodedImage) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL, email);
        editor.putString(IMAGE, encodedImage);
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Source: https://stackoverflow.com/questions/17268519/how-to-store-bitmap-object-in-sharedpreferences-in-android
    private String getEncodedImage (Bitmap profileImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    //Source: https://stackoverflow.com/questions/17268519/how-to-store-bitmap-object-in-sharedpreferences-in-android
    private Bitmap getImageFromEncodedString (String encodedImage) {
        byte[] imageAsBytes = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ImageButton pictureButton = findViewById(R.id.picture_button);

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
    public boolean onOptionsItemSelected(MenuItem item) {

      // when user clicks help menu

        if (item.getItemId() == R.id.help_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActiviy.this);
            builder.setTitle("Help")
                    .setMessage("Enter your information and press save at the bottom")
                    .setPositiveButton("OK", null);


            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
