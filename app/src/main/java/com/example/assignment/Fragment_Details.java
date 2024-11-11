package com.example.assignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Details extends Fragment {

    // Key for passing object
    private static final String ARG_TOURIST_SPOT = "tourist_spot";

    // The object that will be received
    private TouristSpot tourist_spot;

    // Other useful variables
    TextView tv_tourist_spot_rating;
    RatingBar rb_user_rating_num_of_stars;
    ArrayList<UserRatings> list_of_ratings = new ArrayList<>();

    public Fragment_Details() {
        // Required empty public constructor
    }

    public static Fragment_Details newInstance(TouristSpot spot) {

        Fragment_Details fragment = new Fragment_Details();

        Bundle args = new Bundle();

        // Put the object into the bundle
        args.putSerializable(ARG_TOURIST_SPOT, spot);

        // Pass the arguments to the fragment
        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // Retrieve the object from the bundle
            tourist_spot = (TouristSpot) getArguments().getSerializable(ARG_TOURIST_SPOT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__details, container, false);

        // Access the TextView and set the text
        ImageView iv_tourist_spot_thumbnail = view.findViewById(R.id.iv_tourist_spot_thumbnail);
        TextView tv_tourist_spot_name = view.findViewById(R.id.tv_tourist_spot_name);
        TextView tv_tourist_spot_address = view.findViewById(R.id.tv_tourist_spot_address);
        TextView tv_tourist_spot_category = view.findViewById(R.id.tv_tourist_spot_category);
        MaterialCardView card_view_ratings = view.findViewById(R.id.card_view_ratings);
        tv_tourist_spot_rating = view.findViewById(R.id.tv_tourist_spot_rating);
        rb_user_rating_num_of_stars = view.findViewById(R.id.rb_user_rating_num_of_stars);
        TextView tv_tourist_spot_description = view.findViewById(R.id.tv_tourist_spot_description);
        MaterialButton btn_find_route = view.findViewById(R.id.btn_find_route);
        MaterialButton btn_locate_tourist_spot_on_map = view.findViewById(R.id.btn_locate_tourist_spot_on_map);

        // card_view_ratings.setClickable(false);

        //Picasso makes loading the images from url easily
        Picasso
                .get()
                .load(tourist_spot.tourist_spot_uri.getThumbnail_uri())
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .into(iv_tourist_spot_thumbnail);

        // Set text data
        tv_tourist_spot_name.setText(tourist_spot.getName());
        tv_tourist_spot_address.setText(String.format("%s, %s", tourist_spot.getAddress(), tourist_spot.getState()));
        tv_tourist_spot_category.setText(tourist_spot.getCategory());
        tv_tourist_spot_description.setText(tourist_spot.getDescription());

        calculateRating(tourist_spot.getName());

        btn_find_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Page_Input_Location_Map_Route_Activity.class);
                intent.putExtra("coordinates_location_from", tourist_spot.getCoordinates());
                startActivity(intent);
            }
        });

        btn_locate_tourist_spot_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(tourist_spot.getName()));
                Uri map_uri = Uri.parse("geo:" + tourist_spot.getCoordinates() + "?q=" + Uri.parse(tourist_spot.getCoordinates() + "(label)"));

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, map_uri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                else {
                    Toast.makeText(getContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        card_view_ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Page_View_All_Ratings_Activity.class);
                intent.putExtra("tour_name", tourist_spot.getName());
                intent.putExtra("tour_thumbnail", tourist_spot.tourist_spot_uri.getThumbnail_uri());
                startActivity(intent);
            }
        });

        return view;

    }

    // Load ratings about a tour
    private void calculateRating(String tour_name) {

        // Initialize the Firebase Database reference
        DatabaseReference database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("UserRatings");

        // Create a query to search for a specific name
        Query query = database_reference.orderByChild("tour_name").equalTo(tour_name);

        // Attach a listener to the query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total = 0;

                // If there exist at least one matched document
                if(snapshot.exists()){

                    long num_of_ratings = snapshot.getChildrenCount();
                    for(DataSnapshot data_snapshot : snapshot.getChildren()){
                        total = total + data_snapshot.child("num_of_stars").getValue(Integer.class);
                    }

                    // float average = (float) (total / num_of_ratings);
                    float average = (float) total /num_of_ratings;
                    average = (float) (Math.round(average * 100.0) / 100.0);

                    // Toast.makeText(getContext(), String.valueOf(total), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getContext(), String.valueOf(num_of_ratings), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getContext(), String.valueOf(average), Toast.LENGTH_SHORT).show();

                    rb_user_rating_num_of_stars.setVisibility(View.VISIBLE);
                    tv_tourist_spot_rating.setVisibility(View.VISIBLE);
                    tv_tourist_spot_rating.setText(String.valueOf(average));
                    rb_user_rating_num_of_stars.setRating(average);

                }

                // If the database cannot find tourist spot with that name
                else{
                    rb_user_rating_num_of_stars.setVisibility(View.GONE);
                    tv_tourist_spot_rating.setVisibility(View.VISIBLE);
                    tv_tourist_spot_rating.setText("No ratings yet. Be the first to rate!");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                rb_user_rating_num_of_stars.setVisibility(View.GONE);
                tv_tourist_spot_rating.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

}