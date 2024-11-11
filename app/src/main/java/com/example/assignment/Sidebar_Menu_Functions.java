package com.example.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Sidebar_Menu_Functions {

    public static void open_home_page(Context context){
        Intent intent = new Intent(context, Page_Home_Page_Activity.class);
        context.startActivity(intent);
    }

    public static void open_image_recognition_page(Context context){
        Intent intent = new Intent(context, Page_Image_Recognition_Activity.class);
        context.startActivity(intent);
    }

    public static void open_random_photos_page(Context context){
        Intent intent = new Intent(context, Page_Random_Photos_Activity.class);
        context.startActivity(intent);
    }

    public static void open_todo_page(Context context){
        Intent intent = new Intent(context, Page_ToDo_Activity.class);
        context.startActivity(intent);
    }

    public static void open_currency_converter_page(Context context){
        Intent intent = new Intent(context, Page_Currency_Converter_Activity.class);
        context.startActivity(intent);
    }

    public static void open_translate_page(Context context){
        Intent intent = new Intent(context, Page_Translate_Activity.class);
        context.startActivity(intent);
    }

    public static void open_tourist_spot_map_page(Context context){
        Intent intent = new Intent(context, Page_TouristSpot_Map_Activity.class);
        context.startActivity(intent);
    }

    public static void open_weather_page(Context context){
        Intent intent = new Intent(context, Page_Weather_Activity.class);
        context.startActivity(intent);
    }

    public static void open_settings_page(Context context){
        Intent intent = new Intent(context, Page_Settings_Activity.class);
        context.startActivity(intent);
    }

    public static void logout(Context context, GoogleSignInClient googleSignInClient) {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        Toast.makeText(context, "Logged out.", Toast.LENGTH_SHORT).show();

        // Redirect to Login Page
        Intent intent = new Intent(context, Page_Login_Activity.class);
        context.startActivity(intent);

        // If context is an Activity, finish it
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

}

