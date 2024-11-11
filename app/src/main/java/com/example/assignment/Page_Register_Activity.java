package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Page_Register_Activity extends AppCompatActivity {

    private TextInputEditText et_user_email, et_user_password;
    private Button btn_register;
    private TextView tv_alreadyHaveAnAccount;
    private ProgressBar progressbar;

    private FirebaseAuth mAuth;

    // Check to see if the user is logged in or not, but the user must be logged in
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Toast.makeText(this, "hereeeeee", Toast.LENGTH_SHORT).show();
//        if(currentUser != null && currentUser.isEmailVerified()){
//            Intent intent = new Intent(getApplicationContext(), Page_Home_Page_Activity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_page);

        et_user_email = findViewById(R.id.et_user_email);
        et_user_password = findViewById(R.id.et_user_password);
        btn_register = findViewById(R.id.btn_register);
        tv_alreadyHaveAnAccount = findViewById(R.id.tv_alreadyHaveAnAccount);
        progressbar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        // Receiving data from reCAPTCHA page
        Intent received_data = getIntent();

        // Check if Register Page has received any data (from reCAPTCHA page)
        if(received_data.hasExtra("user_email") && received_data.hasExtra("user_password")){
            et_user_email.setText(received_data.getStringExtra("user_email"));
            et_user_password.setText(received_data.getStringExtra("user_password"));
            registerUser(et_user_email.getText().toString(), et_user_password.getText().toString());
        }

        // The user clicks the Register button
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUser();
            }
        });

        // The user clicks on the "Already have an account?" text
        tv_alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Register_Activity.this, Page_Login_Activity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void registerUser(String user_email, String user_password){

        // In the bracket, there is email and password parameters
        // This email and password is from edit text. We then passed them two into createUserWithEmailAndPassword
        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressbar.setVisibility(View.GONE);

                        // If there are no issues before the database can register the user
                        if (task.isSuccessful()) {

                            // Send email verification
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // If email verification is successful
                                    if(task.isSuccessful()){
                                        Toast.makeText(Page_Register_Activity.this, "Please verify your email to continue.", Toast.LENGTH_SHORT).show();
                                        et_user_email.setText("");
                                        et_user_password.setText("");
                                    }

                                    // Email verification fails
                                    else{
                                        Snackbar.make(findViewById(R.id.main), "Email verification fails. " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });


                        }
                        else {

                            // Check if error is due to email already existing
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Snackbar.make(findViewById(R.id.main), "This account already exists.", Snackbar.LENGTH_LONG).show();
                            }
                            // Otherwise, it means sign in fails. Display a message to the user.
                            else{
                                try{
                                    throw task.getException();
                                }
                                catch (Exception e) {
                                    Snackbar.make(findViewById(R.id.main), "Register failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }

                        }
                    }
                });

    }

    public void validateUser(){

        String user_email = String.valueOf(et_user_email.getText());
        String user_password = String.valueOf(et_user_password.getText());

        btn_register.setEnabled(false);
        progressbar.setVisibility(View.VISIBLE);

        if(isEmailAndPasswordInvalid(user_email, user_password)){
            progressbar.setVisibility(View.GONE);
            btn_register.setEnabled(true);
        }
        else{
            Intent intent = new Intent(Page_Register_Activity.this, Page_reCAPTCHA_Activity.class);
            intent.putExtra("user_email", user_email);
            intent.putExtra("user_password", user_password);
            startActivity(intent);
        }

    }

    public boolean isEmailAndPasswordInvalid(String user_email, String user_password){

        // If the user did not input both email and password
        if(user_email.isEmpty() || user_password.isEmpty()){
            Snackbar.make(findViewById(R.id.main), "Please provide both email and password.", Snackbar.LENGTH_LONG).show();
            return true;
        }

        // If the user did not provide a valid email address format
        if(!Patterns.EMAIL_ADDRESS.matcher(user_email).matches()){
            Snackbar.make(findViewById(R.id.main), "Provide a valid email address.", Snackbar.LENGTH_LONG).show();
            return true;
        }

        // Check if the password input is a strong password
        if(isPasswordWeak(user_password)){
            Snackbar.make(findViewById(R.id.main), "Password must include uppercase, lowercase, digits, and special characters.", Snackbar.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    public boolean isPasswordWeak(String user_password){

        // Check if password length is less than 8
        if(user_password.length() < 8){
            return true;
        }
        
        // Check if password length is more than 20
        else if(user_password.length() > 20){
            return true;
        }
        
        // Check if password contain uppercase, lowercase and digit
        else{

            boolean contains_upper_case = false;
            boolean contains_lowercase_case = false;
            boolean contains_digit = false;
            boolean contains_special_char = false;

            for (int i = 0; i < user_password.length(); i++) {

                char curr_char = user_password.charAt(i);

                if(Character.isUpperCase(curr_char)){
                    contains_upper_case = true;
                }
                else if(Character.isLowerCase(curr_char)){
                    contains_lowercase_case = true;
                }
                else if(Character.isDigit(curr_char)){
                    contains_digit = true;
                }
                else if(isSpecialCharacter(curr_char)){
                    contains_special_char = true;
                }

            }
            
            // If contains uppercase, lowercase and digit, then the password is not weak
            if(contains_upper_case && contains_lowercase_case && contains_digit & contains_special_char){
                return false;
            }
            
            // Otherwise, password is weak
            else{
                return true;
            }

        }


    }

    public boolean isSpecialCharacter(char c) {
        // Convert the character to a string and check with regex
        return String.valueOf(c).matches("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");
    }

}
