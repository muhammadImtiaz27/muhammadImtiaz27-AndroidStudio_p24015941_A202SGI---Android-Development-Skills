package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class Page_Login_Activity extends AppCompatActivity {

    private SharedPreferences user_settings, user_info;

    private TextView tv_dontHaveAnAccount, tv_forgotPassword;
    private EditText et_user_email, et_user_password;
    private Button btn_login;
    private ProgressBar progressbar;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private ShapeableImageView imageView;

    // Firebase
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    // For the Google sign in button
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);

                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser user = mAuth.getCurrentUser();

                                // Check if it is a new user
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                                // If it is a new user
                                if(isNewUser){
                                    Toast.makeText(Page_Login_Activity.this, "Welcome, new user!", Toast.LENGTH_LONG).show();

                                    User new_user = new User(user.getEmail(), "null"); // Null is just a placeholder. Because we are not going to store any image for new user
                                    uploadUserData(new_user);
                                }

                                // If it is not a new user
                                else{
                                    setUserSharedPreference(user.getEmail());
                                }

                                Intent intent = new Intent(Page_Login_Activity.this, Page_Home_Page_Activity.class);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                Toast.makeText(Page_Login_Activity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch (ApiException e) {
                    e.printStackTrace();
                }
            }

        }
    });

    // Check to see if the user is logged in or not, but the user must be verified
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        setContentView(R.layout.login_page);

        user_info = getSharedPreferences("UserInformation", MODE_PRIVATE);

        // Store the data into the file called UserSettings
        // MODE_PRIVATE means only this app can access this file
        // getSharedPreferences() means opening the UserSettings file,
        // or automatically creating the UserSettings file (only if it does not exist)
        user_settings = getSharedPreferences("UserSettings", MODE_PRIVATE);

        // Retrieve data from UserSettings file (shared preference)
        // The second parameter is a default value, in case the "isNight" key does not exist, or something went wrong.
        boolean isNight = user_settings.getBoolean("isNight", false);

        // After getting data from UserSettings file, activate the theme accordingly
        activateTheme(isNight);

        tv_dontHaveAnAccount = findViewById(R.id.tv_dontHaveAnAccount);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        et_user_email = findViewById(R.id.et_user_email);
        et_user_password = findViewById(R.id.et_user_password);
        btn_login = findViewById(R.id.btn_login);
        progressbar = findViewById(R.id.progressbar);
        SignInButton btn_google = findViewById(R.id.btn_google);

        // Initialize Firebase Database and Storage
        database = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/");

        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Page_Login_Activity.this, options);

        // User clicks the "Forgot Password" text
        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Login_Activity.this, Page_Reset_Password_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // User clicks the "Don't have an account?" text
        tv_dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page_Login_Activity.this, Page_Register_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // User clicks the Login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_user_email.getText().toString();
                String password = et_user_password.getText().toString();

                progressbar.setVisibility(View.VISIBLE);

                if(email.isEmpty() || password.isEmpty()){
                    Snackbar.make(findViewById(R.id.main), "Please provide both email and password.", Snackbar.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                }
                else if(email.equals("admin") && password.equals("admin")){
                    Intent intent = new Intent(Page_Login_Activity.this, Page_Admin_Activity.class);
                    startActivity(intent);
                    finish();
                }

                // If the user did not provide a valid email address format
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Snackbar.make(findViewById(R.id.main), "Provide a valid email address", Snackbar.LENGTH_LONG).show();
                    progressbar.setVisibility(View.GONE);
                }
                else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressbar.setVisibility(View.GONE);

                                    // If login is successful
                                    if (task.isSuccessful()) {
                                        checkIfEmailVerified(); // Check email verification before the user can log in
                                    }

                                    // If login fails
                                    else {
                                        try{
                                            throw task.getException();
                                        }
                                        catch (Exception e) {
                                            Snackbar.make(findViewById(R.id.main), "Login failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }

                                    }
                                }
                            });
                }

            }
        });

        // User clicks the Google button
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Check if this user info is available in Realtime Database
    private void isAccountExistInRealTimeDB(String user_email, Interface_Account_Exists callback) {
        DatabaseReference database_reference = FirebaseDatabase.getInstance("https://malaysia-tourism-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("User");

        database_reference.orderByChild("email").equalTo(user_email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User exists
                            Toast.makeText(Page_Login_Activity.this, "User exists in Realtime Database", Toast.LENGTH_SHORT).show();
                            callback.onResult(true);  // Call the callback with true
                        }
                        else {
                            // User does not exist
                            Toast.makeText(Page_Login_Activity.this, "User does not exist in the Realtime Database.", Toast.LENGTH_SHORT).show();
                            callback.onResult(false);  // Call the callback with false
                        }
                    }

                    // Error occurred when checking if user data exists in Realtime Database
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors
                        Snackbar.make(findViewById(R.id.main), "Error occurred when checking if user exists in Realtime Database", Snackbar.LENGTH_LONG).show();
                        callback.onResult(false);  // Call the callback with false
                    }
                });
    }

    // Check if the user has verified their email after they register
    private void checkIfEmailVerified(){

        // Check if new users have verified their account or not after registering
        if(mAuth.getCurrentUser().isEmailVerified()){

            isAccountExistInRealTimeDB(mAuth.getCurrentUser().getEmail(), new Interface_Account_Exists() {
                @Override
                public void onResult(boolean exists) {
                    if (exists) {
                        // User exists
                        // Toast.makeText(Page_Login_Activity.this, "Account exists in Realtime Database", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // User does not exist
                        Toast.makeText(Page_Login_Activity.this, "Account does not exists in Realtime Database. Creating it now.", Toast.LENGTH_SHORT).show();
                        uploadUserData(new User(mAuth.getCurrentUser().getEmail(), "null"));
                    }
                }
            });

            setUserSharedPreference(mAuth.getCurrentUser().getEmail());

            Intent intent = new Intent(Page_Login_Activity.this, Page_Home_Page_Activity.class);
            startActivity(intent);
            finish();

        }
        // If new users have not verified yet
        else{
            Snackbar.make(findViewById(R.id.main), "Please verify your email first.", Snackbar.LENGTH_LONG).show();
        }

    }

    // Make the app remember the user's email throughout the whole app
    private void setUserSharedPreference(String user_email) {
        SharedPreferences.Editor user_info_editor = user_info.edit();

        // Store the value of user_email to the "email" key in the UserInfo file
        user_info_editor.putString("email", user_email);
        user_info_editor.apply();
    }

    // Store user data to Realtime Database
    private void uploadUserData(User new_user) {

        // Reference to "TouristSpot" in the database. TouristSpot is a collection name
        DatabaseReference database_reference = database.getReference("User");

        // Generate a unique ID for the user
        String user_id = database_reference.push().getKey();

        // Store the user data as a document in the TouristSpot collection
        database_reference.child(user_id).setValue(new_user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Page_Login_Activity.this, "User data uploaded successfully.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.main), "Failed to upload data: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    // Activate light mode or dark mode
    private void activateTheme(boolean isNight) {
        if(isNight){
            // Activate Night Mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            // Activate Dark Mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }

}