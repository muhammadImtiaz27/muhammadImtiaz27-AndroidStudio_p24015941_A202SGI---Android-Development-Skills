package com.example.assignment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Page_Weather_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private ConstraintLayout cl_result_container;
    private TextView tv_temperature, tv_condition, tv_day_or_night;
    private MaterialButton btn_search_weather_data;
    private EditText et_city_name;
    private ImageView iv_weather_icon;
    private CircularProgressIndicator progress_bar;
    private RecyclerView rv_weather;

    private ArrayList<Weather> weather_rv_dataArrayList;
    private RV_Adapter_Weather weather_rv_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);

        cl_result_container = findViewById(R.id.cl_result_container);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_condition = findViewById(R.id.tv_condition);
        tv_day_or_night = findViewById(R.id.tv_day_or_night);
        btn_search_weather_data = findViewById(R.id.btn_search_weather_data);
        et_city_name = findViewById(R.id.et_city_name);
        iv_weather_icon = findViewById(R.id.iv_weather_icon);
        rv_weather = findViewById(R.id.rv_weather);
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
                    Sidebar_Menu_Functions.open_home_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Weather_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Weather_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Weather_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Weather_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_Weather_Activity.this, callback);

        // Set the LinearLayoutManager to horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_weather.setLayoutManager(layoutManager);

        weather_rv_dataArrayList = new ArrayList<>();
        weather_rv_adapter = new RV_Adapter_Weather(this, weather_rv_dataArrayList);
        rv_weather.setAdapter(weather_rv_adapter);

        btn_search_weather_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city_name = et_city_name.getText().toString();

                if(city_name.isEmpty()){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please provide the name of the city.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    cl_result_container.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.VISIBLE);
                    getWeatherInfo(city_name);
                }
            }
        });

    }

    private String getCityName(double longitude, double latitude){

        String city_name = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try{
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for(Address adr : addresses){
                if(adr != null){
                    String city = adr.getLocality();

                    if(city != null && !city.equals("")){
                        city_name = city;
                    }
                    else{
                        Toast.makeText(this, "CITY NOT FOUND", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return city_name;

    }

    private void getWeatherInfo(String city_name){

        // String url = "https://api.weatherapi.com/v1/forecast.json?key=79513584b6c943a697d85044243009&q=" + city_name + "&days=1&aqi=no&alerts=no";
        String API_base_url = getString(R.string.API_base_url_weather_data);
        String API_key = getString(R.string.API_key_weather_data);

        String url = API_base_url;
        url = url.replace("{APIKEY}", API_key).replace("{CITYNAME}", city_name);

        RequestQueue requestQueue = Volley.newRequestQueue(Page_Weather_Activity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,  // HTTP GET request
                url,  // The URL of the API
                null,  // No request body for GET
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Handle the response
                        // Toast.makeText(Page_Weather_Activity.this, "API Response received!", Toast.LENGTH_SHORT).show();

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
                                        // This runs after the progress reaches 100%
                                        cl_result_container.setVisibility(View.VISIBLE);
                                        progress_bar.setProgressCompat(0, false);
                                        progress_bar.setVisibility(View.GONE);
                                        progress_bar.setIndeterminate(true);

                                        weather_rv_dataArrayList.clear();

                                        try {
                                            // current, temp_c, is_day are all property names defined by the API. Check the API to see all properties and their values.
                                            String temperature = response.getJSONObject("current").getString("temp_c");
                                            int is_day = response.getJSONObject("current").getInt("is_day");
                                            String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                                            String condition_icon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                                            // condition_icon is an image, but it is given as a url. This url is a string
                                            // So we use picasso to make the ImageView (iv_weather_icon) to display the image via the url
                                            Picasso.get().load("https:".concat(condition_icon)).into(iv_weather_icon);

                                            tv_temperature.setText(temperature + "°C");
                                            tv_condition.setText(condition);

                                            // is_day = 1 means it is currently morning.
                                            if(is_day == 1){
                                                tv_day_or_night.setText("It is currently daytime");
                                            }
                                            else{
                                                tv_day_or_night.setText("It is currently nighttime");
                                            }

                                            // forecast, forecastday, hour, time, temp_c, condition, icon, wind_kph are also another property given by the API
                                            // forecastday is inside forecast. forecast day is an array.
                                            // hour is inside forecast day. hour is an array
                                            // Check the API result if you are confused
                                            JSONObject forecastObj = response.getJSONObject("forecast");
                                            JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                                            JSONArray hourArray = forecastO.getJSONArray("hour");

                                            for(int i = 0; i < hourArray.length(); i++){
                                                JSONObject hourObj = hourArray.getJSONObject(i);
                                                String time = hourObj.getString("time");
                                                String temper = hourObj.getString("temp_c");
                                                String img = hourObj.getJSONObject("condition").getString("icon");
                                                String wind_speed = hourObj.getString("wind_kph");
                                                weather_rv_dataArrayList.add(new Weather(img, temper, time, wind_speed));
                                            }

                                            weather_rv_adapter.notifyDataSetChanged();

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                }, 2000); // The duration of the animation (2 seconds in this case)
                            }
                        }, 0);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Snackbar.make(findViewById(R.id.dl_drawer_layout), "Failed to get API response.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }

}