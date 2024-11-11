package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;

import java.util.Set;

public class Page_Settings_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private SharedPreferences shared_preferences;
    boolean isNight;

    private MaterialCardView card_profile;
    private MaterialSwitch switch_theme;

    // Initialize a flag to track initial setup vs user interaction
    boolean isUserToggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_settings);

        // Store the data into the file called UserSettings
        // MODE_PRIVATE means only this app can access this file
        // getSharedPreferences() means opening the UserSettings file,
        // or automatically creating the UserSettings file (only if it does not exist)
        shared_preferences = getSharedPreferences("UserSettings", MODE_PRIVATE);

        // Retrieve data from UserSettings file (shared preference)
        // The second parameter is a default value, in case the "isNight" key does not exist, or something went wrong.
        isNight = shared_preferences.getBoolean("isNight", false);

        // After getting data from UserSettings file, activate the theme accordingly
        activateTheme(isNight);

        card_profile = findViewById(R.id.card_profile);
        switch_theme = findViewById(R.id.switch_theme);

        // Drawer Menu
        dl_drawer_layout = findViewById(R.id.dl_drawer_layout);
        nv_main_menu = findViewById(R.id.nv_main_menu);
        toolbar = findViewById(R.id.toolbar);
        header_view = nv_main_menu.getHeaderView(0);

        setSupportActionBar(toolbar);

        // Drawer menu - Initialize ActionBarDrawerToggle with custom icon
        drawer_toggle = new ActionBarDrawerToggle(this, dl_drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(@NonNull android.view.View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // Change the icon based on the drawer state
                if (dl_drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    // getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);
                }
                else {
                    // getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);
                }
            }
        };

        dl_drawer_layout.addDrawerListener(drawer_toggle);
        drawer_toggle.syncState();

        // Drawer menu - Enable the hamburger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv_main_menu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Close the drawer menu after the user click one of the options
                dl_drawer_layout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();

                if (id == R.id.menu_home) {
                    Sidebar_Menu_Functions.open_home_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Settings_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Settings_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Settings_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Settings_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.client_id)) // Ensure you have the correct client ID
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Use the OnBackPressedDispatcher to handle the back button event
        // It means what happens when the user click the back button on their phone
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                // If the main menu is opened, and then the user click the back button,
                // then close the main menu
                if (dl_drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    dl_drawer_layout.closeDrawer(GravityCompat.START);
                }
                else{
                    // If the drawer is closed, proceed with the default back action
                    Intent intent = new Intent(Page_Settings_Activity.this, Page_Home_Page_Activity.class);
                    startActivity(intent);
                    finish(); // Terminate this current activity, and go back to the previous activity

                }
            }
        };

        // Add the callback to the back press dispatcher
        // This is just like callback functions in JavaScript. It is listening for the back button to be clicked
        // When the back button is clicked, the function named callback, is executed.
        getOnBackPressedDispatcher().addCallback(Page_Settings_Activity.this, callback);

        // Set the switch accordingly
        switch_theme.setChecked(isNight);

        switch_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // Disable the switch for 2 seconds, to prevent the user from abusing the switch theme functionality
                switch_theme.setEnabled(false);

                SharedPreferences.Editor editor = shared_preferences.edit();

                // Wait for 2 seconds (2000 milliseconds) before enabling and performing action
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(isChecked){
                            // Store the value of true to the "isNight" key in the UserSettings file
                            editor.putBoolean("isNight", true);

                            // Activate Dark Mode
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        else{
                            // Store the value of true to the "isNight" key in the UserSettings file
                            editor.putBoolean("isNight", false);

                            // Activate Light Mode
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }

                        // Apply changes. Meaning save the data, so that later it can be retrieved.
                        editor.apply();

                        // Re-enable the switch after 2 seconds
                        switch_theme.setEnabled(true);
                    }
                }, 2000); // 2000 milliseconds = 2 seconds

            }
        });

        card_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Settings_Activity.this, Page_User_Profile_Activity.class);
                startActivity(intent);
            }
        });

    }

    private void activateTheme(boolean isNight) {
        if(isNight){
            // Activate Night Mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            // Activate Dark Mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }
}