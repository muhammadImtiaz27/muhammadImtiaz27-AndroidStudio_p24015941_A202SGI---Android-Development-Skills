package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class Page_ToDo_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private SharedPreferences user_info;

    private NestedScrollView result_container;
    private RecyclerView recyclerView;
    private ConstraintLayout cl_empty_container;
    private FloatingActionButton btn_add_todo, btn_sort_todo;

    private RV_Adapter_ToDo toDo_rv_adapter;
    private ArrayList<ToDo> list_of_todos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do);

        user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);

        result_container = findViewById(R.id.result_container);
        recyclerView = findViewById(R.id.recyclerView);
        cl_empty_container = findViewById(R.id.cl_empty_container);
        btn_add_todo = findViewById(R.id.btn_add_todo);
        btn_sort_todo = findViewById(R.id.btn_sort_todo);

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
                    Sidebar_Menu_Functions.open_home_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_ToDo_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_ToDo_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_ToDo_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_ToDo_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
                    // Return to Home page
                    Intent intent = new Intent(Page_ToDo_Activity.this, Page_Home_Page_Activity.class);
                    startActivity(intent);
                    finish(); // Terminate this current activity, and go back to the previous activity

                }
            }
        };

        // Add the callback to the back press dispatcher
        // This is just like callback functions in JavaScript. It is listening for the back button to be clicked
        // When the back button is clicked, the function named callback, is executed.
        getOnBackPressedDispatcher().addCallback(Page_ToDo_Activity.this, callback);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set the layout manager

        // Get all to-do lists from the local database
        getTodoList();

        // DB_Helper_ToDo db = new DB_Helper_ToDo(this); //Database
        // list_of_todos = db.read_all_todos(); //Get all tasks from the database

        // Initialize the adapter and set it to RecyclerView
        // If the database for the todos is not empty
        if(!list_of_todos.isEmpty()){
            cl_empty_container.setVisibility(View.GONE);
            toDo_rv_adapter = new RV_Adapter_ToDo(this, list_of_todos, R.layout.rv_todo_item);
            recyclerView.setAdapter(toDo_rv_adapter);
        }
        else{
            result_container.setVisibility(View.GONE);
            cl_empty_container.setVisibility(View.VISIBLE);
        }

        // For swiping
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(list_of_todos,from,to);
                toDo_rv_adapter.notifyItemMoved(from,to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                list_of_todos.remove(viewHolder.getAdapterPosition());
                toDo_rv_adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        helper.attachToRecyclerView(recyclerView);

        // When the user click the add button
        btn_add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startAddTaskActivity = new Intent(Page_ToDo_Activity.this, Page_ToDo_Add_Activity.class);
                startActivity(startAddTaskActivity);
            }
        });

        // When the user click the sort button
        btn_sort_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create and show the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Page_ToDo_Activity.this);

                // Inflate the layout for the bottom sheet
                View bottomSheetView = getLayoutInflater().inflate(R.layout.menu_bottom_sheet_sort_todo, null);

                // Set the content view for the BottomSheetDialog
                bottomSheetDialog.setContentView(bottomSheetView);

                // Set click listeners for items in the BottomSheetDialog
                TextView tv_sort_todo_title_ascending = bottomSheetView.findViewById(R.id.tv_sort_todo_title_ascending);
                TextView tv_sort_todo_title_descending = bottomSheetView.findViewById(R.id.tv_sort_todo_title_descending);
                TextView tv_sort_todo_date_newest = bottomSheetView.findViewById(R.id.tv_sort_todo_date_newest);
                TextView tv_sort_todo_date_oldest = bottomSheetView.findViewById(R.id.tv_sort_todo_date_oldest);

                tv_sort_todo_title_ascending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Page_ToDo_Activity.this, Page_ToDo_Activity.class);
                        intent.putExtra("key", "title_asc");
                        startActivity(intent);
                        bottomSheetDialog.dismiss(); // Close the bottom sheet dialog after action
                    }
                });

                tv_sort_todo_title_descending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Page_ToDo_Activity.this, Page_ToDo_Activity.class);
                        intent.putExtra("key", "title_desc");
                        startActivity(intent);
                        bottomSheetDialog.dismiss(); // Close the bottom sheet dialog after action
                    }
                });

                tv_sort_todo_date_newest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Page_ToDo_Activity.this, Page_ToDo_Activity.class);
                        intent.putExtra("key", "date_asc");
                        startActivity(intent);
                        bottomSheetDialog.dismiss(); // Close the bottom sheet dialog after action
                    }
                });

                tv_sort_todo_date_oldest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Page_ToDo_Activity.this, Page_ToDo_Activity.class);
                        intent.putExtra("key", "date_desc");
                        startActivity(intent);
                        bottomSheetDialog.dismiss(); // Close the bottom sheet dialog after action
                    }
                });

                // Show the BottomSheetDialog
                bottomSheetDialog.show();

            }
        });

    }

    public void getTodoList(){

        // Clear the array list
        list_of_todos.clear();

        // Database
        DB_Helper_ToDo db = new DB_Helper_ToDo(Page_ToDo_Activity.this);

        // Get data sent from previous activity. Data is sent by the bottom sheet menu (sorting menu)
        Bundle extras = getIntent().getExtras();

        // If there is data sent from previous activity
        if(extras != null){

            // Extract the data sent from previous activity
            String value = extras.getString("key");

            // Do something with that data
            switch(value){
                case "title_asc":
                    list_of_todos = db.read_all_and_sort_title("ASC", user_info.getString("email", "no email"));
                    break;
                case "title_desc":
                    list_of_todos = db.read_all_and_sort_title("DESC", user_info.getString("email", "no email"));
                    break;
                case "date_asc":
                    list_of_todos = db.read_all_and_sort_date("ASC", user_info.getString("email", "no email"));
                    break;
                case "date_desc":
                    list_of_todos = db.read_all_and_sort_date("DESC", user_info.getString("email", "no email"));
                    break;
                default:
                    list_of_todos = db.read_all_todos(user_info.getString("email", "no email"));
            }

        }

        // If no data sent from previous activity
        else{
            list_of_todos = db.read_all_todos(user_info.getString("email", "no email"));
        }

    }

}