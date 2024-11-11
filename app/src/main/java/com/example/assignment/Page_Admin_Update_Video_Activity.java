package com.example.assignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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

import java.util.HashMap;
import java.util.Map;

public class Page_Admin_Update_Video_Activity extends AppCompatActivity {

    private MaterialButton btn_change_video, btn_update_tourist_spot_video_database;
    private CircularProgressIndicator progress_bar;

    private String original_str_uri_video, new_str_uri_video, name;

    // This is where the video will actually play on the screen.
    // Think of it as the "TV" screen for the video.
    private StyledPlayerView player_view;

    // This will control the video, like play, pause, or stop
    private ExoPlayer exo_player;

    // This holds specific information about the video file chosen by the user,
    // so that the ExoPlayer (exo_player) knows what to play
    private MediaItem media_item;

    private static final int PICK_VIDEO = 102;

    private DatabaseReference database_reference;
    private FirebaseStorage storage;
    private StorageReference storage_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin_update_video);

        // Get data from previous activity
        Intent intent = getIntent();
        original_str_uri_video = intent.getStringExtra("video");
        name = intent.getStringExtra("name");

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        btn_change_video = findViewById(R.id.btn_change_video);
        btn_update_tourist_spot_video_database = findViewById(R.id.btn_update_tourist_spot_video_database);
        progress_bar = findViewById(R.id.progress_bar);

        new_str_uri_video = "";

        player_view = findViewById(R.id.player_view);

        // Create an instance of ExoPlayer
        exo_player = new ExoPlayer.Builder(this).build();

        // Makes sure the video played by exo_player will actually be displayed in the player_view component
        player_view.setPlayer(exo_player);

        // Display the data given from previous activity
        setData();

        // When the user click the Change video button
        btn_change_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*"); // Filter to show only video files
                startActivityForResult(intent, PICK_VIDEO);

            }
        });

        // When the user click the Save video to the database button
        btn_update_tourist_spot_video_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validation
                // Check if the user has selected new images
                if(new_str_uri_video.isEmpty()){
                    Snackbar.make(findViewById(R.id.main), "You cannot update using the same video.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    queryVideo();
                }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK && data != null) {

            if (data.getData() != null) {

                new_str_uri_video = String.valueOf(data.getData());

                // Now you can use the selected video to play the video
                playVideo(data.getData());
            }
            else {
                // Handle the error: the URI is null
                Toast.makeText(this, "Error when selecting video from gallery.", Toast.LENGTH_SHORT).show();
            }

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

    private void setData() {
        playVideo(Uri.parse(original_str_uri_video));
    }

    // Step 1: Query the database by name
    private void queryVideo(){

        progress_bar.setVisibility(View.VISIBLE);

        Query query = database_reference.orderByChild("name").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    DataSnapshot matching_result = snapshot.getChildren().iterator().next();

                    // Now you have the reference to the matching entry, move to step 2
                    deleteOldVideoFromStorage(matching_result);
                }
                else{
                    Snackbar.make(findViewById(R.id.main), "Could not find the data you are looking for.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(R.id.main), "Query failed: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                progress_bar.setVisibility(View.GONE);
            }
        });

    }

    // Step 2: Delete the old video from storage
    private void deleteOldVideoFromStorage(DataSnapshot matching_result) {

        String original_str_uri_video_db = matching_result.child("tourist_spot_uri").getValue(TouristSpot_Uri.class).getVideo_uri();

        if(original_str_uri_video_db == null || original_str_uri_video_db.isEmpty()){
            Snackbar.make(findViewById(R.id.main), "No video to delete in Firebase Storage.", Snackbar.LENGTH_LONG).show();
            progress_bar.setVisibility(View.GONE);
            return;
        }

        StorageReference image_reference = FirebaseStorage.getInstance().getReferenceFromUrl(original_str_uri_video_db);

        image_reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Proceed to upload the new thumbnail
                    updateNewVideoToStorage(matching_result);
                }
                else {
                    Snackbar.make(findViewById(R.id.main), "Failed to delete the old video in Firebase Storage.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });

    }

    // Step 3: Upload the new video to Firebase Storage
    private void updateNewVideoToStorage(DataSnapshot matching_result){

        StorageReference file_reference = storage_reference.child("TouristSpotVideo/" + System.currentTimeMillis());

        file_reference.putFile(Uri.parse(new_str_uri_video))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri download_url) {
                                // Update the thumbnail URI in the database
                                updateNewVideoToRealtimeDatabase(matching_result.getRef(), download_url.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.main), "Failed to upload the new video to Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    // Step 4: Update the video Uri in Firebase Realtime Database
    private void updateNewVideoToRealtimeDatabase(DatabaseReference reference, String new_str_uri_video){

        Map<String, Object> updates = new HashMap<>();
        updates.put("tourist_spot_uri/video_uri", new_str_uri_video);

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

                                    Toast.makeText(Page_Admin_Update_Video_Activity.this, "Video updated successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Page_Admin_Update_Video_Activity.this, Page_Admin_Activity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }, 2000); // The duration of the animation (2 seconds in this case)
                        }
                    }, 0);

                }
                else {
                    Snackbar.make(findViewById(R.id.main), "Failed to update new video in Firebase Realtime Database: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });

    }
}