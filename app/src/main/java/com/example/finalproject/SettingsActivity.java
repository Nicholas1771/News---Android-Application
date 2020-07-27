package com.example.finalproject;

import android.os.Bundle;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        displayToolbar();
        setTitle("Settings");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }


}
