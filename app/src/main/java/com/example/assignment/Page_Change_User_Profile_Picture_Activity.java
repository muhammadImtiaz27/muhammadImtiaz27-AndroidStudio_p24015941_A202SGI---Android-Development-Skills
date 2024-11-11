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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Page_Change_User_Profile_Picture_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient googleSignInClient;

    private static final int PICK_THUMBNAIL = 101;

    private DatabaseReference database_reference;
    private FirebaseStorage storage;
    private StorageReference storage_reference;

    private String original_str_uri_profile_picture;
    private String new_str_uri_profile_picture = "";

    private MaterialButton btn_select_profile_picture, btn_save_profile_picture;
    private ShapeableImageView iv_user_profile_picture;
    private CircularProgressIndicator progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_change_user_profile_picture);

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("User");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        // Get data from previous activity
        Intent intent = getIntent();
        original_str_uri_profile_picture = intent.getStringExtra("profile_picture");

        btn_select_profile_picture = findViewById(R.id.btn_select_profile_picture);
        btn_save_profile_picture = findViewById(R.id.btn_save_profile_picture);
        iv_user_profile_picture = findViewById(R.id.iv_user_profile_picture);
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
                    Sidebar_Menu_Functions.open_home_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Change_User_Profile_Picture_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Change_User_Profile_Picture_Activity.this, googleSignInClient);
                }

                else{
                    Toast.makeText(Page_Change_User_Profile_Picture_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
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
        getOnBackPressedDispatcher().addCallback(Page_Change_User_Profile_Picture_Activity.this, callback);

        //Picasso makes loading the images from url easily
        if(original_str_uri_profile_picture.equals("null")){
            Picasso
                    .get()
                    .load(original_str_uri_profile_picture)
                    .placeholder(R.drawable.image_default_profile_picture)
                    .fit()
                    .centerCrop()
                    .into(iv_user_profile_picture);
        }
        else{
            Picasso
                    .get()
                    .load(original_str_uri_profile_picture)
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(iv_user_profile_picture);
        }

        btn_select_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_THUMBNAIL);
            }
        });

        btn_save_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress_bar.setVisibility(View.VISIBLE);

                if(new_str_uri_profile_picture.isEmpty()){
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Please provide a new profile picture.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }
                else{
                    queryProfilePicture();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user was picking an image for the thumbnail only
        if(requestCode == PICK_THUMBNAIL && resultCode == RESULT_OK && data != null){

            // If the user did picked an image
            if(data.getData() != null){

                // Convert the Uri into String, and store it in a variable
                new_str_uri_profile_picture = data.getData().toString();

                //Picasso makes loading the images from url easily
                Picasso
                        .get()
                        .load(new_str_uri_profile_picture)
                        .placeholder(R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(iv_user_profile_picture);
            }

        }

    }

    // Get the image's extension
    private String getFileExtension(String curr_str_uri_photo) {

        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(Uri.parse(curr_str_uri_photo));

        if (mimeType != null) {
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        return "";

    }

    // Step 1: Query the database by name
    private void queryProfilePicture(){

        SharedPreferences user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);

        Query query = database_reference.orderByChild("email").equalTo(user_info.getString("email", "no email"));

        // Query the UserInformation collection in Firebase Realtime Database
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // If user's profile information exist in Realtime Database
                if(snapshot.exists()){
                    DataSnapshot matching_result = snapshot.getChildren().iterator().next();

                    // Now you have the reference to the matching entry, move to step 2
                    deleteOldProfilePictureFromStorage(matching_result);
                }

                // If user's profile information does not exist in Realtime Database
                else{
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "User data does not exist in the database.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }

            }

            // When querying UserInformation failed
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(R.id.dl_drawer_layout), "Query failed: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                progress_bar.setVisibility(View.GONE);
            }
        });

    }

    // Step 2: Delete the old profile picture from Firebase Storage
    private void deleteOldProfilePictureFromStorage(DataSnapshot matching_result) {

        String original_str_uri_profile_picture_db = matching_result.child("uri_profile_picture").getValue(String.class);

        if(original_str_uri_profile_picture_db == null || original_str_uri_profile_picture_db.isEmpty()){
            Toast.makeText(Page_Change_User_Profile_Picture_Activity.this, "No profile picture to delete.", Toast.LENGTH_SHORT).show();
            progress_bar.setVisibility(View.GONE);
            return;
        }

        // Check if key "uri_profile_picture" is equal to "null". It is "null" to tell you the user is using default profile picture
        else if(original_str_uri_profile_picture_db.equals("null")){
            // Proceed to upload the new profile picture
            updateNewProfilePictureToStorage(matching_result);
        }
        else{
            StorageReference image_reference = FirebaseStorage.getInstance().getReferenceFromUrl(original_str_uri_profile_picture_db);

            // Attempt to delete user's old profile picture from Firebase Storage
            image_reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    // If deletion is successful
                    if (task.isSuccessful()) {
                        // Proceed to upload the new profile picture
                        updateNewProfilePictureToStorage(matching_result);
                    }

                    // If deletion is unsuccessful
                    else {
                        Snackbar.make(findViewById(R.id.dl_drawer_layout), "Failed to delete the old profile picture from Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    // Step 3: Upload the new profile picture to Firebase Storage
    private void updateNewProfilePictureToStorage(DataSnapshot matching_result){

        // Before storing the image to FirebaseStorage, get the image's extension
        // getFileExtension() will return strings like "jpg", "png", etc. Without the dot.
        String image_extension = getFileExtension(new_str_uri_profile_picture);

        if(image_extension.isEmpty()){
            image_extension = "." + "jpg";
        }
        else{
            image_extension = "." + image_extension;
        }

        StorageReference file_reference = storage_reference.child("UserGallery/" + System.currentTimeMillis() + image_extension);

        // Attempt to upload user's new profile picture to Firebase Storage in UserGallery folder
        file_reference.putFile(Uri.parse(new_str_uri_profile_picture))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { // If upload was successful
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri download_url) {
                                // Update the profile picture URI in the database
                                updateNewProfilePictureToRealtimeDatabase(matching_result.getRef(), download_url.toString());
                            }
                        });
                    }
                })

                // If uploading new profile picture to Firebase Storage fail
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.dl_drawer_layout), "Failed to upload the new profile picture to Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    // Step 4: Update the photos Uris in Firebase Realtime Database
    private void updateNewProfilePictureToRealtimeDatabase(DatabaseReference reference, String new_str_uri_profile_picture){

        Map<String, Object> updates = new HashMap<>();
        updates.put("uri_profile_picture", new_str_uri_profile_picture);

        reference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progress_bar.setVisibility(View.GONE);
                if (task.isSuccessful()) {

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

                                    Toast.makeText(Page_Change_User_Profile_Picture_Activity.this, "Profile Picture updated successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Page_Change_User_Profile_Picture_Activity.this, Page_Home_Page_Activity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }, 2000); // The duration of the animation (2 seconds in this case)
                        }
                    }, 0);

                }
                // If updating user's profile information in Realtime Database fail
                else {
                    Snackbar.make(findViewById(R.id.dl_drawer_layout), "Failed to update the profile picture: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });

    }

}