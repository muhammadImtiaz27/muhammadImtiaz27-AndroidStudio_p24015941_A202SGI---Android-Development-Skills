package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Page_User_Profile_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private SharedPreferences user_info;
    private DatabaseReference database_reference;

    private ConstraintLayout result_container;
    private TextView tv_user_email;
    private ShapeableImageView iv_user_picture;
    private FloatingActionButton btn_change_user_picture;
    private CircularProgressIndicator progress_bar;

    private String str_uri_profile_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_user_profile);

        user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);

        // Initialize the Firebase Database reference
        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("User");

        result_container = findViewById(R.id.result_container);
        tv_user_email = findViewById(R.id.tv_user_email);
        iv_user_picture = findViewById(R.id.iv_user_picture);
        btn_change_user_picture = findViewById(R.id.btn_change_user_picture);
        progress_bar = findViewById(R.id.progress_bar);

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
                    Sidebar_Menu_Functions.open_home_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_User_Profile_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_User_Profile_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_User_Profile_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_User_Profile_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_User_Profile_Activity.this, callback);

        str_uri_profile_picture = "";

        tv_user_email.setText(user_info.getString("email", "Could not get email"));

        btn_change_user_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_User_Profile_Activity.this, Page_Change_User_Profile_Picture_Activity.class);
                intent.putExtra("profile_picture", str_uri_profile_picture);
                startActivity(intent);
            }
        });

        // Get user profile picture from Realtime Database
        getUserProfilePicture();

    }

    private void getUserProfilePicture() {

        String user_email = user_info.getString("email", "Could not get email");

        Toast.makeText(this, user_email, Toast.LENGTH_SHORT).show();

        // Create a query to search for a specific name
        Query query = database_reference.orderByChild("email").equalTo(user_email);

        // Attach a listener to the query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // If the user with that email is found in the database
                if(snapshot.exists()){

                    // Get the first child (since there's only one expected result)
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();

                    // Get the data as a User object
                    User user = dataSnapshot.getValue(User.class);

                    // Steps to turn the progress bar from spinning indefinite to definite, after getting the translated data successfully
                    // Step 1: Switch to determinate mode
                    progress_bar.setIndeterminate(false);

                    // Step 2: Start at 0% progress
                    progress_bar.setProgressCompat(0, false);

                    // Step 3: Animate progress to 100% over a period (e.g., 2 seconds)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Step 4: Animate to 100% (in 2000 milliseconds)
                            progress_bar.setProgressCompat(100, true);

                            // Step 5: Continue with the rest of the code after animation
                            progress_bar.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Display the result
                                    // Check if the user is using default profile picture
                                    if(!user.getUri_profile_picture().equals("null")){
                                        Picasso
                                                .get()
                                                .load(user.getUri_profile_picture())
                                                .placeholder(R.drawable.image_placeholder)
                                                .fit()
                                                .centerCrop()
                                                .into(iv_user_picture);

                                        Toast.makeText(Page_User_Profile_Activity.this, "hereeee", Toast.LENGTH_SHORT).show();
                                    }

                                    str_uri_profile_picture = user.getUri_profile_picture();

                                    progress_bar.setProgressCompat(0, false);
                                    progress_bar.setVisibility(View.GONE);
                                    progress_bar.setIndeterminate(true);
                                    result_container.setVisibility(View.VISIBLE);
                                }
                            }, 2000); // The duration of the animation (2 seconds in this case)
                        }
                    }, 0);

                }

                // If the database cannot find user with that email
                else{
                    Toast.makeText(Page_User_Profile_Activity.this, user_info.getString("email", "Could not get email"), Toast.LENGTH_SHORT).show();
                    Toast.makeText(Page_User_Profile_Activity.this, "No such User is found in the database", Toast.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Page_User_Profile_Activity.this, "Error retrieving data from the database. " + error, Toast.LENGTH_LONG).show();
                progress_bar.setVisibility(View.GONE);
            }
        });

    }
}