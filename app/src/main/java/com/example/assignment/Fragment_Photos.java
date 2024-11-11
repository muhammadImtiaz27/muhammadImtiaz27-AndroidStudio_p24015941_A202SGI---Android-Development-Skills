package com.example.assignment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Fragment_Photos extends Fragment {

    // Key for passing object
    private static final String ARG_TOURIST_SPOT = "tourist_spot";

    // The object that will be received
    private TouristSpot tourist_spot;

    public Fragment_Photos() {
        // Required empty public constructor
    }

    public static Fragment_Photos newInstance(TouristSpot spot) {

        Fragment_Photos fragment = new Fragment_Photos();

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
        View view = inflater.inflate(R.layout.fragment__photos, container, false);

        // Access the TextView and set the text
        LinearLayout ll_photos_container = view.findViewById(R.id.ll_photos_container);

        // First, remove all views inside the Linear Layout
        ll_photos_container.removeAllViews();

        // Loop through the list
        for(int i = 0; i < tourist_spot.tourist_spot_uri.getList_of_images_uri().size(); i++){

            // Convert the string into Uri
            Uri image_uri = Uri.parse(tourist_spot.tourist_spot_uri.getList_of_images_uri().get(i));

            // Programmatically create an imageView
            ImageView imageView = new ImageView(getContext());

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
                    .load(tourist_spot.tourist_spot_uri.getList_of_images_uri().get(i))
                    .placeholder(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(imageView);

            ll_photos_container.addView(imageView);

        }

        return view;

    }
}