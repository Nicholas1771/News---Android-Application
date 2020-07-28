package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;
public class ProfileActiviy extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //private EditText firstNameEditText;
    //private EditText lastNameEditText;
    //private EditText emailEditText;

    RelativeLayout backgroundImageContainer;

    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activiy);

        displayToolbar();
        setTitle("Profile");

        final ImageButton pictureButton = findViewById(R.id.picture_button);

        backgroundImageContainer = findViewById(R.id.background_image_container);

        new BackgroundImageQuery().execute();

        //firstNameEditText = findViewById(R.id.first_name_edit_text);
        //lastNameEditText = findViewById(R.id.last_name_edit_text);
        //emailEditText = findViewById(R.id.email_edit_text);

        pictureButton.setOnClickListener(v -> dispatchTakePictureIntent());

        final Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {

            String toastMessage = "Saved profile picture (Fake message - data will be saved in milestone 3)";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getApplicationContext(), toastMessage, duration).show();

            //String firstName = firstNameEditText.getText().toString();
            //String lastName = lastNameEditText.getText().toString();
            //String email = emailEditText.getText().toString();
            //TO-DO save the data as shared preference
        });
    }

    public void updateBackgroundImage () {
        backgroundImageContainer.setBackground(new BitmapDrawable(getApplicationContext().getResources(), image));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ImageButton pictureButton = findViewById(R.id.picture_button);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pictureButton.setImageBitmap(imageBitmap);
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

    private class BackgroundImageQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                //url where we get the articles from
                publishProgress(10);
                String ACCESS_KEY = "8Rb5ana9LDe_4_n78eZ_gciKw-HURz34SSdLKjoD-kM";
                URL url = new URL("https://api.unsplash.com/photos/random?client_id=" + ACCESS_KEY);

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
                JSONObject urls = jObject.getJSONObject("urls");
                publishProgress(60);
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
            updateBackgroundImage();
        }
    }


    // help menu alert dialog
    public boolean onOptionsItemSelected(MenuItem item) {

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
