package com.example.assignment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Video#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Video extends Fragment {

    // This is where the video will actually play on the screen.
    // Think of it as the "TV" screen for the video.
    StyledPlayerView player_view;

    // This will control the video, like play, pause, or stop
    ExoPlayer exo_player;

    // This holds specific information about the video file chosen by the user,
    // so that the ExoPlayer (exo_player) knows what to play
    MediaItem media_item;

    // Key for passing object
    private static final String ARG_TOURIST_SPOT = "tourist_spot";

    // The object that will be received
    private TouristSpot tourist_spot;

    public Fragment_Video() {
        // Required empty public constructor
    }


    public static Fragment_Video newInstance(TouristSpot spot) {

        Fragment_Video fragment = new Fragment_Video();

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
        View view = inflater.inflate(R.layout.fragment__video, container, false);

        player_view = view.findViewById(R.id.player_view);

        // Create an instance of ExoPlayer
        exo_player = new ExoPlayer.Builder(getContext()).build();

        // Makes sure the video played by exo_player will actually be displayed in the player_view component
        player_view.setPlayer(exo_player);

        // Checks if the exo_player has been created yet.
        // If it is null, it means it is not created, so we need to create it.
        if (exo_player == null) {
            // Reinitialize ExoPlayer if it's null
            exo_player = new ExoPlayer.Builder(getContext()).build();
            player_view.setPlayer(exo_player);
        }

        // Create a media item using the uri of the selected video.
        // This object (media_item) contains all necessary info about the video that will be played.
        media_item = MediaItem.fromUri(tourist_spot.tourist_spot_uri.getVideo_uri());

        // Tells exo_player what video to play
        exo_player.setMediaItem(media_item);

        // Loads the video into memory and gets everything ready to play
        exo_player.prepare();

        // Play the video
        exo_player.setPlayWhenReady(true);

        return view;
    }

    // onStart means what happens when the activity starts
    @Override
    public void onStart() {
        super.onStart();

        // We want to ensure that the video player is ready whenever the activity comes into view

        // Reinitialize ExoPlayer if it has been released
        if (exo_player == null) {
            exo_player = new ExoPlayer.Builder(getContext()).build();
            player_view.setPlayer(exo_player);
        }
    }

    // onStop means what happens when the activity closes
    @Override
    public void onStop() {
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
}