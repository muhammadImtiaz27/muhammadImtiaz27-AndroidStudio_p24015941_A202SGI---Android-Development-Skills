package com.example.assignment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Page_Admin_Activity extends AppCompatActivity {

    Button btn_create_tourist_spot, btn_get_tourist_spot;

    // Custom Dialog for Confirm Delete
    private Dialog dialog;
    private TextView tv_btn_yes, tv_btn_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_admin);

        btn_create_tourist_spot = findViewById(R.id.btn_create_tourist_spot);
        btn_get_tourist_spot = findViewById(R.id.btn_get_tourist_spot);

        // The Create Tourist Spot button is clicked
        btn_create_tourist_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Page_Admin_Activity.this, Page_Admin_Add_TouristSpot_Activity.class);
                startActivity(intent);

            }
        });

        // The Get Tourist Spot button is clicked
        btn_get_tourist_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Page_Admin_Activity.this, Page_Admin_Get_TouristSpot_Activity.class);
                startActivity(intent);

            }
        });

        // Use the OnBackPressedDispatcher to handle the back button event
        // It means what happens when the user click the back button on their phone
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutDialogBox();
            }
        };

        // Add the callback to the back press dispatcher
        // This is just like callback functions in JavaScript. It is listening for the back button to be clicked
        // When the back button is clicked, the function named callback, is executed.
        getOnBackPressedDispatcher().addCallback(Page_Admin_Activity.this, callback);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showLogoutDialogBox() {

        // Create the dialog
        dialog = new Dialog(Page_Admin_Activity.this);
        dialog.setContentView(R.layout.custom_dialog_box_logout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // The user is able to close the dialog box by clicking anywhere outside the dialog box
        dialog.setCancelable(true);

        // Use dialog.findViewById() to get views from the dialog layout
        tv_btn_yes = dialog.findViewById(R.id.tv_btn_yes);
        tv_btn_no = dialog.findViewById(R.id.tv_btn_no);

        // Close the dialog when the No button is clicked
        tv_btn_no.setOnClickListener(v -> dialog.dismiss());

        // Logout when the Yes button is clicked
        tv_btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Page_Admin_Activity.this, Page_Login_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // Show the dialog
        dialog.show();

    }

}

