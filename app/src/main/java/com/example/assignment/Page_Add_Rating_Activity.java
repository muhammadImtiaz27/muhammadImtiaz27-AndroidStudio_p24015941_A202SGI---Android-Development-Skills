package com.example.assignment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Page_Add_Rating_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private SharedPreferences user_info;

    private TextView tv_user_rating_name, tv_star_label;
    private TextInputEditText et_user_rating_description;
    private MaterialButton btn_upload_user_rating, btn_select_photo;
    private ImageView iv_user_rating_photo, iv_tour_thumbnail;
    private RatingBar rb_user_rating_num_of_stars;
    private CircularProgressIndicator progress_bar;

    private static final int PICK_PHOTO = 101;

    private Uri uri_selected_photo;
    private int selected_num_of_stars;
    String tour_name, tour_thumbnail, user_rating_description;

    // Firebase
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storage_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_add_rating);

        Intent intent = getIntent();
        tour_name = intent.getStringExtra("tour_name");
        tour_thumbnail = intent.getStringExtra("tour_thumbnail");

        user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);
        user_rating_description = "";

        tv_user_rating_name = findViewById(R.id.tv_user_rating_name);
        tv_star_label = findViewById(R.id.tv_star_label);
        et_user_rating_description = findViewById(R.id.et_user_rating_description);
        btn_select_photo = findViewById(R.id.btn_select_photo);
        btn_upload_user_rating = findViewById(R.id.btn_upload_user_rating);
        iv_user_rating_photo = findViewById(R.id.iv_user_rating_photo);
        iv_tour_thumbnail = findViewById(R.id.iv_tour_thumbnail);
        rb_user_rating_num_of_stars = findViewById(R.id.rb_user_rating_num_of_stars);
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
                    Sidebar_Menu_Functions.open_home_page(Page_Add_Rating_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Add_Rating_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Add_Rating_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Add_Rating_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Add_Rating_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Add_Rating_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Add_Rating_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Add_Rating_Activity.this);
                    finish();
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Add_Rating_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Add_Rating_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_Add_Rating_Activity.this, callback);

        // Initialize Firebase Database and Storage
        database = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        //Picasso makes loading the images from url easily
        Picasso
                .get()
                .load(tour_thumbnail)
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .into(iv_tour_thumbnail);

        tv_user_rating_name.setText(tour_name);
        tv_star_label.setText("Average");

        selected_num_of_stars = rb_user_rating_num_of_stars.getNumStars();

        rb_user_rating_num_of_stars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                selected_num_of_stars = Math.round(rating);

                switch (selected_num_of_stars){
                    case 1:
                        tv_star_label.setText("Terrible");
                        break;
                    case 2:
                        tv_star_label.setText("Poor");
                        break;
                    case 3:
                        tv_star_label.setText("Average");
                        break;
                    case 4:
                        tv_star_label.setText("Very Good");
                        break;
                    case 5:
                        tv_star_label.setText("Excellent");
                        break;
                    default:
                        tv_star_label.setText("Error");
                }

            }
        });

        btn_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO);
            }
        });

        btn_upload_user_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_rating_description = et_user_rating_description.getText().toString();

                if(user_rating_description.isEmpty()){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please provide a description.", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(Page_Add_Rating_Activity.this, "Please provide a description.", Toast.LENGTH_LONG).show();
                }
                else if(user_rating_description.length() > 100){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Your description should not be more than 100 characters", Snackbar.LENGTH_LONG).show();
                }
                else if(uri_selected_photo == null){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please select a photo", Snackbar.LENGTH_LONG).show();
                }
                else{
                    progress_bar.setVisibility(View.VISIBLE);

                    // Upload the Image first
                    uploadPhotoToStorage();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user was picking an image for the thumbnail only
        if(requestCode == PICK_PHOTO && resultCode == RESULT_OK && data != null){
            // If the user did picked an image
            if(data.getData() != null){
                uri_selected_photo = data.getData();
                iv_user_rating_photo.setImageURI(uri_selected_photo);
            }
        }

    }

    // Get the image's extension
    private String getFileExtension(Uri curr_image_uri) {

        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(curr_image_uri);

        if (mimeType != null) {
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        return "";

    }

    private void uploadPhotoToStorage() {

        // Before storing the image to FirebaseStorage, get the image's extension
        // getFileExtension() will return strings like "jpg", "png", etc. Without the dot.
        String image_extension = getFileExtension(uri_selected_photo);

        if(image_extension.isEmpty()){
            image_extension = "." + "jpg";
        }
        else{
            image_extension = "." + image_extension;
        }

        // Before we store the image, we need to specify where we want it to be stored.
        // Store the image inside one of the subfolders of TouristSpotGallery folder.
        // TouristSpotGallery/stateNameFolder/imageFileName
        // Furthermore, fileName must be unique. Firebase does not allow more than one image to have the same name.
        // Otherwise, if you upload multiple images with the same file name,
        // the previously saved image with the same file name, will be overwritten by the new image with the same file name.
        // To make images have unique name, we get the current time in milliseconds
        StorageReference file_reference = storage_reference.child("UserRatingsGallery/" + System.currentTimeMillis() + image_extension);

        file_reference.putFile(uri_selected_photo)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get the download URL for the uploaded image
                        file_reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri download_url) {

                                        // The thumbnail url is converted to string, and this string is added to the list
                                        String str_uri_photo = download_url.toString();

                                        // You have stored the image itself into Storage,
                                        // but you need to store its uri into the Realtime Database.
                                        Toast.makeText(Page_Add_Rating_Activity.this, "Photo successfully uploaded into storage.", Toast.LENGTH_SHORT).show();
                                        uploadPhotoToRealTimeDatabase(new UserRatings(user_info.getString("email", "no email"), tour_name, user_rating_description, selected_num_of_stars, str_uri_photo));

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Page_Add_Rating_Activity.this, "Failed to upload photo to storage. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    private void uploadPhotoToRealTimeDatabase(UserRatings new_user_rating) {

        // Reference to "TouristSpot" in the database. TouristSpot is a collection name
        DatabaseReference database_reference = database.getReference("UserRatings");

        // Generate a unique ID for the user
        String user_rating_id = database_reference.push().getKey();

        // Store the user data as a document in the TouristSpot collection
        database_reference.child(user_rating_id).setValue(new_user_rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Steps to turn the progress bar from spinning indefinite to definite, after getting the data successfully
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
                                        progress_bar.setProgressCompat(0, false);
                                        progress_bar.setVisibility(View.GONE);
                                        progress_bar.setIndeterminate(true);

                                        Toast.makeText(Page_Add_Rating_Activity.this, "Data uploaded successfully!", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(Page_Add_Rating_Activity.this, Page_Home_Page_Activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 2000); // The duration of the animation (2 seconds in this case)
                            }
                        }, 0);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Page_Add_Rating_Activity.this, "Failed to upload data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

}