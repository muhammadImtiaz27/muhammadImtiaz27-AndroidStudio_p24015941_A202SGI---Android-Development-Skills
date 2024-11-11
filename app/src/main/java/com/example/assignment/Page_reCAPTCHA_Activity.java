package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaTasksClient;

public class Page_reCAPTCHA_Activity extends AppCompatActivity {

    // This key is from i hope this works
    private String site_key = "6LclGnIqAAAAAJV0cSZ8N_a0TXMRcpvNgzhKsp0e";
    private MaterialCheckBox cb_not_robot;

    private RecaptchaTasksClient recaptchaTasksClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_re_captcha);

        cb_not_robot = findViewById(R.id.cb_not_robot);

        cb_not_robot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    initializeRecaptchaClient();
                }
            }
        });
    }

    private void initializeRecaptchaClient() {
        Recaptcha
                .getTasksClient(getApplication(), site_key)
                .addOnSuccessListener(new OnSuccessListener<RecaptchaTasksClient>() {
                    @Override
                    public void onSuccess(RecaptchaTasksClient client) {
                        recaptchaTasksClient = client;
                        Toast.makeText(Page_reCAPTCHA_Activity.this, "reCAPTCHA Initialized", Toast.LENGTH_SHORT).show();

                        // Receiving data from reCAPTCHA page
                        Intent received_data = getIntent();

                        // Check if Register Page has received any data (from reCAPTCHA page)
                        if(received_data.hasExtra("user_email") && received_data.hasExtra("user_password")){
                            Intent send_data = new Intent(Page_reCAPTCHA_Activity.this, Page_Register_Activity.class);
                            send_data.putExtra("user_email", received_data.getStringExtra("user_email"));
                            send_data.putExtra("user_password", received_data.getStringExtra("user_password"));
                            startActivity(send_data);
                            finish();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MainActivity", "reCAPTCHA Initialization failed", e);
                        Toast.makeText(Page_reCAPTCHA_Activity.this, "reCAPTCHA Initialization failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}