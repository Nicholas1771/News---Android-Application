package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
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

            case R.id.nav_profile: //profile menu item
                intent = new Intent(BaseActivity.this, ProfileActiviy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.nav_news: //news menu item
                intent = new Intent(BaseActivity.this, NewsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.nav_favourites: //favourites menu item
                intent = new Intent(BaseActivity.this, FavouritesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.nav_settings: //settings menu item
                intent = new Intent(BaseActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }
    }

    // method sets up Navigation Drawer
    void setUpNav () {

        //gets the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);

        //gets the toolbar
        toolbar = findViewById(R.id.toolbar);

        //get the navigation view
        NavigationView navigationView = findViewById(R.id.navigation);
        //hide the help menu item
        navigationView.getMenu().findItem(R.id.nav_help).setVisible(false);
        navigationView.setNavigationItemSelectedListener(this);

        //Sets up the drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        //set up the navigation
        setUpNav();
    }


    // setter for Title
    public void setTitle(String title) {
        //changes title of toolbar
        super.setTitle(title + " V1.3");
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
