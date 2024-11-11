package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Page_View_All_Ratings_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private DatabaseReference database_reference;

    private LinearLayout ll_result_container;
    private Button btn_add_rating;
    private RecyclerView rv_user_ratings;
    private CircularProgressIndicator progress_bar;

    Fragment_Rating_Review fragment_rating_review;

    private List<UserRatings> list_of_user_ratings;
    private RV_Adapter_User_Ratings rv_adapter_user_ratings;

    private String tour_name, tour_thumbnail;

    int star_1_counter = 0;
    int star_2_counter = 0;
    int star_3_counter = 0;
    int star_4_counter = 0;
    int star_5_counter = 0;
    float average;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_view_all_ratings);

        Intent intent = getIntent();
        tour_name = intent.getStringExtra("tour_name");
        tour_thumbnail = intent.getStringExtra("tour_thumbnail");

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("UserRatings");
        list_of_user_ratings = new ArrayList<>();

        ll_result_container = findViewById(R.id.ll_result_container);
        btn_add_rating = findViewById(R.id.btn_add_rating);
        rv_user_ratings = findViewById(R.id.rv_user_ratings);
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
                    Sidebar_Menu_Functions.open_home_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_View_All_Ratings_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_View_All_Ratings_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_View_All_Ratings_Activity.this);
                    finish();
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_View_All_Ratings_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_View_All_Ratings_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_View_All_Ratings_Activity.this, callback);

        getDataFromDatabase();

        btn_add_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_View_All_Ratings_Activity.this, Page_Add_Rating_Activity.class);
                intent.putExtra("tour_name", tour_name);
                intent.putExtra("tour_thumbnail", tour_thumbnail);
                startActivity(intent);
            }
        });

    }

    private void getDataFromDatabase() {

        // Get the tourist spot with key "name" with value of the name variable
        Query query = database_reference.orderByChild("tour_name").equalTo(tour_name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Clear the list to avoid duplicate data
                list_of_user_ratings.clear();

                long num_of_raters = snapshot.getChildrenCount();
                int total = 0;

                // Loop through all the children of the TouristSpot node
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    // Add each child to the list
                    list_of_user_ratings.add(dataSnapshot.getValue(UserRatings.class));

                    total = total + dataSnapshot.getValue(UserRatings.class).getNum_of_stars();

                    switch (dataSnapshot.getValue(UserRatings.class).getNum_of_stars()){
                        case 1:
                            star_1_counter++;
                            break;
                        case 2:
                            star_2_counter++;
                            break;
                        case 3:
                            star_3_counter++;
                            break;
                        case 4:
                            star_4_counter++;
                            break;
                        case 5:
                            star_5_counter++;
                            break;
                    }

                }

                average = (float) total / num_of_raters;
                average = (float) (Math.round(average * 100.0) / 100.0);

                setRatingReviewFragment();
                setRecyclerView_Search(list_of_user_ratings);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress_bar.setVisibility(View.GONE);
                ll_result_container.setVisibility(View.GONE);
                Toast.makeText(Page_View_All_Ratings_Activity.this, "Error occurred when getting data from the database", Toast.LENGTH_SHORT).show();
                // tv_error_discover.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setRatingReviewFragment(){

        fragment_rating_review = Fragment_Rating_Review.newInstance(star_1_counter, star_2_counter, star_3_counter, star_4_counter, star_5_counter, average);

        // Add the fragment to the activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_rating_review, fragment_rating_review)
                .commit();

    }

    private void setRecyclerView_Search(List<UserRatings> list_of_user_ratings){

        // Set up the LayoutManager to make the RecyclerView vertical
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_user_ratings.setLayoutManager(layoutManager);

        // Initialize the adapter
        rv_adapter_user_ratings = new RV_Adapter_User_Ratings(this, list_of_user_ratings);

        // Set the adapter to the RecyclerView
        rv_user_ratings.setAdapter(rv_adapter_user_ratings);

        progress_bar.setVisibility(View.GONE);
        ll_result_container.setVisibility(View.VISIBLE);

    }

}