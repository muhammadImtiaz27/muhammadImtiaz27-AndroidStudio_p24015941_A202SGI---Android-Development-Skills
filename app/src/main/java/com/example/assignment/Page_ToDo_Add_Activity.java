package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;

public class Page_ToDo_Add_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private SharedPreferences user_info;

    private TextInputEditText et_todo_title, et_todo_description, et_todo_date, et_todo_time;
    private MaterialButton btn_add_todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_to_do);

        user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);

        et_todo_title = findViewById(R.id.et_todo_title);
        et_todo_description = findViewById(R.id.et_todo_description);
        et_todo_date = findViewById(R.id.et_todo_date);
        et_todo_time = findViewById(R.id.et_todo_time);
        btn_add_todo = findViewById(R.id.btn_add_todo);

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
                    Sidebar_Menu_Functions.open_home_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_ToDo_Add_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_ToDo_Add_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_ToDo_Add_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_ToDo_Add_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
                    finish(); // Terminate this current activity, and go back to the previous activity

                }
            }
        };

        // Add the callback to the back press dispatcher
        // This is just like callback functions in JavaScript. It is listening for the back button to be clicked
        // When the back button is clicked, the function named callback, is executed.
        getOnBackPressedDispatcher().addCallback(Page_ToDo_Add_Activity.this, callback);

        // When the user click the Date Edit Text
        et_todo_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create the DatePicker and set today's date as the default selection
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select a date")
                        .setSelection(System.currentTimeMillis()) // Set default to today's date
                        .build();

                datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

                // When the user click the OK button after selecting a date
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {

                        // selection is the number of milliseconds since the Unix epoch (January 1, 1970),
                        // which is how dates and times are commonly represented in programming

                        // Convert selection (long datatype) into a string
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = sdf.format(selection);  // This will give a string like "08/10/2024"
                        et_todo_date.setText(formattedDate);  // Display the formatted date

                    }
                });

            }
        });

        // When the user click the Time Edit Text
        et_todo_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create the MaterialTimePicker with 24-hour format
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)  // Use CLOCK_12H for 12-hour format with AM/PM
                        .setHour(12)                          // Default hour
                        .setMinute(0)                         // Default minute
                        .setTitleText("Select a time")        // Set the title
                        .build();

                // Show the TimePicker
                timePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");

                // When the user click the OK button after selecting time
                timePicker.addOnPositiveButtonClickListener(dialog -> {
                    // Get the selected hour and minute
                    int selectedHour = timePicker.getHour();
                    int selectedMinute = timePicker.getMinute();

                    // Format the time accordingly.
                    // Selected time = selectedHour:selectedMinute
                    // selectedMinute is the one being formatted. If minute is less than 10, add a zero before it.
                    String selectedTime = selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute);

                    et_todo_time.setText(selectedTime);
                });

            }
        });

        // When the user click the Save button
        btn_add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = et_todo_title.getText().toString();
                String description = et_todo_description.getText().toString();
                String due_date = et_todo_date.getText().toString();
                String due_time = et_todo_time.getText().toString();

                if(title.isEmpty() || description.isEmpty() || due_date.isEmpty() || due_time.isEmpty()){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please provide all fields.", Snackbar.LENGTH_LONG).show();
                }
                else if (title.length() > 50) {
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "The title must be less than 50 characters.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    // Store the result in a database
                    DB_Helper_ToDo db = new DB_Helper_ToDo(Page_ToDo_Add_Activity.this);
                    db.create_todo(new ToDo(user_info.getString("email", "no email"), title, description, due_date, due_time));
                    Intent intent = new Intent(Page_ToDo_Add_Activity.this, Page_ToDo_Activity.class); // Go back to the To-Do page
                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}