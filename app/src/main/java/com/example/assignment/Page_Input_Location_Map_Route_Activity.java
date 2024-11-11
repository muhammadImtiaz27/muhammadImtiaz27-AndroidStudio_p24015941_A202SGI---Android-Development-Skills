package com.example.assignment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class Page_Input_Location_Map_Route_Activity extends AppCompatActivity {

    private String coordinates_location_from;

    // This variable is used for accessing location services.
    private FusedLocationProviderClient fused_location_client;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_input_location_map_route);

        Intent intent = getIntent();

        if(intent.hasExtra("coordinates_location_from")){
            coordinates_location_from = intent.getStringExtra("coordinates_location_from");
        }
        else{
            startActivity(new Intent(Page_Input_Location_Map_Route_Activity.this, Page_Home_Page_Activity.class));
            finish();
        }

        // Initializes fused_location_client to access location services.
        // It allows the app to get the user's current location in a battery-efficient way.
        fused_location_client = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // This function handles checking permissions and getting the user’s current location
    private void getCurrentLocation(){

        // Checks if the app has permission to access the user’s precise location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // This line of code tries to get the last known location of the user
            fused_location_client.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() { // This method will be called when the location is successfully retrieved.
                        @Override
                        public void onSuccess(Location location) {

                            // Checks if the location is not null, meaning a location was found
                            if(location != null){
                                // If a location is found, it retrieves the latitude and longitude of the current location
                                double current_latitude = location.getLatitude();
                                double current_longitude = location.getLongitude();

                                // Now we have the current location, proceed to open Google Maps with a route
                                showRouteOnMap(current_latitude, current_longitude, coordinates_location_from);
                            }

                            // If the location is null (meaning the app could not find a current location)
                            else{
                                Snackbar.make(findViewById(R.id.main), "Unable to get current location.", Snackbar.LENGTH_LONG).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() { // If there’s an error while trying to get the location, this line catches that error
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.main), "Error when getting the current location.", Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
        else {
            // If location permissions are not granted,
            // this line of code requests the necessary permission from the user.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    // Prepares a URL to open Google Maps with directions
    private void showRouteOnMap(double from_latitude, double from_longitude, String coordinates_destination) {

        // Create a Uri for Google Maps directions, from current location to the destination
        // Uri map_uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + from_latitude + "," + from_longitude + "&destination=" + Uri.encode(destination) + "&travelmode=driving");
        Uri map_uri = Uri.parse("https://www.google.co.in/maps/dir/" + from_latitude + "," + from_longitude + "/" + coordinates_destination);

        // Create an intent to open Google Maps using the uri we constructed from above
        // Intent.ACTION_VIEW indicates that we want to view something (in this case, a map)
        // setPackage("com.google.android.apps.maps") specifies that we want to use the Google Maps app to handle this intent.
        // This way, it ensures that the intent is specifically targeted to Google Maps.
        Intent map_intent = new Intent(Intent.ACTION_VIEW, map_uri);
        map_intent.setPackage("com.google.android.apps.maps");

        // Checks if there is an app available that can handle the intent (i.e., if Google Maps is installed).
        if (map_intent.resolveActivity(getPackageManager()) != null) {

            // If there is an app that can handle the intent,
            // startActivity(mapIntent) is called to open Google Maps and display the route.
            startActivity(map_intent);
        }

        // If Google Maps is not installed
        else {
            Snackbar.make(findViewById(R.id.main), "Google Maps is not installed", Snackbar.LENGTH_LONG).show();
        }
    }

    // This method is called when the user responds to the permission request dialog.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // It checks if the permission request code matches the one we defined earlier (LOCATION_PERMISSION_REQUEST_CODE).
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            // If permission is granted, get the location
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }

            // If permission is denied
            else {
                Snackbar.make(findViewById(R.id.main), "Location permission is required to show the route", Snackbar.LENGTH_LONG).show();
            }

        }

    }

}