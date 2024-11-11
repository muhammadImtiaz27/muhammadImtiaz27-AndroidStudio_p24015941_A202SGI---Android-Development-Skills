package com.example.assignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class Page_Reset_Password_Activity extends AppCompatActivity {

    private TextInputEditText et_user_email;
    private MaterialButton btn_reset;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_reset_password);

        et_user_email = findViewById(R.id.et_user_email);
        btn_reset = findViewById(R.id.btn_reset);

        mAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = String.valueOf(et_user_email.getText());

                if(user_email.isEmpty()){
                    Toast.makeText(Page_Reset_Password_Activity.this, "Please provide your email.", Toast.LENGTH_SHORT).show();
                }
                else{
                    checkEmailExistsOrNot(user_email);
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void checkEmailExistsOrNot(String user_email){

        mAuth.fetchSignInMethodsForEmail(user_email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        // If email does not exist
                        if (task.getResult().getSignInMethods().size() == 0){
                            sendPasswordResetEmail(user_email);
                        }

                        // If email does exist
                        else {
                            Toast.makeText(Page_Reset_Password_Activity.this, "The email you provided does not exist.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    public void sendPasswordResetEmail(String user_email){

        mAuth.sendPasswordResetEmail(user_email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // If sending password reset email successful
                        if(task.isSuccessful()){
                            Toast.makeText(Page_Reset_Password_Activity.this, "Please check your email to reset your password", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Toast.makeText(Page_Reset_Password_Activity.this, "An error occurred when trying to send you an email to reset your password. Please try again later.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

}