package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Page_Search_TouristSpot_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private TextInputEditText et_search_tourist_spot_name;
    private Chip chip_state, chip_category;
    private MaterialButton btn_search_tourist_spot;
    private RecyclerView rv_search_tourist_spot;
    private ConstraintLayout cl_empty_container;
    private CircularProgressIndicator progress_bar;

    private String selected_state, selected_category;

    private DatabaseReference database_reference;
    private List<TouristSpot> list_of_tourist_spots;
    private RV_Adapter_Discovery discovery_rv_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_search_tourist_spot);

        et_search_tourist_spot_name = findViewById(R.id.et_search_tourist_spot_name);
        chip_state = findViewById(R.id.chip_state);
        chip_category = findViewById(R.id.chip_category);
        btn_search_tourist_spot = findViewById(R.id.btn_search_tourist_spot);

        cl_empty_container = findViewById(R.id.cl_empty_container);

        rv_search_tourist_spot = findViewById(R.id.rv_search_tourist_spot);
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
                    Sidebar_Menu_Functions.open_home_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Search_TouristSpot_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Search_TouristSpot_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Search_TouristSpot_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Search_TouristSpot_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_Search_TouristSpot_Activity.this, callback);

        selected_state = "";
        selected_category = "";

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");
        list_of_tourist_spots = new ArrayList<>();

        // Get tourist spot name sent by Home page
        Intent intent = getIntent();

        // Check if the previous activity has sent any data with key "name"
        if(intent.hasExtra("name")){
            // Set the EditText to that value
            et_search_tourist_spot_name.setText(intent.getStringExtra("name"));

            generateQuery();
        }

        // If not, check if the previous activity has sent any data with key "selected_category"
        else if(intent.hasExtra("selected_category")){
            selected_category = intent.getStringExtra("selected_category");
            chip_category.setChipIconVisible(true);
            chip_category.setText(selected_category);
            generateQuery();
        }

        chip_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create and show the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Page_Search_TouristSpot_Activity.this);

                // Inflate the layout for the bottom sheet
                View bottomSheetView = getLayoutInflater().inflate(R.layout.menu_bottom_sheet_states, null);

                // Set the content view for the BottomSheetDialog
                bottomSheetDialog.setContentView(bottomSheetView);

                // Set click listeners for items in the BottomSheetDialog
                LinearLayout ll_state_none = bottomSheetView.findViewById(R.id.ll_state_none);
                LinearLayout ll_state_johor = bottomSheetView.findViewById(R.id.ll_state_johor);
                LinearLayout ll_state_kedah = bottomSheetView.findViewById(R.id.ll_state_kedah);
                LinearLayout ll_state_kelantan = bottomSheetView.findViewById(R.id.ll_state_kelantan);
                LinearLayout ll_state_melaka = bottomSheetView.findViewById(R.id.ll_state_melaka);
                LinearLayout ll_state_negeri_sembilan = bottomSheetView.findViewById(R.id.ll_state_negeri_sembilan);
                LinearLayout ll_state_pahang = bottomSheetView.findViewById(R.id.ll_state_pahang);
                LinearLayout ll_state_perak = bottomSheetView.findViewById(R.id.ll_state_perak);
                LinearLayout ll_state_perlis = bottomSheetView.findViewById(R.id.ll_state_perlis);
                LinearLayout ll_state_pulau_pinang = bottomSheetView.findViewById(R.id.ll_state_pulau_pinang);
                LinearLayout ll_state_sabah = bottomSheetView.findViewById(R.id.ll_state_sabah);
                LinearLayout ll_state_sarawak = bottomSheetView.findViewById(R.id.ll_state_sarawak);
                LinearLayout ll_state_selangor = bottomSheetView.findViewById(R.id.ll_state_selangor);
                LinearLayout ll_state_terengganu = bottomSheetView.findViewById(R.id.ll_state_terengganu);

                ll_state_none.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "";
                        chip_state.setChipIconVisible(false);
                        chip_state.setText("Select a state");
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_johor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Johor";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_kedah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Kedah";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_kelantan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Kelantan";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_melaka.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Melaka";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_negeri_sembilan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Negeri Sembilan";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_pahang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Pahang";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_perak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Perak";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_perlis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Perlis";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_pulau_pinang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Pulau Pinang";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_sabah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Sabah";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_sarawak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Sarawak";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_selangor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Selangor";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_state_terengganu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_state = "Terengganu";
                        chip_state.setChipIconVisible(true);
                        chip_state.setText(selected_state);
                        bottomSheetDialog.dismiss();
                    }
                });

                // Show the BottomSheetDialog
                bottomSheetDialog.show();

            }
        });

        chip_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create and show the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Page_Search_TouristSpot_Activity.this);

                // Inflate the layout for the bottom sheet
                View bottomSheetView = getLayoutInflater().inflate(R.layout.menu_bottom_sheet_category, null);

                // Set the content view for the BottomSheetDialog
                bottomSheetDialog.setContentView(bottomSheetView);

                // Set click listeners for items in the BottomSheetDialog
                LinearLayout ll_category_none = bottomSheetView.findViewById(R.id.ll_category_none);
                LinearLayout ll_category_historical = bottomSheetView.findViewById(R.id.ll_category_historical);
                LinearLayout ll_category_hotel = bottomSheetView.findViewById(R.id.ll_category_hotel);
                LinearLayout ll_category_museum = bottomSheetView.findViewById(R.id.ll_category_museum);
                LinearLayout ll_category_park = bottomSheetView.findViewById(R.id.ll_category_park);
                LinearLayout ll_category_restaurant = bottomSheetView.findViewById(R.id.ll_category_restaurant);
                LinearLayout ll_category_wildlife = bottomSheetView.findViewById(R.id.ll_category_wildlife);

                ll_category_none.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selected_category = "";
                        chip_category.setChipIconVisible(false);
                        chip_category.setText("Select a category");
                        bottomSheetDialog.dismiss();

                    }
                });

                ll_category_historical.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_category = "Historical";
                        chip_category.setChipIconVisible(true);
                        chip_category.setText(selected_category);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_category_hotel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_category = "Hotel";
                        chip_category.setChipIconVisible(true);
                        chip_category.setText(selected_category);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_category_museum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_category = "Museum";
                        chip_category.setChipIconVisible(true);
                        chip_category.setText(selected_category);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_category_park.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_category = "Park";
                        chip_category.setChipIconVisible(true);
                        chip_category.setText(selected_category);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_category_restaurant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_category = "Restaurant";
                        chip_category.setChipIconVisible(true);
                        chip_category.setText(selected_category);
                        bottomSheetDialog.dismiss();
                    }
                });

                ll_category_wildlife.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_category = "Wildlife";
                        chip_category.setChipIconVisible(true);
                        chip_category.setText(selected_category);
                        bottomSheetDialog.dismiss();
                    }
                });

                // Show the BottomSheetDialog
                bottomSheetDialog.show();

            }
        });

        btn_search_tourist_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQuery();
            }
        });
    }

    private void executeFirebaseQuery(Query query){

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Clear the list to avoid duplicate data
                list_of_tourist_spots.clear();

                // Loop through all the children of the TouristSpot node
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    // Add each child to the list
                    list_of_tourist_spots.add(dataSnapshot.getValue(TouristSpot.class));
                }

                setRecyclerView_Search();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress_bar.setVisibility(View.GONE);
                rv_search_tourist_spot.setVisibility(View.GONE);
                Toast.makeText(Page_Search_TouristSpot_Activity.this, "Error occurred when getting data from the database", Toast.LENGTH_SHORT).show();
                // tv_error_discover.setVisibility(View.VISIBLE);
            }
        });

    }

    private void executeFilteredFirebaseQuery(Query query, String filter_key, String filter_value){

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the list to avoid duplicate data
                list_of_tourist_spots.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    TouristSpot tourist_spot = snapshot.getValue(TouristSpot.class);
                    if (tourist_spot != null && filter_value.equals(snapshot.child(filter_key).getValue(String.class))) {
                        // Add tourist spot to the list
                        list_of_tourist_spots.add(snapshot.getValue(TouristSpot.class));
                    }

                }

                setRecyclerView_Search();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress_bar.setVisibility(View.GONE);
                rv_search_tourist_spot.setVisibility(View.GONE);
                Toast.makeText(Page_Search_TouristSpot_Activity.this, "Error occurred when getting data from the database", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void executeFilteredFirebaseQuery(Query query, String filter_key_1, String filter_value_1, String filter_key_2, String filter_value_2){

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the list to avoid duplicate data
                list_of_tourist_spots.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    TouristSpot tourist_spot = snapshot.getValue(TouristSpot.class);
                    if (tourist_spot != null
                            && filter_value_1.equals(snapshot.child(filter_key_1).getValue(String.class))
                            && filter_value_2.equals(snapshot.child(filter_key_2).getValue(String.class)))
                    {
                        // Add tourist spot to the list
                        list_of_tourist_spots.add(snapshot.getValue(TouristSpot.class));
                    }

                }

                setRecyclerView_Search();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress_bar.setVisibility(View.GONE);
                rv_search_tourist_spot.setVisibility(View.GONE);
                Toast.makeText(Page_Search_TouristSpot_Activity.this, "Error occurred when getting data from the database", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void generateQuery(){

        String selected_name = et_search_tourist_spot_name.getText().toString();

        // No input is provided
        if(selected_name.isEmpty() && selected_state.isEmpty() && selected_category.isEmpty()){
            Query query = database_reference;
            executeFirebaseQuery(query);
        }

        // Only the name is provided
        else if (!selected_name.isEmpty() && selected_state.isEmpty() && selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("name").equalTo(selected_name);
            executeFirebaseQuery(query);
        }

        // Only the state is provided
        else if (selected_name.isEmpty() && !selected_state.isEmpty() && selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("state").equalTo(selected_state);
            executeFirebaseQuery(query);
        }

        // Only the category is provided
        else if (selected_name.isEmpty() && selected_state.isEmpty() && !selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("category").equalTo(selected_category);
            executeFirebaseQuery(query);
        }

        // Only the state and category are provided
        else if (selected_name.isEmpty() && !selected_state.isEmpty() && !selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("state").equalTo(selected_state);
            executeFilteredFirebaseQuery(query, "category", selected_category);
        }

        // Only the name and category are provided
        else if (!selected_name.isEmpty() && selected_state.isEmpty() && !selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("name").equalTo(selected_name);
            executeFilteredFirebaseQuery(query, "category", selected_category);
        }

        // Only the name and state are provided
        else if (!selected_name.isEmpty() && !selected_state.isEmpty() && selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("name").equalTo(selected_name);
            executeFilteredFirebaseQuery(query, "state", selected_state);
        }

        // Name, state and category are provided
        else if (!selected_name.isEmpty() && !selected_state.isEmpty() && !selected_category.isEmpty()) {
            Query query = database_reference.orderByChild("name").equalTo(selected_name);
            executeFilteredFirebaseQuery(query, "state", selected_state, "category", selected_category);
        }

    }

    private void setRecyclerView_Search() {

        if(list_of_tourist_spots.isEmpty()){
            rv_search_tourist_spot.setVisibility(View.GONE);
            cl_empty_container.setVisibility(View.VISIBLE);
        }
        else{

            // Set up the LayoutManager to make the RecyclerView horizontal
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rv_search_tourist_spot.setLayoutManager(layoutManager);

            // Initialize the adapter
            discovery_rv_adapter = new RV_Adapter_Discovery(this, list_of_tourist_spots, R.layout.rv_search_tourist_spot_item);

            // Set the adapter to the RecyclerView
            rv_search_tourist_spot.setAdapter(discovery_rv_adapter);

            rv_search_tourist_spot.setVisibility(View.VISIBLE);
            cl_empty_container.setVisibility(View.GONE);

        }

    }
}