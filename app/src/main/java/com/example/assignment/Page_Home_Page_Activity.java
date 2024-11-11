package com.example.assignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Page_Home_Page_Activity extends AppCompatActivity {

    private SharedPreferences user_info;

    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;

    private TextInputEditText et_search_tourist_spot_name;
    private MaterialCardView card_museum, card_hotel, card_history, card_wildlife, card_park, card_restaurant;
    private RecyclerView rv_tourist_spots, rv_todo;
    MaterialCardView card_error_discover, card_error_todo;
    TextView tv_error_discover, tv_error_todo;

    private ShimmerFrameLayout shimmer_container_discovery, shimmer_container_todo;

    private View header_view;

    private DatabaseReference database_reference;
    private List<TouristSpot> list_of_tourist_spots;
    private RV_Adapter_Discovery discovery_rv_adapter;

    private ArrayList<ToDo> list_of_todos;
    private RV_Adapter_ToDo toDo_rv_adapter;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;

    // Custom Dialog for Logout
    private Dialog dialog;
    private TextView tv_btn_yes, tv_btn_no;

    // SIDEBAR MENU
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // SIDEBAR MENU - Handle hamburger icon click to open/close the drawer
        if (drawer_toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page); // Ensure this is your layout file

        user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);

        dl_drawer_layout = findViewById(R.id.dl_drawer_layout);
        nv_main_menu = findViewById(R.id.nv_main_menu);
        toolbar = findViewById(R.id.toolbar);

        et_search_tourist_spot_name = findViewById(R.id.et_search_tourist_spot_name);

        card_museum = findViewById(R.id.card_museum);
        card_hotel = findViewById(R.id.card_hotel);
        card_history = findViewById(R.id.card_history);
        card_wildlife = findViewById(R.id.card_wildlife);
        card_park = findViewById(R.id.card_park);
        card_restaurant = findViewById(R.id.card_restaurant);

        rv_tourist_spots = findViewById(R.id.rv_tourist_spots);
        rv_todo = findViewById(R.id.rv_todo);
        card_error_discover = findViewById(R.id.card_error_discover);
        card_error_todo = findViewById(R.id.card_error_todo);
        tv_error_discover = findViewById(R.id.tv_error_discover);
        tv_error_todo = findViewById(R.id.tv_error_todo);

        header_view = nv_main_menu.getHeaderView(0);

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");
        list_of_tourist_spots = new ArrayList<>();

        shimmer_container_discovery = findViewById(R.id.shimmer_container_discovery);
        shimmer_container_todo = findViewById(R.id.shimmer_container_todo);

        list_of_todos = new ArrayList<>();

        setSupportActionBar(toolbar);

        // SIDEBAR MENU - Initialize ActionBarDrawerToggle with custom icon
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

        // SIDEBAR MENU - Enable the hamburger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv_main_menu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Close the drawer menu after the user click one of the options
                dl_drawer_layout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();

                if (id == R.id.menu_home) {
                    Sidebar_Menu_Functions.open_home_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Home_Page_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Home_Page_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Home_Page_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Home_Page_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

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

                // This is the default phone behavior
                // If the main menu is not opened, and then the user click the back button,
                // then go back to the previous activity or close the app
                else {
                    showLogoutDialogBox();
                }
            }
        };

        // Add the callback to the back press dispatcher
        // This is just like callback functions in JavaScript. It is listening for the back button to be clicked
        // When the back button is clicked, the function named callback, is executed.
        getOnBackPressedDispatcher().addCallback(Page_Home_Page_Activity.this, callback);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.client_id)) // Ensure you have the correct client ID
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // For displaying Tourist Spot in the recyclerview
        getDataFromFirebase();

        // For displaying Tourist Spot in the recyclerview
        getDataFromLocalDatabase();

        // Listen for specific keyboard actions (in this case, the Enter key or Done key)
        et_search_tourist_spot_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {

                // Check if the key pressed is Done or Enter
                if(actionID == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)){

                    // Make the keyboard disappear after the Enter or Done key is pressed
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    String search = et_search_tourist_spot_name.getText().toString();
                    if(search.isEmpty()){
                        Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please provide the name of the tourist spot.", Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                        intent.putExtra("name", search);
                        startActivity(intent);
                    }

                    // Indicates the action has been handled
                    return true;

                }

                // If it is neither the Done and Enter key is entered, do nothing
                return false;
            }
        });

        card_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                intent.putExtra("selected_category", "Hotel");
                startActivity(intent);
            }
        });

        card_park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                intent.putExtra("selected_category", "Park");
                startActivity(intent);
            }
        });

        card_museum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                intent.putExtra("selected_category", "Museum");
                startActivity(intent);
            }
        });

        card_wildlife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                intent.putExtra("selected_category", "Wildlife");
                startActivity(intent);
            }
        });

        card_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                intent.putExtra("selected_category", "Historical");
                startActivity(intent);
            }
        });

        card_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Home_Page_Activity.this, Page_Search_TouristSpot_Activity.class);
                intent.putExtra("selected_category", "Restaurant");
                startActivity(intent);
            }
        });

    }

    private void getDataFromLocalDatabase(){

        // Clear the array list
        list_of_todos.clear();

        // Database
        DB_Helper_ToDo db = new DB_Helper_ToDo(Page_Home_Page_Activity.this);

        list_of_todos = db.read_all_todos(user_info.getString("email", "no email"));

        shimmer_container_todo.stopShimmer();
        shimmer_container_todo.setVisibility(View.GONE);

        if(list_of_todos.isEmpty()){
            rv_todo.setVisibility(View.GONE);
            tv_error_todo.setText("You currently have zero todos");
            card_error_todo.setVisibility(View.VISIBLE);
        }
        else{
            rv_todo.setVisibility(View.VISIBLE);
            card_error_todo.setVisibility(View.GONE);
            setRecyclerView_Todo(list_of_todos);
        }

    }

    private void setRecyclerView_Todo(ArrayList<ToDo> list_of_todos) {

        // Set up the LayoutManager to make the RecyclerView horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_todo.setLayoutManager(layoutManager);

        toDo_rv_adapter = new RV_Adapter_ToDo(this, list_of_todos, R.layout.rv_todo_horizontal_item);
        rv_todo.setAdapter(toDo_rv_adapter);

    }

    private void getDataFromFirebase() {

        // Get the 3 recently added tourist spots
        Query query = database_reference.limitToLast(3);

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

                setRecyclerView_Discovery(list_of_tourist_spots);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmer_container_discovery.stopShimmer();
                shimmer_container_discovery.setVisibility(View.GONE);
                rv_tourist_spots.setVisibility(View.GONE);
                tv_error_discover.setText("Error occurend when receiving data from database");
                card_error_discover.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setRecyclerView_Discovery(List<TouristSpot> list_of_tourist_spots) {

        // Set up the LayoutManager to make the RecyclerView horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_tourist_spots.setLayoutManager(layoutManager);

        // Initialize the adapter
        discovery_rv_adapter = new RV_Adapter_Discovery(this, list_of_tourist_spots, R.layout.rv_discovery_item);

        // Set the adapter to the RecyclerView
        rv_tourist_spots.setAdapter(discovery_rv_adapter);

        shimmer_container_discovery.stopShimmer();
        shimmer_container_discovery.setVisibility(View.GONE);
        rv_tourist_spots.setVisibility(View.VISIBLE);

        if(list_of_tourist_spots.isEmpty()){
            tv_error_discover.setText("Zero tourist spot in the database.");
            card_error_discover.setVisibility(View.VISIBLE);
        }

    }

    private void showLogoutDialogBox() {

        // Create the dialog
        dialog = new Dialog(Page_Home_Page_Activity.this);
        dialog.setContentView(R.layout.custom_dialog_box_logout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // The user is able to close the dialog box by clicking anywhere outside the dialog box
        dialog.setCancelable(true);

        // Use dialog.findViewById() to get views from the dialog layout
        tv_btn_yes = dialog.findViewById(R.id.tv_btn_yes);
        tv_btn_no = dialog.findViewById(R.id.tv_btn_no);


        // Close the dialog when the No button is clicked
        tv_btn_no.setOnClickListener(v -> dialog.dismiss());

        // Logout when the Yes button is clicked
        tv_btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Sidebar_Menu_Functions.logout(Page_Home_Page_Activity.this, googleSignInClient);
            }
        });

        // Show the dialog
        dialog.show();

    }

//    @Override
//    public void onBackPressed() {
//        if(dl_drawer_layout.isDrawerOpen(GravityCompat.START)){
//            dl_drawer_layout.closeDrawer(GravityCompat.START);
//        }
//        else{
//            super.onBackPressed();
//        }
//    }
}
