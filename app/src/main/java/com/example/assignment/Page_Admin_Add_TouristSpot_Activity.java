package com.example.assignment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;

public class Page_Admin_Add_TouristSpot_Activity extends AppCompatActivity {

    private EditText et_tourist_spot_name, et_tourist_spot_address, et_tourist_spot_description, et_tourist_spot_coordinates;
    private PowerSpinnerView sp_tourist_spot_state, sp_tourist_spot_category;
    private MaterialButton btn_upload_tourist_spot, btn_add_tourist_spot_photos, btn_add_tourist_spot_thumbnail, btn_add_tourist_spot_video;
    private ImageView iv_tourist_spot_thumbnail;
    private CircularProgressIndicator progress_bar;

    // For video purposes
    // This is where the video will actually play on the screen.
    // Think of it as the "TV" screen for the video.
    private StyledPlayerView player_view;

    // This will control the video, like play, pause, or stop
    private ExoPlayer exo_player;

    // This holds specific information about the video file chosen by the user,
    // so that the ExoPlayer (exo_player) knows what to play
    private MediaItem media_item;

    // Image(s) selected by the user
    private List<Uri> list_of_selected_tourist_spot_photos_uri;
    private Uri selected_tourist_spot_thumbnail_uri;
    private Uri selected_tourist_spot_video;

    // 100 is the request code for selecting multiple images
    // 101 is the request code for selecting one image
    // 102 is the request code for selecting one video
    private static final int PICK_PHOTOS = 100;
    private static final int PICK_THUMBNAIL = 101;
    private static final int PICK_VIDEO = 102;

    // Firebase
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storage_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin_add_tourist_spot);

        et_tourist_spot_name = findViewById(R.id.et_tourist_spot_name);
        et_tourist_spot_address = findViewById(R.id.et_tourist_spot_address);
        et_tourist_spot_description = findViewById(R.id.et_tourist_spot_description);
        et_tourist_spot_coordinates = findViewById(R.id.et_tourist_spot_coordinates);

        sp_tourist_spot_state = findViewById(R.id.sp_tourist_spot_state);
        sp_tourist_spot_category = findViewById(R.id.sp_tourist_spot_category);

        iv_tourist_spot_thumbnail = findViewById(R.id.iv_tourist_spot_thumbnail);

        btn_upload_tourist_spot = findViewById(R.id.btn_upload_tourist_spot);
        btn_add_tourist_spot_photos = findViewById(R.id.btn_add_tourist_spot_photos);
        btn_add_tourist_spot_thumbnail = findViewById(R.id.btn_add_tourist_spot_thumbnail);
        btn_add_tourist_spot_video = findViewById(R.id.btn_add_tourist_spot_video);

        player_view = findViewById(R.id.player_view);

        // Create an instance of ExoPlayer
        exo_player = new ExoPlayer.Builder(this).build();

        // Makes sure the video played by exo_player will actually be displayed in the player_view component
        player_view.setPlayer(exo_player);

        progress_bar = findViewById(R.id.progress_bar);

        list_of_selected_tourist_spot_photos_uri = new ArrayList<>();

        // Create all the spinner items. Each item will have a text and image (flag)
        List<IconSpinnerItem> iconSpinnerItems_state = new ArrayList<>();
        iconSpinnerItems_state.add(new IconSpinnerItem("Johor", getDrawable(R.drawable.flag_johor)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Kedah", getDrawable(R.drawable.flag_kedah)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Kelantan", getDrawable(R.drawable.flag_kelantan)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Melaka", getDrawable(R.drawable.flag_melaka)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Negeri Sembilan", getDrawable(R.drawable.flag_negeri_sembilan)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Perak", getDrawable(R.drawable.flag_perak)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Perlis", getDrawable(R.drawable.flag_perlis)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Pahang", getDrawable(R.drawable.flag_pahang)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Pulau Pinang", getDrawable(R.drawable.flag_pulau_pinang)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Sabah", getDrawable(R.drawable.flag_sabah)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Sarawak", getDrawable(R.drawable.flag_sarawak)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Selangor", getDrawable(R.drawable.flag_selangor)));
        iconSpinnerItems_state.add(new IconSpinnerItem("Terengganu", getDrawable(R.drawable.flag_terengganu)));

        // Put all those items into the spinner
        IconSpinnerAdapter iconSpinnerAdapter_from = new IconSpinnerAdapter(sp_tourist_spot_state);
        sp_tourist_spot_state.setSpinnerAdapter(iconSpinnerAdapter_from);
        sp_tourist_spot_state.setItems(iconSpinnerItems_state);
        sp_tourist_spot_state.selectItemByIndex(0);
        sp_tourist_spot_state.setLifecycleOwner(this);

        // Create all the spinner items for Category. Each item will have only text
        List<IconSpinnerItem> iconSpinnerItems_category = new ArrayList<>();
        iconSpinnerItems_category.add(new IconSpinnerItem("Historical"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Hotel"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Museum"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Park"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Restaurant"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Wildlife"));

        // Put all those items into the spinner
        IconSpinnerAdapter iconSpinnerAdapter_from_2 = new IconSpinnerAdapter(sp_tourist_spot_category);
        sp_tourist_spot_category.setSpinnerAdapter(iconSpinnerAdapter_from_2);
        sp_tourist_spot_category.setItems(iconSpinnerItems_category);
        sp_tourist_spot_category.selectItemByIndex(0);
        sp_tourist_spot_category.setLifecycleOwner(this);

        // Initialize Firebase Database and Storage
        database = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        // The user clicks the Select Thumbnail button
        btn_add_tourist_spot_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_THUMBNAIL);

            }
        });

        // The user clicks the Select Photos button
        btn_add_tourist_spot_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Intent to open gallery and pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Only images
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection

                // Start the activity to pick an image
                startActivityForResult(intent, PICK_PHOTOS);

            }
        });

        // The user clicks the Select Video button
        btn_add_tourist_spot_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to open gallery and pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);

                // Filter to show only video files
                intent.setType("video/*");

                // Start the activity to pick an image
                startActivityForResult(intent, PICK_VIDEO);
            }
        });

        // The user clicks the Upload button
        btn_upload_tourist_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.GONE);
                uploadToFirebase();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // onStart means what happens when the activity starts
    @Override
    protected void onStart() {
        super.onStart();

        // We want to ensure that the video player is ready whenever the activity comes into view

        // Reinitialize ExoPlayer if it has been released
        if (exo_player == null) {
            exo_player = new ExoPlayer.Builder(this).build();
            player_view.setPlayer(exo_player);
        }
    }

    // onStop means what happens when the activity closes
    @Override
    protected void onStop() {
        super.onStop();

        // We want to makes sure that the video stops playing and cleans up resources when the activity is no longer visible.

        // Prevent the video from playing when the app is closed
        if (exo_player != null) {
            exo_player.setPlayWhenReady(false);
            // Release the player when done
            exo_player.release();
            exo_player = null; // Set to null so it can be reinitialized in onStart
        }
    }

    private void uploadToFirebase() {

        // Extract text from EditTexts
        String name = et_tourist_spot_name.getText().toString().trim();
        String description = et_tourist_spot_description.getText().toString().trim();
        String state = sp_tourist_spot_state.getText().toString();
        String category = sp_tourist_spot_category.getText().toString();
        String address = et_tourist_spot_address.getText().toString().trim();
        String coordinates = et_tourist_spot_coordinates.getText().toString().trim();

        // Input validation
        if(name.isEmpty() || description.isEmpty() || address.isEmpty() || coordinates.isEmpty()){
            Snackbar.make(findViewById(R.id.main), "Please fill in all input fields.", Snackbar.LENGTH_LONG).show();
            return;
        }
        else if (selected_tourist_spot_thumbnail_uri == null) {
            Snackbar.make(findViewById(R.id.main), "Please provide a thumbnail.", Snackbar.LENGTH_LONG).show();
            return;
        }
        else if(list_of_selected_tourist_spot_photos_uri.isEmpty()){
            Snackbar.make(findViewById(R.id.main), "Please provide some photos.", Snackbar.LENGTH_LONG).show();
            return;
        }
        else if(selected_tourist_spot_video == null){
            Snackbar.make(findViewById(R.id.main), "Please provide a video.", Snackbar.LENGTH_LONG).show();
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);

        // Upload the Thumbnail first
        uploadThumbnail(name, description, state, category, address, coordinates);

    }

    private void uploadThumbnail(String name, String description, String state, String category, String address, String coordinates) {

        // Before storing the image to FirebaseStorage, get the image's extension
        // getFileExtension() will return strings like "jpg", "png", etc. Without the dot.
        String image_extension = getFileExtension(selected_tourist_spot_thumbnail_uri);

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
        StorageReference file_reference = storage_reference.child("TouristSpotGallery/" + System.currentTimeMillis() + image_extension);

        file_reference.putFile(selected_tourist_spot_thumbnail_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get the download URL for the uploaded image
                        file_reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri download_url) {

                                        // The thumbnail url is converted to string, and this string is added to the list
                                        String str_uri_thumbnail = download_url.toString();

                                        // You have stored the thumbnail image itself into Storage,
                                        // but you need to store its uri into the Realtime Database.
                                        // Before you do that, you need to upload photos first.
                                        Toast.makeText(Page_Admin_Add_TouristSpot_Activity.this, "Thumbnail successfully uploaded.", Toast.LENGTH_SHORT).show();
                                        uploadPhotos(name, description, state, category, address, coordinates, str_uri_thumbnail);

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.main), "Failed to upload thumbnail to Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    private void uploadPhotos(String name, String description, String state, String category, String address, String coordinates, String str_uri_thumbnail) {

        // This list stores the image URLs
        // Each image will have their own URL. The URL will tell us where the image is stored
        // Without URL, we will not have any way to retrieve the image.
        // Even though the image exist in the StorageDatabase, but without its URL, we won't be able to access that image.
        List<String> list_of_image_urls = new ArrayList<>();

        // Upload image each from the list to FirebaseStorage
        for(Uri curr_image_uri : list_of_selected_tourist_spot_photos_uri){

            // Before storing the image to FirebaseStorage, get the image's extension
            // getFileExtension() will return strings like "jpg", "png", etc. Without the dot.
            String image_extension = getFileExtension(curr_image_uri);

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
            StorageReference file_reference = storage_reference.child("TouristSpotGallery/" +  System.currentTimeMillis() + image_extension);

            // After knowing where to store them, we can start storing the image into that location
            file_reference.putFile(curr_image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Get the download URL for the uploaded image
                            file_reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri download_url) {

                                            // The image url is converted to string, and this string is added to the list
                                            list_of_image_urls.add(download_url.toString());

                                            // Check if all images are uploaded
                                            if (list_of_image_urls.size() == list_of_selected_tourist_spot_photos_uri.size()) {
                                                // All photos are uploaded to StorageDatabase.
                                                // Now store the data into the Realtime Database
                                                Toast.makeText(Page_Admin_Add_TouristSpot_Activity.this, "Photos successfully uploaded.", Toast.LENGTH_SHORT).show();
                                                uploadVideo(name, description, state, category, address, coordinates, str_uri_thumbnail, list_of_image_urls);
                                            }

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.main), "Failed to upload photos to Firebase Storage.", Snackbar.LENGTH_LONG).show();
                            progress_bar.setVisibility(View.GONE);
                        }
                    });

        }

    }

    private void uploadVideo(String name, String description, String state, String category, String address, String coordinates, String str_uri_thumbnail, List<String> list_of_image_urls) {

        StorageReference file_reference = storage_reference.child("TouristSpotVideo/" + System.currentTimeMillis());

        file_reference.putFile(selected_tourist_spot_video)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get the download URL for the uploaded video
                        file_reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri download_url) {

                                        // The video url is converted to string
                                        String str_uri_video = download_url.toString();

                                        // You have stored the video itself into Storage,
                                        // but you need to store its uri into the Realtime Database.
                                        Toast.makeText(Page_Admin_Add_TouristSpot_Activity.this, "Video successfully uploaded.", Toast.LENGTH_SHORT).show();
                                        TouristSpot tourist_spot = new TouristSpot(name, description, state, category, address, coordinates, new TouristSpot_Uri(str_uri_thumbnail, list_of_image_urls, str_uri_video));
                                        storeTextualTouristSpotData(tourist_spot);

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.main), "Failed to upload video to Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    private void storeTextualTouristSpotData(TouristSpot tourist_spot) {

        // Reference to "TouristSpot" in the database. TouristSpot is a collection name
        DatabaseReference database_reference = database.getReference("TouristSpot");

        // Generate a unique ID for the user
        String tourist_spot_id = database_reference.push().getKey();

        // Store the user data as a document in the TouristSpot collection
        database_reference.child(tourist_spot_id).setValue(tourist_spot)
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

                                        Toast.makeText(Page_Admin_Add_TouristSpot_Activity.this, "Data uploaded successfully!", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(Page_Admin_Add_TouristSpot_Activity.this, Page_Admin_Activity.class);
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
                        Snackbar.make(findViewById(R.id.main), "Failed to upload new tourist spot data to Firebase Realtime Database.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user was picking image(s) for the photos
        if (requestCode == PICK_PHOTOS && resultCode == RESULT_OK && data != null) {

            // Clear the list first
            list_of_selected_tourist_spot_photos_uri.clear();

            // If the user selected multiple images
            if (data.getClipData() != null) {

                // Number of selected images
                int count = data.getClipData().getItemCount();

                // Loop through all the images selected by the user.
                for (int i = 0; i < count; i++) {

                    // Get the image's Uri
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();

                    // Store this image URI into the list for further processing
                    list_of_selected_tourist_spot_photos_uri.add(imageUri);
                }
            }

            // If the user selected only one image
            else if (data.getData() != null) {
                Uri imageUri = data.getData();

                // Store this image URI into the list for further processing
                list_of_selected_tourist_spot_photos_uri.add(imageUri);
            }

            addImageToLayout();
            // iv_image.setImageURI(list_of_selected_images_uri.get(1));

        }

        // If the user was picking an image for the thumbnail only
        else if(requestCode == PICK_THUMBNAIL && resultCode == RESULT_OK && data != null){

            // If the user did picked an image
            if(data.getData() != null){
                selected_tourist_spot_thumbnail_uri = data.getData();
                iv_tourist_spot_thumbnail.setImageURI(selected_tourist_spot_thumbnail_uri);
            }

        }

        // If the user was picking a video
        else if(requestCode == PICK_VIDEO && resultCode == RESULT_OK && data != null){

            if (data.getData() != null) {
                selected_tourist_spot_video = data.getData();

                // Now you can use the selected video to play the video
                playVideo();
            }
            else {
                // Handle the error: the URI is null
                Toast.makeText(this, "Failed to select video from gallery.", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void addImageToLayout() {

        // First, remove all views inside the Linear Layout
        LinearLayout ll_photos_container = findViewById(R.id.ll_photos_container);
        ll_photos_container.removeAllViews();

        for(Uri curr_photo_uri : list_of_selected_tourist_spot_photos_uri){

            ImageView imageView = new ImageView(this);

            // Create layout params with MATCH_PARENT width and WRAP_CONTENT height
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Convert dp to px for margin-bottom
            int margin_bottom_dp = 24; // 24dp

            // Convert 16dp to px
            int margin_bottom_px = (int) (margin_bottom_dp * getResources().getDisplayMetrics().density);

            // Set only the bottom margin (left, top, right margins set to 0)
            layoutParams.setMargins(0, 0, 0, margin_bottom_px);

            imageView.setLayoutParams(layoutParams);
            imageView.setAdjustViewBounds(true);
            imageView.setImageURI(curr_photo_uri);
            ll_photos_container.addView(imageView);

        }

    }

    private void playVideo() {

        // Checks if the exo_player has been created yet.
        // If it is null, it means it is not created, so we need to create it.
        if (exo_player == null) {
            // Reinitialize ExoPlayer if it's null
            exo_player = new ExoPlayer.Builder(this).build();
            player_view.setPlayer(exo_player);
        }

        // Create a media item using the uri of the selected video.
        // This object (media_item) contains all necessary info about the video that will be played.
        media_item = MediaItem.fromUri(selected_tourist_spot_video);

        // Tells exo_player what video to play
        exo_player.setMediaItem(media_item);

        // Loads the video into memory and gets everything ready to play
        exo_player.prepare();

        // Play the video
        exo_player.setPlayWhenReady(true);

    }

}

// Note:
/*
 *
 *  What is onActivityResult()
 *
 *  It is a method that handles the result of an activity that you started using the startActivityResult() method.
 *
 *  When you start another activity (such as opening the gallery to pick an image),
 *  your app pauses while that new activity takes over.
 *
 *  Once the user is done with that task (examples: selecting an image or canceling),
 *  the system calls the onActivityResult() in your app to let it know what happened,
 *  and it provides any necessary data (like the Uri of the selected image).
 *
 *
 *  Let me break down the code:
 *
 *  1) Intent intent = new Intent(Intent.ACTION_PICK);
 *     intent.setType("image/*");
 *     intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
 *
 *     Intent.ACTION_PICK
 *     - This opens the device's gallery to select an image
 *
 *     intent.setType("image/*")
 *     - Limits the selection to images only
 *
 *     intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
 *     - Allows the user to select multiple images.
 *       If you don't write this line of code, then the app only allows to user to select one image.
 *
 *  2) startActivityForResult(intent, PICK_IMAGE);
 *
 *     Start the activity of opening the phone's gallery.
 *
 *     Btw, what is PICK_IMAGE?
 *     - We initialized this constant variable at the very top of the code, to 100.
 *
 *     Why did we create this variable?
 *     - We need a way to know what specific special task the user are performing when the system returns the results.
 *       The special tasks could be picking an image from the gallery, picking a file, taking a photo from the camera, etc.
 *
 *       Let's say the user opened the activity about picking an image from the gallery.
 *       The user could either select the image and then close the activity. Or not select any image and then close the activity.
 *       Regardless of whether they selected an image or not, when the user close the activity, it returns some data.
 *       That data can be found in onActivityResult() method.
 *
 *       If the user opened the activity about picking a file,
 *       Regardless whether the user has selected a file or not, when the user close that activity, it returns some data.
 *       That data can be found in onActivityResult() method.
 *
 *       If the user opened the activity about taking a photo from the camera,
 *       Regardless whether the user has taken a photo or not, when the user close that activity, it returns some data.
 *       That data can be found in onActivityResult() method.
 *
 *       There are other special activities out there, besides the three I just mentioned.
 *
 *       Do you see that they all run the onActivityResult() method when they are finished with their tasks?
 *       They don't have their own methods. Instead, they use the same method, which is onActivityResult().
 *       In this method, we need to know what activity was just finished.
 *       To do that, we use the code.
 *
 *       startActivityForResult(intent, PICK_IMAGE);
 *       - intent is for opening the gallery activity.
 *         PICK_IMAGE is just a code (in this case, we set it to 100)
 *         When the user has finished using the gallery activity,
 *         this activity will return some data and will immediately run the onActivityResult().
 *         The returned data can be found in this method, and one of data is the value of PICK_IMAGE (100), which is stored as requestCode variable.
 *
 *
 *       In short,
 *       startActivityForResult() is a method to start an activity when you expect a result back (like selecting an image)
 *       One of the things you pass into this method, is a unique code.
 *       If you want to open the phone's gallery, then you need to pass in 100.
 *
 *      Furthermore, why the value 100?
 *      - The number 100 is arbitrary. You could choose any unique integer, but 100 is often used as a common example in tutorials.
 *
 * 3) onActivityResult()
 *    This method is called when the result is returned, and it contains:
 *    - requestCode
 *      It is a code you used to start the activity (example: PICK_IMAGE),
 *      to identify what request this result belongs to.
 *
 *      If the requestCode is 100, then it means the phone's gallery activity was opened and then closed.
 *
 *      Btw, startActivityResult() is not just for opening images. It can open files, take a photo from camera, etc.
 *      Each of them has their own unique code, and whenever those activities are opened and then closed,
 *      all of them will run the onActivityResult()
 *
 *      That is why we need to check what activity was just closed. Was it the gallery activity? Or the file activity? etc.
 *      To do that, we check the requestCode variable.
 *      In simple terms, we use requestCode to check what special activity was just closed by the user.
 *
 *
 * 4) What is Uri?
 *    In Android Studio, a Uri (Uniform Resource Identifier) is like an address that tells Android where to find something,
 *    such as a file, image, or webpage. Think of it as a URL, but instead of just for websites,
 *    it can point to many different kinds of resources on your device or the internet.
 *
 *    For example, a Uri might tell your app where to find a specific photo stored on your phone,
 *    or it can help you open a web link in a browser.
 *
 *    In short, Uri is a way for Android to locate and access resources.
 *
 * */