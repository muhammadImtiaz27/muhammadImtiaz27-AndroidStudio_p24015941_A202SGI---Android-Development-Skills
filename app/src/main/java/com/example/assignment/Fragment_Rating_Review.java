package com.example.assignment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class Fragment_Rating_Review extends Fragment {

    private static final String ARG_STAR_1 = "arg_star_1";
    private static final String ARG_STAR_2 = "arg_star_2";
    private static final String ARG_STAR_3 = "arg_star_3";
    private static final String ARG_STAR_4 = "arg_star_4";
    private static final String ARG_STAR_5 = "arg_star_5";
    private static final String ARG_AVERAGE = "arg_average";

    public Fragment_Rating_Review() {
        // Required empty public constructor
    }

    public static Fragment_Rating_Review newInstance(int star_1, int star_2, int star_3, int star_4, int star_5, float average) {
        Fragment_Rating_Review fragment = new Fragment_Rating_Review();
        Bundle args = new Bundle();
        args.putInt(ARG_STAR_1, star_1);
        args.putInt(ARG_STAR_2, star_2);
        args.putInt(ARG_STAR_3, star_3);
        args.putInt(ARG_STAR_4, star_4);
        args.putInt(ARG_STAR_5, star_5);
        args.putFloat(ARG_AVERAGE, average);
        fragment.setArguments(args); // Set the arguments for the fragment
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__rating__review, container, false);

        TextView tv_average_rating = view.findViewById(R.id.tv_average_rating);
        RatingBar rb_average_rating = view.findViewById(R.id.rb_average_rating);
        LinearProgressIndicator horizontal_bar_5_star = view.findViewById(R.id.horizontal_bar_5_star);
        LinearProgressIndicator horizontal_bar_4_star = view.findViewById(R.id.horizontal_bar_4_star);
        LinearProgressIndicator horizontal_bar_3_star = view.findViewById(R.id.horizontal_bar_3_star);
        LinearProgressIndicator horizontal_bar_2_star = view.findViewById(R.id.horizontal_bar_2_star);
        LinearProgressIndicator horizontal_bar_1_star = view.findViewById(R.id.horizontal_bar_1_star);

        // Retrieve the argument passed to the fragment
        if (getArguments() != null) {

            int star_1 = getArguments().getInt(ARG_STAR_1);
            int star_2 = getArguments().getInt(ARG_STAR_2);
            int star_3 = getArguments().getInt(ARG_STAR_3);
            int star_4 = getArguments().getInt(ARG_STAR_4);
            int star_5 = getArguments().getInt(ARG_STAR_5);
            float average = getArguments().getFloat(ARG_AVERAGE);

            // Calculate the total space each horizontal bar will take
            int total_raters = star_1 + star_2 + star_3 + star_4 + star_5;

            horizontal_bar_5_star.setProgress((int) (((double) star_5 / total_raters) * 100));
            horizontal_bar_4_star.setProgress((int) (((double) star_4 / total_raters) * 100));
            horizontal_bar_3_star.setProgress((int) (((double) star_3 / total_raters) * 100));
            horizontal_bar_2_star.setProgress((int) (((double) star_2 / total_raters) * 100));
            horizontal_bar_1_star.setProgress((int) (((double) star_1 / total_raters) * 100));

            // Set the retrieved text on the TextView
            tv_average_rating.setText(String.format("Average: %s", average));
            rb_average_rating.setRating(average);

        }

        return view;
    }
}