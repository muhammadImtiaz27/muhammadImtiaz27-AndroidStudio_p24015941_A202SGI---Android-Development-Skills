package com.example.assignment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Page_Admin_Update_Thumbnail_Activity extends AppCompatActivity {

    private ImageView iv_tourist_spot_thumbnail;
    private MaterialButton btn_change_tourist_spot_thumbnail, btn_update_tourist_spot_thumbnail_database;
    private CircularProgressIndicator progress_bar;

    private String original_str_uri_thumbnail, new_str_uri_thumbnail, name, state;

    private static final int PICK_THUMBNAIL = 101;

    private DatabaseReference database_reference;
    private FirebaseStorage storage;
    private StorageReference storage_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin_update_thumbnail);

        iv_tourist_spot_thumbnail = findViewById(R.id.iv_tourist_spot_thumbnail);
        btn_change_tourist_spot_thumbnail = findViewById(R.id.btn_change_tourist_spot_thumbnail);
        btn_update_tourist_spot_thumbnail_database = findViewById(R.id.btn_update_tourist_spot_thumbnail_database);
        progress_bar = findViewById(R.id.progress_bar);

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        // Get data from previous activity
        Intent intent = getIntent();
        original_str_uri_thumbnail = intent.getStringExtra("thumbnail");
        name = intent.getStringExtra("name");
        state = intent.getStringExtra("state");

        new_str_uri_thumbnail = "";

        // Display the data given from previous activity
        setData();

        // When the user click Select new thumbnail button
        btn_change_tourist_spot_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_THUMBNAIL);

            }
        });

        // When the user click Save thumbnail to database button
        btn_update_tourist_spot_thumbnail_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validation
                // Check if the user has selected new images
                if(new_str_uri_thumbnail.isEmpty()){
                    Snackbar.make(findViewById(R.id.main), "You cannot update using the same thumbnail.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    queryThumbnail();
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                new_str_uri_thumbnail = data.getData().toString();

                //Picasso makes loading the images from url easily
                Picasso
                        .get()
                        .load(new_str_uri_thumbnail)
                        .placeholder(R.drawable.image_placeholder)
                        .fit()
                        .centerCrop()
                        .into(iv_tourist_spot_thumbnail);
            }

        }

    }

    private void setData() {

        //Picasso makes loading the images from url easily
        Picasso
                .get()
                .load(original_str_uri_thumbnail)
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .into(iv_tourist_spot_thumbnail);

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
    private void queryThumbnail(){

        progress_bar.setVisibility(View.VISIBLE);

        Query query = database_reference.orderByChild("name").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    DataSnapshot matching_result = snapshot.getChildren().iterator().next();

                    // Now you have the reference to the matching entry, move to step 2
                    deleteOldThumbnailFromStorage(matching_result);
                }
                else{
                    Snackbar.make(findViewById(R.id.main), "Could not find the data you are looking for in Firebase Realtime Database.", Snackbar.LENGTH_LONG).show();
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

    // Step 2: Delete the old thumbnail from storage
    private void deleteOldThumbnailFromStorage(DataSnapshot matching_result) {

        String original_str_uri_thumbnail_db = matching_result.child("tourist_spot_uri").getValue(TouristSpot_Uri.class).getThumbnail_uri();

        if(original_str_uri_thumbnail_db == null || original_str_uri_thumbnail_db.isEmpty()){
            Snackbar.make(findViewById(R.id.main), "No thumbnail to delete in Firebase Storage.", Snackbar.LENGTH_LONG).show();
            progress_bar.setVisibility(View.GONE);
            return;
        }

        StorageReference image_reference = FirebaseStorage.getInstance().getReferenceFromUrl(original_str_uri_thumbnail_db);

        image_reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Proceed to upload the new thumbnail
                    updateNewThumbnailToStorage(matching_result);
                }
                else {
                    Snackbar.make(findViewById(R.id.main), "Failed to delete old thumbnail from Firebase Storage.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });

    }

    // Step 3: Upload the new thumbnail to Firebase Storage
    private void updateNewThumbnailToStorage(DataSnapshot matching_result){

        // Before storing the image to FirebaseStorage, get the image's extension
        // getFileExtension() will return strings like "jpg", "png", etc. Without the dot.
        String image_extension = getFileExtension(new_str_uri_thumbnail);

        if(image_extension.isEmpty()){
            image_extension = "." + "jpg";
        }
        else{
            image_extension = "." + image_extension;
        }

        StorageReference file_reference = storage_reference.child("TouristSpotGallery/" + System.currentTimeMillis() + image_extension);

        file_reference.putFile(Uri.parse(new_str_uri_thumbnail))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri download_url) {
                                // Update the thumbnail URI in the database
                                updateNewThumbnailToRealtimeDatabase(matching_result.getRef(), download_url.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.main), "Failed to upload new thumbnail in Firebase Storage.", Snackbar.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    // Step 4: Update the photos Uris in Firebase Realtime Database
    private void updateNewThumbnailToRealtimeDatabase(DatabaseReference reference, String new_str_uri_thumbnail){

        Map<String, Object> updates = new HashMap<>();
        updates.put("tourist_spot_uri/thumbnail_uri", new_str_uri_thumbnail);

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

                                    Toast.makeText(Page_Admin_Update_Thumbnail_Activity.this, "Thumbnail updated successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Page_Admin_Update_Thumbnail_Activity.this, Page_Admin_Activity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }, 2000); // The duration of the animation (2 seconds in this case)
                        }
                    }, 0);

                }
                else {
                    Snackbar.make(findViewById(R.id.main), "Failed to update the thumbnail in Firebase Realtime Database.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });

    }

}