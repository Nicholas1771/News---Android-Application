package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar, drawer layout, navigation view and toggle
    private Toolbar toolbar;

    //drawer layout
    private DrawerLayout drawerLayout;

    // displays toolbar
    void displayToolbar() {

        //gets the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        //sets the suppport action bar to the toolber
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //sets up the navigation after onCreate
        setUpNav();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //calls method menuItemselected
        menuItemSelected(item);

        //close drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //handles when a menuitem is selected
    public void menuItemSelected (MenuItem item) {
        //intent to go to
        Intent intent;

        // switch statements for each menu item with intents
        switch (item.getItemId()) {
            case R.id.nav_profile:
                intent = new Intent(BaseActivity.this, ProfileActiviy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.nav_news:
                intent = new Intent(BaseActivity.this, NewsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.nav_favourites:
                intent = new Intent(BaseActivity.this, FavouritesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(BaseActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }
    }

    // method sets up Navigation Drawer

    void setUpNav () {
        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);

        //Sets up the toggle action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        //Sets up the drawer toggle
        drawerLayout.addDrawerListener(toggle);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        setUpNav();
    }

    // setter for Title
    private void setTitle(String title) {
        //changes title of toolbar
        toolbar.setTitle(title + " V1.2");
    }

    protected abstract int getLayoutResource();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates the menu
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //When the back button is pressed, if the drawer is open then close it
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //drawer was not open perform normal onBackPressed
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //call menuItemSelected method
        menuItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

}
