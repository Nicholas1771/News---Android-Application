package com.example.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends BaseActivity {

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

        delete_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackbar();
            }
        });

    }

    // snackbar for the delete button
    private void showSnackbar() {
        Snackbar.make(delete_search_button, "Do you want to delete the search history?" , Snackbar.LENGTH_LONG)
                .setAction("Delete", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SettingsActivity.this, "Search history has been deleted", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.help_settings:
              AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
              builder.setTitle("Help")
                      .setMessage("Delete your search history or click the About Button")
                      .setPositiveButton("OK", (dialog, which) -> SettingsActivity.super.onBackPressed())
                      .setNegativeButton("Cancel", null);

              AlertDialog alert = builder.create();
              alert.show();

            default:
                return super.onOptionsItemSelected(item);

        }
    }








}
