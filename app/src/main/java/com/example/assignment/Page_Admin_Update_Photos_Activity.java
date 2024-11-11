package com.example.assignment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Page_Admin_Update_Photos_Activity extends AppCompatActivity {

    private MaterialButton btn_change_tourist_spot_photos, btn_update_tourist_spot_photos;
    private LinearLayout ll_photos_container;
    private CircularProgressIndicator progress_bar;

    private static final int PICK_PHOTOS = 100;

    // Image(s) selected by the user
    private List<String> original_list_of_str_uri_photos, new_list_of_str_uri_photos;
    private String name, state;

    private DatabaseReference database_reference;
    private FirebaseStorage storage;
    private StorageReference storage_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin_update_photos);

        btn_change_tourist_spot_photos = findViewById(R.id.btn_change_tourist_spot_photos);
        btn_update_tourist_spot_photos = findViewById(R.id.btn_update_tourist_spot_photos);
        ll_photos_container = findViewById(R.id.ll_photos_container);
        progress_bar = findViewById(R.id.progress_bar);

        original_list_of_str_uri_photos = new ArrayList<>();
        new_list_of_str_uri_photos = new ArrayList<>();

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        // When the user click Select new photos button
        btn_change_tourist_spot_photos.setOnClickListener(new View.OnClickListener() {
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

        // When the user click Save photos to database button
        btn_update_tourist_spot_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validation
                // Check if the user has selected new images
                if(new_list_of_str_uri_photos.isEmpty()){
                    Snackbar.make(findViewById(R.id.main), "You cannot update using the same photos.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    queryPhotos();
                }

            }
        });

        Intent intent = getIntent();
        original_list_of_str_uri_photos = intent.getStringArrayListExtra("photos");
        name = intent.getStringExtra("name");
        state = intent.getStringExtra("state");

        addImageToLayout(original_list_of_str_uri_photos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user was picking image(s) for the photos
        if (requestCode == PICK_PHOTOS && resultCode == RESULT_OK && data != null) {

            // Clear the list first
            new_list_of_str_uri_photos.clear();

            // If the user selected multiple images
            if (data.getClipData() != null) {

                // Number of selected images
                int count = data.getClipData().getItemCount();

                // Loop through all the images selected by the user.
                for (int i = 0; i < count; i++) {

                    // Get the image's Uri
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();

                    // Convert the Uri to string, and store it in the list, for further processing
                    new_list_of_str_uri_photos.add(imageUri.toString());
                }
            }

            // If the user selected only one image
            else if (data.getData() != null) {
                Uri imageUri = data.getData();

                // Convert the Uri to string, and store it in the list, for further processing
                new_list_of_str_uri_photos.add(imageUri.toString());
            }

            addImageToLayout(new_list_of_str_uri_photos);
            // iv_image.setImageURI(list_of_selected_images_uri.get(1));

        }

    }

    private void addImageToLayout(List<String> list_of_str_uri_photos) {

        // First, remove all views inside the Linear Layout
        ll_photos_container.removeAllViews();

        // Loop through the list
        for(int i = 0; i < list_of_str_uri_photos.size(); i++){

            // Convert the string into Uri
            Uri image_uri = Uri.parse(list_of_str_uri_photos.get(i));

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
                    .load(list_of_str_uri_photos.get(i))
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(imageView);

            ll_photos_container.addView(imageView);

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
    private void queryPhotos(){

        progress_bar.setVisibility(View.VISIBLE);

        Query query = database_reference.orderByChild("name").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    DataSnapshot matching_result = snapshot.getChildren().iterator().next();

                    // Now you have the reference to the matching entry, move to step 2
                    deleteOldPhotos(matching_result);
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

    // Step 2: Delete old photos from storage
    private void deleteOldPhotos(DataSnapshot matching_result) {

        // Clear any previous data
        original_list_of_str_uri_photos.clear();

        // Add image URIs to the list
        for (DataSnapshot curr_uri : matching_result.child("tourist_spot_uri/list_of_images_uri").getChildren()) {
            String old_image_str_uri = curr_uri.getValue(String.class);
            original_list_of_str_uri_photos.add(old_image_str_uri);
        }

        if (original_list_of_str_uri_photos.isEmpty()) {
            Snackbar.make(findViewById(R.id.main), "No photos to delete in Firebase Storage.", Snackbar.LENGTH_LONG).show();
            progress_bar.setVisibility(View.GONE);
            return;
        }

        // Counter for tracking deletions
        final int totalImages = original_list_of_str_uri_photos.size();
        final AtomicInteger successCount = new AtomicInteger(0);

        for (String curr_str_uri_photo : original_list_of_str_uri_photos) {

            if (curr_str_uri_photo != null) {

                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(curr_str_uri_photo);

                imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            successCount.incrementAndGet();
                        }

                        // Check if all deletions are completed
                        int completedDeletions = successCount.get();
                        if (completedDeletions == totalImages) {
                            progress_bar.setVisibility(View.GONE);

                            Toast.makeText(Page_Admin_Update_Photos_Activity.this, "All images deleted successfully in Firebase Storage.", Toast.LENGTH_LONG).show();

                            // Now you have deleted old photos, move to step 3
                            updateNewPhotosToStorage(matching_result);
                        }
                        else if (!task.isSuccessful()) {
                            // If any deletion failed, handle the failure here
                            progress_bar.setVisibility(View.GONE);
                            Snackbar.make(findViewById(R.id.main), "Some photos failed to delete in Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    // Step 3: Upload new photos to Firebase Storage
    private void updateNewPhotosToStorage(DataSnapshot matching_result){

        List<String> list_of_db_str_uri_photos = new ArrayList<>();

        // Upload each image from the list to FirebaseStorage
        for(String curr_image_str_uri_photo : new_list_of_str_uri_photos){

            // Before storing the image to FirebaseStorage, get the image's extension
            // getFileExtension() will return strings like "jpg", "png", etc. Without the dot.
            String image_extension = getFileExtension(curr_image_str_uri_photo);

            if(image_extension.isEmpty()){
                image_extension = "." + "jpg";
            }
            else{
                image_extension = "." + image_extension;
            }

            StorageReference file_reference = storage_reference.child("TouristSpotGallery/" + System.currentTimeMillis() + image_extension);

            // After knowing where to store them, we can start storing the image into that location
            file_reference.putFile(Uri.parse(curr_image_str_uri_photo))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Get the download URL for the uploaded image
                            file_reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri download_url) {

                                            list_of_db_str_uri_photos.add(download_url.toString());

                                            // Check if all images are uploaded
                                            if (list_of_db_str_uri_photos.size() == new_list_of_str_uri_photos.size()) {
                                                Toast.makeText(Page_Admin_Update_Photos_Activity.this, "Photos successfully uploaded.", Toast.LENGTH_SHORT).show();

                                                // Once all images are uploaded to Firebase Storage, move to Step 4
                                                updateNewPhotosToRealtimeDatabase(matching_result.getRef(), list_of_db_str_uri_photos);

                                            }

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.main), "Failed to upload new photos to Firebase Storage. " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            progress_bar.setVisibility(View.GONE);
                        }
                    });

        }

    }

    // Step 4: Update the photos Uris in Firebase Realtime Database
    private void updateNewPhotosToRealtimeDatabase(DatabaseReference reference, List<String> list_of_db_str_uri_photos){

        Map<String, Object> updates = new HashMap<>();
        updates.put("tourist_spot_uri/list_of_images_uri", list_of_db_str_uri_photos);

        reference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

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

                                    Toast.makeText(Page_Admin_Update_Photos_Activity.this, "All photos updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Page_Admin_Update_Photos_Activity.this, Page_Admin_Activity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }, 2000); // The duration of the animation (2 seconds in this case)
                        }
                    }, 0);

                }
                else {
                    Snackbar.make(findViewById(R.id.main), "Failed to update images: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }

            }
        });

    }

}