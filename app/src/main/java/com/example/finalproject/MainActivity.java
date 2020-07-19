package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Temporary button to go to news list to test it
        Button button = findViewById(R.id.button);

        //Sets the onclicklistener for the button to go to the news list activity
        button.setOnClickListener(v -> {
            Intent newsListActivity = new Intent(MainActivity.this, NewsListActivity.class);
            startActivity(newsListActivity);
        });
    }
}
