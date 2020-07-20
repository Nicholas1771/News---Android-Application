package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
//Milestone 1
//Temporary activity please do not consider this an activity for the requirements
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button used to go to news list activity
        Button button = findViewById(R.id.button);

        //On click listener for button gos to the news list activity when triggered
        button.setOnClickListener(v -> {
            Intent newsListActivity = new Intent(MainActivity.this, NewsListActivity.class);
            startActivity(newsListActivity);
        });
    }
}

