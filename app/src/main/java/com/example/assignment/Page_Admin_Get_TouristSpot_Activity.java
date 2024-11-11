package com.example.assignment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page_Admin_Get_TouristSpot_Activity extends AppCompatActivity {

    DatabaseReference databaseReference;

    private TextInputEditText et_touristSpot_name;
    private MaterialButton btn_search_tourist_spot, btn_update_text_data, btn_update_thumbnail, btn_update_photos, btn_update_video, btn_delete_tourist_spot;
    private CircularProgressIndicator progress_bar_search, progress_bar_delete;

    private LinearLayout ll_search_result_container, ll_photos_container;
    private TextView tv_touristSpot_name, tv_touristSpot_description, tv_touristSpot_address, tv_tourist_spot_coordinates, tv_touristSpot_state, tv_touristSpot_category;
    private ImageView iv_touristSpot_thumbnail, iv_touristSpot_state;

    // This is where the video will actually play on the screen.
    // Think of it as the "TV" screen for the video.
    private StyledPlayerView player_view;

    // This will control the video, like play, pause, or stop
    private ExoPlayer exo_player;

    // This holds specific information about the video file chosen by the user,
    // so that the ExoPlayer (exo_player) knows what to play
    private MediaItem media_item;

    // Custom Dialog for Confirm Delete
    private Dialog dialog;
    private TextView tv_btn_delete, tv_btn_cancel;

    TouristSpot tourist_spot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin_get_tourist_spot);

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");

        et_touristSpot_name = findViewById(R.id.et_tourist_spot_name);

        btn_search_tourist_spot = findViewById(R.id.btn_search_tourist_spot);
        btn_update_text_data = findViewById(R.id.btn_update_text_data);
        btn_update_thumbnail = findViewById(R.id.btn_update_thumbnail);
        btn_update_photos = findViewById(R.id.btn_update_photos);
        btn_update_video = findViewById(R.id.btn_update_video);
        btn_delete_tourist_spot = findViewById(R.id.btn_delete_tourist_spot);

        progress_bar_search = findViewById(R.id.progress_bar_search);
        progress_bar_delete = findViewById(R.id.progress_bar_delete);

        ll_search_result_container = findViewById(R.id.ll_search_result_container);
        ll_photos_container = findViewById(R.id.ll_photos_container);
        tv_touristSpot_name = findViewById(R.id.tv_touristSpot_name);
        tv_touristSpot_description = findViewById(R.id.tv_touristSpot_description);
        tv_touristSpot_address =  findViewById(R.id.tv_touristSpot_address);
        tv_tourist_spot_coordinates = findViewById(R.id.tv_touristSpot_coordinates);
        tv_touristSpot_state = findViewById(R.id.tv_touristSpot_state);
        tv_touristSpot_category = findViewById(R.id.tv_touristSpot_category);
        iv_touristSpot_thumbnail = findViewById(R.id.iv_touristSpot_thumbnail);
        iv_touristSpot_state = findViewById(R.id.iv_touristSpot_state);

        tourist_spot = new TouristSpot();

        player_view = findViewById(R.id.player_view);

        // Create an instance of ExoPlayer
        exo_player = new ExoPlayer.Builder(this).build();

        // Makes sure the video played by exo_player will actually be displayed in the player_view component
        player_view.setPlayer(exo_player);

        // When the user click the Search button
        btn_search_tourist_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = et_touristSpot_name.getText().toString();

                if(name.isEmpty()){
                    Snackbar.make(findViewById(R.id.main), "Please provide the name of the tourist spot", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Create a query to search for a specific name
                Query query = databaseReference.orderByChild("name").equalTo(name);

                // Remove the search result container
                ll_search_result_container.setVisibility(View.GONE);

                // Set up the progress bar accordingly
                progress_bar_search.setVisibility(View.VISIBLE);

                // Attach a listener to the query
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // If the tourist spot with name is found in the database
                        if(snapshot.exists()){

                            // Get the first child (since there's only one expected result)
                            DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();

                            // Get the data as a TouristSpot object
                            // TouristSpot touristSpot = dataSnapshot.getValue(TouristSpot.class);

                            tourist_spot = dataSnapshot.getValue(TouristSpot.class);
                            // tourist_spot.setName(dataSnapshot.getValue(TouristSpot.class).getName());
                            // tourist_spot.setDescription(dataSnapshot.getValue(TouristSpot.class).getDescription());
                            // tourist_spot.setAddress(dataSnapshot.getValue(TouristSpot.class).getAddress());
                            // tourist_spot.setState(dataSnapshot.getValue(TouristSpot.class).getState());
                            // tourist_spot.setCategory(dataSnapshot.getValue(TouristSpot.class).getCategory());
                            // tourist_spot.setList_of_images_uri(dataSnapshot.getValue(TouristSpot.class).getList_of_images_uri());
                            // tourist_spot.setThumbnail_uri(dataSnapshot.getValue(TouristSpot.class).getThumbnail_uri());

                            // Display the data
                            displayData(tourist_spot);
                        }

                        // If the database cannot find tourist spot with that name
                        else{
                            Snackbar.make(findViewById(R.id.main), "No such Tourist Spot is found in the Firebase Database", Snackbar.LENGTH_LONG).show();
                            progress_bar_search.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(findViewById(R.id.main), "Error retrieving data from the Firebase Database. " + error, Snackbar.LENGTH_LONG).show();
                        progress_bar_search.setVisibility(View.GONE);
                    }
                });

            }
        });

        // When the user click Update Text Data button
        btn_update_text_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 Intent intent = new Intent(Page_Admin_Get_TouristSpot_Activity.this, Page_Admin_Update_Text_Data_Activity.class);
                 intent.putExtra("name", tourist_spot.getName());
                 intent.putExtra("description", tourist_spot.getDescription());
                 intent.putExtra("state", tourist_spot.getState());
                 intent.putExtra("category", tourist_spot.getCategory());
                 intent.putExtra("address", tourist_spot.getAddress());
                 intent.putExtra("coordinates", tourist_spot.getCoordinates());
                 startActivity(intent);

            }
        });

        // When the user click Update Thumbnail button
        btn_update_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the thumbnail to another activity
                Intent intent = new Intent(Page_Admin_Get_TouristSpot_Activity.this, Page_Admin_Update_Thumbnail_Activity.class);
                intent.putExtra("name", tourist_spot.getName());
                intent.putExtra("state", tourist_spot.getState());
                intent.putExtra("thumbnail", tourist_spot.tourist_spot_uri.getThumbnail_uri());
                startActivity(intent);
            }
        });

        // When the user click Update Photo button
        btn_update_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Convert the List<String> to an ArrayList<String>, because Intent does not support passing List to another activity. It support ArrayList instead.
                ArrayList<String> arr_list_of_images_uri = new ArrayList<>(tourist_spot.tourist_spot_uri.getList_of_images_uri());

                // Pass the photos to another activity
                Intent intent = new Intent(Page_Admin_Get_TouristSpot_Activity.this, Page_Admin_Update_Photos_Activity.class);
                intent.putExtra("name", tourist_spot.getName());
                intent.putExtra("state", tourist_spot.getState());
                intent.putStringArrayListExtra("photos", arr_list_of_images_uri);
                startActivity(intent);
            }
        });

        // When the user click Update Video button
        btn_update_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the video to another activity
                Intent intent = new Intent(Page_Admin_Get_TouristSpot_Activity.this, Page_Admin_Update_Video_Activity.class);
                intent.putExtra("name", tourist_spot.getName());
                intent.putExtra("video", tourist_spot.tourist_spot_uri.getVideo_uri());
                startActivity(intent);

            }
        });

        // When the user click Delete Tourist Spot button
        btn_delete_tourist_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress_bar_delete.setVisibility(View.GONE);
                disableButtons();
                showConfirmDeleteDialog();

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

    private void playVideo(Uri uri_selected_video) {

        // Checks if the exo_player has been created yet.
        // If it is null, it means it is not created, so we need to create it.
        if (exo_player == null) {
            // Reinitialize ExoPlayer if it's null
            exo_player = new ExoPlayer.Builder(this).build();
            player_view.setPlayer(exo_player);
        }

        // Create a media item using the uri of the selected video.
        // This object (media_item) contains all necessary info about the video that will be played.
        media_item = MediaItem.fromUri(uri_selected_video);

        // Tells exo_player what video to play
        exo_player.setMediaItem(media_item);

        // Loads the video into memory and gets everything ready to play
        exo_player.prepare();

        // Play the video
        exo_player.setPlayWhenReady(true);
    }

    private void deleteImageFromStorage(String image_url){

        // Create a StorageReference from the image URL
        StorageReference storage_reference = FirebaseStorage.getInstance().getReferenceFromUrl(image_url);

        // Delete the image
        storage_reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Image deleted successfully.");
                }
                else {
                    Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "There was an error deleting an image at Storage", Toast.LENGTH_SHORT).show();
                    enableButtons();
                }
            }
        });

    }

    private void deleteTouristSpotByName(String name){

        progress_bar_delete.setVisibility(View.VISIBLE);

        // Attach a listener to fetch and delete the specific entry
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Loop through every single Tourist Spot in Realtime Database,
                // until it encounters one with key "name" equal to the name inputted by the user.
                for(DataSnapshot curr_data_snapshot: snapshot.getChildren()){

                    // If it founds the Tourist Spot with the name inputted by the user, in the Realtime Database.
                    // If it found it, then we can delete it.
                    if(curr_data_snapshot.child("name").getValue(String.class).equals(name)){

                        // ---- Step 1: Delete all photos of Tourist Spot in Firebase Storage (just photos, not thumbnail)
                        // To do that, we need to use the list_of_images_uri key, because it shows us where those photos are located in Storage
                        // list_of_images_uri key is a list of urls. This key is a list
                        DataSnapshot images_snapshot = curr_data_snapshot.child("tourist_spot_uri/list_of_images_uri");

                        // Since this key is a list, we need to loop through the list
                        for (DataSnapshot image_snapshot : images_snapshot.getChildren()) {

                            // This list is a list of string
                            // So we need to tell we are extracting a string value (hence, String.class).
                            String image_url = image_snapshot.getValue(String.class);

                            // We now have an element of that list,
                            // in other words, we have a value that tells us the location of a photo in the Storage
                            // We use this element to point us to the image in the Storage, and then delete that image from there.
                            deleteImageFromStorage(image_url);
                        }

                        // ---- Step 2: After deleting all photos, we proceed to delete the thumbnail in Firebase Storage
                        // The thumbnail is in the thumbnail_url key
                        // Get the value of thumbnail_uri key, which is a String data type
                        String thumbnail_uri = curr_data_snapshot.child("tourist_spot_uri/thumbnail_uri").getValue(String.class);

                        // Delete this thumbnail from the Storage
                        deleteImageFromStorage(thumbnail_uri);

                        // ---- Step 3: After deleting all photos and thumbnail, proceed to delete the video in Firebase Storage
                        // The video is in the video_uri key
                        // Get the value of video_uri key, which is a String data type
                        String original_str_uri_video_db = curr_data_snapshot.child("tourist_spot_uri/video_uri").getValue(String.class);

                        if(original_str_uri_video_db == null || original_str_uri_video_db.isEmpty()){
                            Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "No video to delete.", Toast.LENGTH_SHORT).show();
                            // progress_bar.setVisibility(View.GONE);
                            return;
                        }

                        StorageReference video_reference = FirebaseStorage.getInstance().getReferenceFromUrl(original_str_uri_video_db);

                        // Delete the video from Firebase Storage
                        video_reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // ---- Step 4: After deleting all photos, thumbnail and video, proceed the entry in Realtime Database
                                if (task.isSuccessful()) {
                                    // Proceed to delete entry from Realtime Database

                                    //------ After deleting photos, thumbnail and video, delete the entry from Realtime Database
                                    curr_data_snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "Data deleted successfully", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "Failed to delete data", Toast.LENGTH_LONG).show();
                                            }
                                            progress_bar_delete.setVisibility(View.GONE);
                                            enableButtons();

                                            // Go back to Admin Dashboard
                                            Intent intent = new Intent(Page_Admin_Get_TouristSpot_Activity.this, Page_Admin_Activity.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });


                                }
                                else {
                                    Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "Failed to delete the video.", Toast.LENGTH_SHORT).show();
                                    // progress_bar.setVisibility(View.GONE);
                                }
                            }
                        });




                        // Exit the function after deleting, because we are only deleting one entry
                        return;

                    }

                }

                Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "Could not find the data you are looking for.", Toast.LENGTH_SHORT).show();
                progress_bar_delete.setVisibility(View.GONE);
                enableButtons();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Page_Admin_Get_TouristSpot_Activity.this, "Error finding the data you are looking for.", Toast.LENGTH_SHORT).show();
                progress_bar_delete.setVisibility(View.GONE);
                enableButtons();
            }
        });

    }

    private void showConfirmDeleteDialog() {

        // Create the dialog
        dialog = new Dialog(Page_Admin_Get_TouristSpot_Activity.this);
        dialog.setContentView(R.layout.custom_dialog_box_delete_tourist_spot);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // The user is able to close the dialog box by clicking anywhere outside the dialog box
        dialog.setCancelable(true);

        // Use dialog.findViewById() to get views from the dialog layout
        tv_btn_delete = dialog.findViewById(R.id.tv_btn_delete);
        tv_btn_cancel = dialog.findViewById(R.id.tv_btn_cancel);

        // Delete the Tourist Spot when the Delete button is clicked
        tv_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteTouristSpotByName(tv_touristSpot_name.getText().toString());
            }
        });

        // Close the dialog when the Cancel button is clicked
        tv_btn_cancel.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();

    }

    private void displayData(TouristSpot tourist_spot) {

        // Steps to turn the progress bar from spinning indefinite to definite, after getting the data successfully
        // Step 1: Switch to determinate mode
        progress_bar_search.setIndeterminate(false);

        // Step 2: Start at 0% progress
        progress_bar_search.setProgressCompat(0, false);

        // Step 3: Animate progress to 100% over a period (e.g., 2 seconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Step 4: Animate to 100% (in 2000 milliseconds)
                progress_bar_search.setProgressCompat(100, true);

                // Step 5: Continue with the rest of the code after animation
                progress_bar_search.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This runs after the progress reaches 100%
                        progress_bar_search.setProgressCompat(0, false);
                        progress_bar_search.setVisibility(View.GONE);
                        progress_bar_search.setIndeterminate(true);

                        // Display the data

                        //Picasso makes loading the images from url easily
                        Picasso
                                .get()
                                .load(tourist_spot.tourist_spot_uri.getThumbnail_uri())
                                .placeholder(R.drawable.image_placeholder)
                                .fit()
                                .centerCrop()
                                .into(iv_touristSpot_thumbnail);

                        ll_search_result_container.setVisibility(View.VISIBLE);
                        tv_touristSpot_name.setText(tourist_spot.getName());
                        tv_touristSpot_description.setText(tourist_spot.getDescription());
                        tv_touristSpot_address.setText(tourist_spot.getAddress());
                        tv_tourist_spot_coordinates.setText(tourist_spot.getCoordinates());
                        tv_touristSpot_state.setText(tourist_spot.getState());
                        tv_touristSpot_category.setText(String.valueOf(tourist_spot.getCategory()));

                        switch (tourist_spot.getState()){
                            case "Johor":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_johor);
                                break;
                            case "Kedah":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_kedah);
                                break;
                            case "Kelantan":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_kelantan);
                                break;
                            case "Melaka":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_melaka);
                                break;
                            case "Negeri Sembilan":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_negeri_sembilan);
                                break;
                            case "Perak":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_perak);
                                break;
                            case "Perlis":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_perlis);
                                break;
                            case "Pahang":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_pahang);
                                break;
                            case "Pulau Pinang":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_pulau_pinang);
                                break;
                            case "Sabah":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_sabah);
                                break;
                            case "Sarawak":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_sarawak);
                                break;
                            case "Selangor":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_selangor);
                                break;
                            case "Terengganu":
                                iv_touristSpot_state.setImageResource(R.drawable.flag_terengganu);
                                break;
                        }

                        addImageToLayout(tourist_spot.tourist_spot_uri.getList_of_images_uri());

                        playVideo(Uri.parse(tourist_spot.tourist_spot_uri.getVideo_uri()));

                    }
                }, 2000); // The duration of the animation (2 seconds in this case)
            }
        }, 0);

    }

    private void addImageToLayout(List<String> list_of_images_uri) {

        // First, remove all views inside the Linear Layout
        ll_photos_container.removeAllViews();

        // Loop through the list
        for(int i = 0; i < list_of_images_uri.size(); i++){

            // Convert the string into Uri
            Uri image_uri = Uri.parse(list_of_images_uri.get(i));

            // Programmatically create an imageView
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

            //Picasso makes loading the images from url easily
            Picasso
                    .get()
                    .load(list_of_images_uri.get(i))
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(imageView);

            ll_photos_container.addView(imageView);

        }

    }

    private void disableButtons(){
        btn_search_tourist_spot.setEnabled(false);
        btn_update_text_data.setEnabled(false);
        btn_update_thumbnail.setEnabled(false);
        btn_update_photos.setEnabled(false);
    }

    private void enableButtons(){
        btn_search_tourist_spot.setEnabled(true);
        btn_update_text_data.setEnabled(true);
        btn_update_thumbnail.setEnabled(true);
        btn_update_photos.setEnabled(true);
    }

}