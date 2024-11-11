package com.example.assignment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Page_Currency_Converter_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private TextInputEditText et_currency_amount;
    private PowerSpinnerView sp_countries_from, sp_countries_to;
    private MaterialButton btn_convert_currency;
    private MaterialCardView card_result;
    private TextView tv_currency_converter_result;
    private FloatingActionButton btn_info;
    private CircularProgressIndicator progress_bar;

    // Custom Dialog
    private Dialog dialog;
    TextView tv_dialog_box_message;
    TextView tv_btn_close;

    private String str_sp_currency_from = "";
    private String str_sp_currency_to = "";
    private String currency_code_from = "";
    private String currency_code_to = "";

    // Storing API results
    double currency_from = 0.0;
    double currency_to = 0.0;
    double result = 0.0;
    String last_updated = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_currency_converter);

        et_currency_amount = findViewById(R.id.et_currency_amount);
        sp_countries_from = findViewById(R.id.sp_countries_from);
        sp_countries_to = findViewById(R.id.sp_countries_to);
        btn_convert_currency = findViewById(R.id.btn_convert_currency);
        card_result = findViewById(R.id.card_result);
        tv_currency_converter_result = findViewById(R.id.tv_currency_convert_result);
        btn_info = findViewById(R.id.btn_info);
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
                    Sidebar_Menu_Functions.open_home_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Currency_Converter_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Currency_Converter_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Currency_Converter_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Currency_Converter_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_Currency_Converter_Activity.this, callback);

        // Create all the spinner items. Each item will have a text and image (flag)
        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
        iconSpinnerItems.add(new IconSpinnerItem("India (INR)", getDrawable(R.drawable.flag_india)));
        iconSpinnerItems.add(new IconSpinnerItem("Indonesia (IDR)", getDrawable(R.drawable.flag_indonesia)));
        iconSpinnerItems.add(new IconSpinnerItem("Japan (JPY)", getDrawable(R.drawable.flag_japan)));
        iconSpinnerItems.add(new IconSpinnerItem("South Korea (KRW)", getDrawable(R.drawable.flag_south_korea)));
        iconSpinnerItems.add(new IconSpinnerItem("Malaysia (MYR)", getDrawable(R.drawable.flag_malaysia)));
        iconSpinnerItems.add(new IconSpinnerItem("Russia (RUB)", getDrawable(R.drawable.flag_russia)));
        iconSpinnerItems.add(new IconSpinnerItem("Saudi Arabia (SAR)", getDrawable(R.drawable.flag_saudi)));
        iconSpinnerItems.add(new IconSpinnerItem("Singapore (SGD)", getDrawable(R.drawable.flag_singapore)));
        iconSpinnerItems.add(new IconSpinnerItem("United Arab Emirates (AED)", getDrawable(R.drawable.flag_uae)));
        iconSpinnerItems.add(new IconSpinnerItem("United States of America (USD)", getDrawable(R.drawable.flag_usa)));

        // Put all those items into FROM spinner
        IconSpinnerAdapter iconSpinnerAdapter_from = new IconSpinnerAdapter(sp_countries_from);
        sp_countries_from.setSpinnerAdapter(iconSpinnerAdapter_from);
        sp_countries_from.setItems(iconSpinnerItems);
        sp_countries_from.selectItemByIndex(0);
        sp_countries_from.setLifecycleOwner(this);

        // Put all those items into TO spinner
        IconSpinnerAdapter iconSpinnerAdapter_to = new IconSpinnerAdapter(sp_countries_to);
        sp_countries_to.setSpinnerAdapter(iconSpinnerAdapter_to);
        sp_countries_to.setItems(iconSpinnerItems);
        sp_countries_to.selectItemByIndex(4);
        sp_countries_to.setLifecycleOwner(this);

//        currency_from = (String) sp_countries_from.getText();
//        currency_to = (String) sp_countries_to.getText();

        // When the user select one of the item from the FROM spinner
//        sp_countries_from.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
//            @Override
//            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex, IconSpinnerItem newItem) {
//                Toast.makeText(Page_Currency_Converter_Activity.this, newItem.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        // When the user select one of the item from the TO spinner
//        sp_countries_to.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
//            @Override
//            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex, IconSpinnerItem newItem) {
//                Toast.makeText(Page_Currency_Converter_Activity.this, newItem.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        btn_convert_currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                card_result.setVisibility(View.GONE);
                btn_info.setVisibility(View.GONE);
                progress_bar.setVisibility(View.VISIBLE);

                double currency_amount = 0.0;

                // Extract the values from the EditText and the two spinners
                String str_currency_amount = String.valueOf(et_currency_amount.getText());
                str_sp_currency_from = (String) sp_countries_from.getText();
                str_sp_currency_to = (String) sp_countries_to.getText();

                // Convert to lowercase
                str_sp_currency_from = str_sp_currency_from.toLowerCase();
                str_sp_currency_to = str_sp_currency_to.toLowerCase();

                if(str_currency_amount.isEmpty()){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please provide the currency amount.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                    return;
                }
                else if(str_sp_currency_to.equals(str_sp_currency_from)){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Your selected currencies cannot be the same!", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                    return;
                }

                try {
                    currency_amount = Double.parseDouble(str_currency_amount);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace(); // Handle the exception if the string is not a valid double
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Your currency amount is not valid!", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                    return;
                }

                // Get the currency code for both FROM and TO
                currency_code_from = getCurrencyCode(str_sp_currency_from);
                currency_code_to = getCurrencyCode(str_sp_currency_to);
                // I think you should check if the currency_code_to and currency_code_from returns "", just in case.

                // Call the API to convert the currency
                convertCurrency(currency_amount, currency_code_from, currency_code_to);

            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBox();
            }
        });

    }

    void openDialogBox() {
        // Create the dialog
        dialog = new Dialog(Page_Currency_Converter_Activity.this);
        dialog.setContentView(R.layout.custom_dialog_box_currency_converter);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // The user is able to close the dialog box by clicking anywhere outside the dialog box
        dialog.setCancelable(true);

        // Use dialog.findViewById() to get views from the dialog layout
        tv_dialog_box_message = dialog.findViewById(R.id.tv_dialog_box_message);
        tv_btn_close = dialog.findViewById(R.id.tv_btn_close);

        // Format the message
        String message = String.format("%s: %s %n%n%s: %s %n%nLast updated: (%s)",
                str_sp_currency_from.toUpperCase(), currency_from,
                str_sp_currency_to.toUpperCase(), currency_to,
                last_updated);

        tv_dialog_box_message.setText(message);

        // Close the dialog when the close button is clicked
        tv_btn_close.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    void convertCurrency(double currency_amount, String currency_code_from, String currency_code_to){

        String API_key = getString(R.string.API_key_currency_converter);
        String API_base_url = getString(R.string.API_base_url_currency_converter);

        String url = API_base_url + API_key + "&currencies=" + currency_code_to + "," + currency_code_from;

        RequestQueue requestQueue = Volley.newRequestQueue(Page_Currency_Converter_Activity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,  // HTTP GET request
                url,  // The URL of the API
                null,  // No request body for GET
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        Toast.makeText(Page_Currency_Converter_Activity.this, "API Response received!", Toast.LENGTH_SHORT).show();

                        try {

                            // Extract "meta" object from the response
                            JSONObject meta_object = response.getJSONObject("meta");
                            last_updated = meta_object.getString("last_updated_at");

                            // Format the date properly
                            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault());

                            try {
                                // Parse the date from the API
                                Date date = originalFormat.parse(last_updated);

                                // Format the date to the desired format
                                last_updated = targetFormat.format(date);
                            }
                            catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            // Extract "data" object from the response
                            JSONObject data_object = response.getJSONObject("data");

                            // The "data" object has two objects, and each object has two properties in it ("code" and "value"). We are only interested in the "value" property.
                            // Example:
                            // "data": {
                            //     "MYR": {
                            //         "code": "MYR",
                            //         "value": 4.1233505471
                            //     },
                            //     "USD": {
                            //         "code": "USD",
                            //         "value": 1
                            //     }
                            // }
                            //

                            Iterator<String> keys = data_object.keys();

                            // First object
                            String first_key_name = keys.next();
                            JSONObject first_key_object = data_object.getJSONObject(first_key_name);

                            // Second object
                            String second_key_name = keys.next();
                            JSONObject second_key_object = data_object.getJSONObject(second_key_name);

                            if(first_key_object.getString("code").equals(currency_code_to)){
                                currency_to = first_key_object.getDouble("value");
                                currency_from = second_key_object.getDouble("value");
                            }
                            else if(second_key_object.getString("code").equals(currency_code_to)){
                                currency_to = second_key_object.getDouble("value");
                                currency_from = first_key_object.getDouble("value");
                            }

                            // Round off to two decimal places
                            // df.format() will return a string, so we need to convert it to double
                            DecimalFormat df = new DecimalFormat("#.##");
                            currency_from = Double.parseDouble(df.format(currency_from));
                            currency_to = Double.parseDouble(df.format(currency_to));

                            // Calculate the final result
                            result = currency_amount * (currency_to / currency_from);

                            // Round off result to two decimal places as well
                            result = Double.parseDouble(df.format(result));

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
                                            tv_currency_converter_result.setText(String.format("(%s) %s", currency_code_to, String.valueOf(result)));

                                            card_result.setVisibility(View.VISIBLE);
                                            btn_info.setVisibility(View.VISIBLE);

                                            progress_bar.setProgressCompat(0, false);
                                            progress_bar.setVisibility(View.GONE);
                                            progress_bar.setIndeterminate(true);
                                        }
                                    }, 2000); // The duration of the animation (2 seconds in this case)
                                }
                            }, 0);

                            // Btw, besides "data", there's also "meta" object, where it has a property "last_updated_at". This property can be useful. Figure out how to include this in the layout

                        } catch (JSONException e) {
                            progress_bar.setVisibility(View.GONE);
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Snackbar.make(findViewById(R.id.dl_drawer_layout), "Failed to get API response", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }

    String getCurrencyCode(String currency){

        String currency_code = "";

        switch (currency) {
            case "india (inr)":
                // Indian Rupee
                currency_code = "INR";
                break;

            case "indonesia (idr)":
                // Indonesian Rupiah
                currency_code = "IDR";
                break;

            case "japan (jpy)":
                // Japanese Yen
                currency_code = "JPY";
                break;

            case "south korea (krw)":
                // South Korean Won
                currency_code = "KRW";
                break;

            case "malaysia (myr)":
                // Malaysian Ringgit
                currency_code = "MYR";
                break;

            case "russia (rub)":
                // Russian Ruble
                currency_code = "RUB";
                break;

            case "saudi arabia (sar)":
                // Saudi Riyal
                currency_code = "SAR";
                break;

            case "singapore (sgd)":
                // Singapore Dollar
                currency_code = "SGD";
                break;

            case "united arab emirates (aed)":
                // United Arab Emirates Dirham
                currency_code = "AED";
                break;

            case "united states of america (usd)":
                // US Dollar
                currency_code = "USD";
                break;

            default:
                break;
        }

        return currency_code;

    }

}