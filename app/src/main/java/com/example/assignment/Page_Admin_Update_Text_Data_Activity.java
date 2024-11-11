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
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Page_Admin_Update_Text_Data_Activity extends AppCompatActivity {

    private EditText et_tourist_spot_name, et_tourist_spot_address, et_tourist_spot_coordinates, et_tourist_spot_description;
    private PowerSpinnerView sp_tourist_spot_state, sp_tourist_spot_category;
    private MaterialButton btn_update_text_data;
    private CircularProgressIndicator progress_bar;

    private List<IconSpinnerItem> iconSpinnerItems_state;
    private List<IconSpinnerItem> iconSpinnerItems_category;

    private String original_name, original_description, original_address, original_coordinates, original_state, original_category;
    private List<String> original_list_of_str_uri_photos;

    private String new_state, new_name;

    private FirebaseStorage storage;
    private StorageReference storage_reference;
    DatabaseReference database_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin_update_text_data);

        et_tourist_spot_name = findViewById(R.id.et_tourist_spot_name);
        et_tourist_spot_address = findViewById(R.id.et_tourist_spot_address);
        et_tourist_spot_coordinates = findViewById(R.id.et_tourist_spot_coordinates);
        et_tourist_spot_description = findViewById(R.id.et_tourist_spot_description);
        sp_tourist_spot_state = findViewById(R.id.sp_tourist_spot_state);
        sp_tourist_spot_category = findViewById(R.id.sp_tourist_spot_category);
        btn_update_text_data = findViewById(R.id.btn_update_text_data);
        progress_bar = findViewById(R.id.progress_bar);

        original_list_of_str_uri_photos = new ArrayList<>();

        database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("TouristSpot");
        storage = FirebaseStorage.getInstance();
        storage_reference = storage.getReference();

        // Create all the spinner items. Each item will have a text and image (flag)
        iconSpinnerItems_state = new ArrayList<>();
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
        IconSpinnerAdapter iconSpinnerAdapter_state = new IconSpinnerAdapter(sp_tourist_spot_state);
        sp_tourist_spot_state.setSpinnerAdapter(iconSpinnerAdapter_state);
        sp_tourist_spot_state.setItems(iconSpinnerItems_state);
        sp_tourist_spot_state.selectItemByIndex(0);
        sp_tourist_spot_state.setLifecycleOwner(this);

        // Create all the spinner items for Category. Each item will have only text
        iconSpinnerItems_category = new ArrayList<>();
        iconSpinnerItems_category.add(new IconSpinnerItem("Historical"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Hotel"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Museum"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Park"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Restaurant"));
        iconSpinnerItems_category.add(new IconSpinnerItem("Wildlife"));

        // Put all those items into the spinner
        IconSpinnerAdapter iconSpinnerAdapter_category = new IconSpinnerAdapter(sp_tourist_spot_category);
        sp_tourist_spot_category.setSpinnerAdapter(iconSpinnerAdapter_category);
        sp_tourist_spot_category.setItems(iconSpinnerItems_category);
        sp_tourist_spot_category.selectItemByIndex(0);
        sp_tourist_spot_category.setLifecycleOwner(this);

        // Toast.makeText(this, iconSpinnerItems.get(1).getText(), Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        original_name = intent.getStringExtra("name");
        original_address = intent.getStringExtra("address");
        original_coordinates = intent.getStringExtra("coordinates");
        original_description = intent.getStringExtra("description");
        original_state = intent.getStringExtra("state");
        original_category = intent.getStringExtra("category");

        setData();

        btn_update_text_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Extract all data from the input fields
                new_name = et_tourist_spot_name.getText().toString().trim();
                String new_description = et_tourist_spot_description.getText().toString().trim();
                new_state = sp_tourist_spot_state.getText().toString();
                String new_category = sp_tourist_spot_category.getText().toString();
                String new_address = et_tourist_spot_address.getText().toString().trim();
                String new_coordinates = et_tourist_spot_coordinates.getText().toString().trim();

                // Input validation
                if(new_name.isEmpty() || new_description.isEmpty() || new_address.isEmpty() || new_coordinates.isEmpty()){
                    Snackbar.make(findViewById(R.id.main), "Please fill in all fields.", Snackbar.LENGTH_LONG).show();
                }

                else if((new_name.equals(original_name)) &&
                        (new_address.equals(original_address)) &&
                        (new_description.equals(original_description)) &&
                        (new_state.equals(original_state)) &&
                        (new_coordinates.equals(original_coordinates))){
                    Snackbar.make(findViewById(R.id.main), "You have not changed anything. Please provide new text data.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    // Set up the progress bar accordingly
                    progress_bar.setVisibility(View.VISIBLE);

                    // Upload the Thumbnail first
                    updateTextData(new_name, new_description, new_state, new_category, new_address, new_coordinates);
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // After receiving data from previous activity, set the edittext, ImageView accordingly with the data received
    private void setData() {

        et_tourist_spot_name.setText(original_name);
        et_tourist_spot_description.setText(original_description);
        et_tourist_spot_address.setText(original_address);
        et_tourist_spot_coordinates.setText(original_coordinates);

        for(int i = 0; i < iconSpinnerItems_state.size(); i++){
            if(iconSpinnerItems_state.get(i).getText().toString().equals(original_state)){
                sp_tourist_spot_state.selectItemByIndex(i);
                break;
            }
        }

        for(int i = 0; i < iconSpinnerItems_category.size(); i++){
            if(iconSpinnerItems_category.get(i).getText().toString().equals(original_category)){
                sp_tourist_spot_category.selectItemByIndex(i);
                break;
            }
        }
    }

    // Step 1: Query the database and update the text data
    private void updateTextData(String new_name, String new_description, String new_state, String new_category, String new_address, String new_coordinates) {

        // Query to find the data based on the criteria
        // Apparently, Firebase does not support AND operator, when querying data.
        // So, the name key must be unique in your database
        Query query = database_reference.orderByChild("name").equalTo(original_name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // If any data matches the query
                if(snapshot.exists()){

                    // Write this line of code if you know there is only one matching result, not multiple matching results
                    DataSnapshot matched_result = snapshot.getChildren().iterator().next();

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", new_name);
                    updates.put("state", new_state);
                    updates.put("category", new_category);
                    updates.put("description", new_description);
                    updates.put("address", new_address);
                    updates.put("coordinates", new_coordinates);

                    // Update the entry
                    matched_result.getRef().updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                            }
                                        }, 2000); // The duration of the animation (2 seconds in this case)
                                    }
                                }, 0);

                                Toast.makeText(getApplicationContext(), "Text Data update successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Page_Admin_Update_Text_Data_Activity.this, Page_Admin_Activity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {
                                Snackbar.make(findViewById(R.id.main), "Update failed: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                progress_bar.setVisibility(View.GONE);
                            }
                        }
                    });

                }

                // If no data matches the query
                else{
                    Snackbar.make(findViewById(R.id.main), "Update failed. Could not find the data you were looking for.", Snackbar.LENGTH_LONG).show();
                    progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(R.id.main), "Query cancelled: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                progress_bar.setVisibility(View.GONE);
            }
        });

    }

}