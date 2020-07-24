package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class ProfileActiviy extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activiy);

        displayToolbar();
        setTitle("Profile");

        final ImageButton pictureButton = findViewById(R.id.picture_button);

        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);

        pictureButton.setOnClickListener(v -> dispatchTakePictureIntent());

        final Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {

            String toastMessage = "Saved profile picture (Fake message - data will be saved in milestone 3)";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getApplicationContext(), toastMessage, duration).show();

            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            //TO-DO save the data as shared preference
        });
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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pictureButton.setImageBitmap(imageBitmap);
        }

        //Creates snackbar to retake picture
        View profileView = findViewById(R.id.profile);

        String snackbarMessage = "Retake picture?";
        int duration = Snackbar.LENGTH_LONG;

        Snackbar snackbar = Snackbar.make(profileView, snackbarMessage, duration);

        //This action will hide the article if confirmed
        snackbar.setAction("Retake", v -> {
            dispatchTakePictureIntent();
        });

        //Shows the snackbar
        snackbar.show();
    }
}
