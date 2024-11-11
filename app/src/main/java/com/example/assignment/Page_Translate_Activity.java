package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Page_Translate_Activity extends AppCompatActivity {

    // Drawer Menu
    private DrawerLayout dl_drawer_layout;
    private NavigationView nv_main_menu;
    private ActionBarDrawerToggle drawer_toggle;
    private Toolbar toolbar;
    private View header_view;

    // For logout option in the drawer menu
    private GoogleSignInClient google_sign_in_client;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private PowerSpinnerView sp_source_language, sp_target_language;
    private TextInputEditText et_text_to_be_translated;
    private FloatingActionButton btn_mic;
    private MaterialButton btn_translate_text;
    private TextView tv_translated_text;
    private CircularProgressIndicator progress_bar;

    private static final int REQUEST_PERMISSION_CODE = 1;
    int language_code, source_language_code, target_language_code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_translate_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        sp_source_language = findViewById(R.id.sp_source_language);
        sp_target_language = findViewById(R.id.sp_target_language);
        et_text_to_be_translated = findViewById(R.id.et_text_to_be_translated);
        btn_mic = findViewById(R.id.btn_mic);
        btn_translate_text = findViewById(R.id.btn_translate_text);
        tv_translated_text = findViewById(R.id.tv_translated_text);
        progress_bar = findViewById(R.id.progress_bar);

        // Drawer Menu
        dl_drawer_layout = findViewById(R.id.dl_drawer_layout);
        nv_main_menu = findViewById(R.id.nv_main_menu);
        toolbar = findViewById(R.id.toolbar);
        header_view = nv_main_menu.getHeaderView(0);

        setSupportActionBar(toolbar);

        // Drawer menu - Initialize ActionBarDrawerToggle with custom icon
        drawer_toggle = new ActionBarDrawerToggle(this, dl_drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(@NonNull android.view.View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // Change the icon based on the drawer state
                if (dl_drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    // getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);
                }
                else {
                    // getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_open_24);
                }
            }
        };

        dl_drawer_layout.addDrawerListener(drawer_toggle);
        drawer_toggle.syncState();

        // Drawer menu - Enable the hamburger icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv_main_menu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Close the drawer menu after the user click one of the options
                dl_drawer_layout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();

                if (id == R.id.menu_home) {
                    Sidebar_Menu_Functions.open_home_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_image_recognition) {
                    Sidebar_Menu_Functions.open_image_recognition_page(Page_Translate_Activity.this);
                }

                else if(id == R.id.menu_currency){
                    Sidebar_Menu_Functions.open_currency_converter_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_todo) {
                    Sidebar_Menu_Functions.open_todo_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_translate) {
                    Sidebar_Menu_Functions.open_translate_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_view_tourist_spot_on_map) {
                    Sidebar_Menu_Functions.open_tourist_spot_map_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_weather) {
                    Sidebar_Menu_Functions.open_weather_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_random_photos) {
                    Sidebar_Menu_Functions.open_random_photos_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_settings) {
                    Sidebar_Menu_Functions.open_settings_page(Page_Translate_Activity.this);
                }

                else if (id == R.id.menu_logout) {
                    Sidebar_Menu_Functions.logout(Page_Translate_Activity.this, google_sign_in_client);
                }

                else{
                    Toast.makeText(Page_Translate_Activity.this, "Invalid option!", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        // Use the OnBackPressedDispatcher to handle the back button event
        // It means what happens when the user click the back button on their phone
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                // If the main menu is opened, and then the user click the back button,
                // then close the main menu
                if (dl_drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    dl_drawer_layout.closeDrawer(GravityCompat.START);
                }
                else{
                    // If the drawer is closed, proceed with the default back action
                    finish(); // Terminate this current activity, and go back to the previous activity

                }
            }
        };

        // Add the callback to the back press dispatcher
        // This is just like callback functions in JavaScript. It is listening for the back button to be clicked
        // When the back button is clicked, the function named callback, is executed.
        getOnBackPressedDispatcher().addCallback(Page_Translate_Activity.this, callback);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.client_id)) // Ensure you have the correct client ID
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        google_sign_in_client = GoogleSignIn.getClient(this, gso);

        // Create all the spinner items. Each item will have a text and image (flag)
        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
        iconSpinnerItems.add(new IconSpinnerItem("Arabic"));
        iconSpinnerItems.add(new IconSpinnerItem("Belarusian"));
        iconSpinnerItems.add(new IconSpinnerItem("Bengali"));
        iconSpinnerItems.add(new IconSpinnerItem("Bulgarian"));
        iconSpinnerItems.add(new IconSpinnerItem("Catalan"));
        iconSpinnerItems.add(new IconSpinnerItem("Czech"));
        iconSpinnerItems.add(new IconSpinnerItem("English"));
        iconSpinnerItems.add(new IconSpinnerItem("Hindi"));
        iconSpinnerItems.add(new IconSpinnerItem("Korean"));
        iconSpinnerItems.add(new IconSpinnerItem("Malay"));
        iconSpinnerItems.add(new IconSpinnerItem("Welsh"));

        // Put all those items into FROM spinner
        IconSpinnerAdapter iconSpinnerAdapter_from = new IconSpinnerAdapter(sp_source_language);
        sp_source_language.setSpinnerAdapter(iconSpinnerAdapter_from);
        sp_source_language.setItems(iconSpinnerItems);
        sp_source_language.selectItemByIndex(0);
        sp_source_language.setLifecycleOwner(this);

        // Put all those items into TO spinner
        IconSpinnerAdapter iconSpinnerAdapter_to = new IconSpinnerAdapter(sp_target_language);
        sp_target_language.setSpinnerAdapter(iconSpinnerAdapter_to);
        sp_target_language.setItems(iconSpinnerItems);
        sp_target_language.selectItemByIndex(4);
        sp_target_language.setLifecycleOwner(this);

        // User clicks on the mic icon
        btn_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");

                try {
                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Page_Translate_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // User clicks the Translate button
        btn_translate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv_translated_text.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);

                String text_to_be_translated = et_text_to_be_translated.getText().toString();
                String source_language = sp_source_language.getText().toString().toLowerCase();
                String target_language = sp_target_language.getText().toString().toLowerCase();

                if(text_to_be_translated.isEmpty()){
                    Snackbar
                            .make(findViewById(R.id.dl_drawer_layout), "Please enter the text you want to translate.", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(source_language.equals(target_language)){
                    Snackbar
                            .make(findViewById(R.id.dl_drawer_layout), "Source language and target language cannot be the same. Please select different languages.", Snackbar.LENGTH_LONG)
                            .show();
                }
                else{
                    source_language_code = getLanguageCode(source_language);
                    target_language_code = getLanguageCode(target_language);

                    if(source_language_code == -1 || target_language_code == -1){
                        Snackbar
                                .make(findViewById(R.id.dl_drawer_layout), "Error getting language code.", Snackbar.LENGTH_LONG)
                                .show();
                    }
                    else{
                        translateText(source_language_code, target_language_code, text_to_be_translated);
                        progress_bar.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

    }

    // This onActivityResult() is used to handle the result after another screen or feature in your app has finished doing something (like voice capturing or file picker or etc).
    // We are only interested in voice capturing
    // This onActivityResult() gets called when another activity (like voice capturing or a file picker) finishes and sends back information to your main activity (Page_Translate_Activity Activity).
    // To understand this method better, visit this link: https://stackoverflow.com/questions/9268153/what-is-the-meaning-of-requestcode-in-startactivityforresult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the voice capture activity
        // REQUEST_PERMISSION_CODE is just a variable we created by the way. We only care about the value it stores.
        // REQUEST_PERMISSION CODE can be any integer value btw. We simply say, if it 1, then it is talking about the voice capturing. If 2, we can say it is talking about file picking.
        // We can also say, if it is 1, it is talking about file picking, if 2, then voice capturing. etc.
        // requestCode is a parameter, obtained from startActivityForResult() method (This method is located at btn_mic click listener)
        // REQUEST_PERMISSION_CODE is a global variable. We passed the value of this variable from the startActivityForResult() method to startActivityForResult() method. It is the requestCode parameter.
        if(requestCode == REQUEST_PERMISSION_CODE){

            // Check if the app manage to capture the user's voice
            if(resultCode == RESULT_OK && data != null){
                // This line of code extracts the spoken text (from voice recognition) and puts it into a list called result.
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                // result.get(0) takes the first item (spoken text) from the list and displays it in a text field.
                et_text_to_be_translated.setText(result.get(0));
            }
        }

    }

    public int getLanguageCode(String language){

        // Do note, if you return language_code as 0, you are returning an african language
        int language_code = 0;

        switch (language){
            case "english":
                language_code = FirebaseTranslateLanguage.EN;
                break;
            case "korean":
                language_code = FirebaseTranslateLanguage.KO;
                break;
            case "arabic":
                language_code = FirebaseTranslateLanguage.AR;
                break;
            case "belarusian":
                language_code = FirebaseTranslateLanguage.BE;
                break;
            case "bulgarian":
                language_code = FirebaseTranslateLanguage.BG;
                break;
            case "bengali":
                language_code = FirebaseTranslateLanguage.BN;
                break;
            case "catalan":
                language_code = FirebaseTranslateLanguage.CA;
                break;
            case "czech":
                language_code = FirebaseTranslateLanguage.CS;
                break;
            case "malay":
                language_code = FirebaseTranslateLanguage.MS;
                break;
            case "welsh":
                language_code = FirebaseTranslateLanguage.CY;
                break;
            case "hindi":
                language_code = FirebaseTranslateLanguage.HI;
                break;
            case "urdu":
                language_code = FirebaseTranslateLanguage.UR;
                break;
            default:
                language_code = -1;
        }

        return language_code;
    }

    public void translateText(int from_language_code, int to_language_code, String source_text){

        Toast.makeText(this, "Downloading Modal...", Toast.LENGTH_SHORT).show();
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(from_language_code)
                .setTargetLanguage(to_language_code)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        translator.translate(source_text).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String translated_text) {
                                // Steps to turn the progress bar from spinning indefinite to definite, after getting the translated data successfully
                                // Step 1: Switch to determinate mode
                                progress_bar.setIndeterminate(false);

                                // Step 2: Start at 0% progress
                                progress_bar.setProgressCompat(0, false);

                                // Step 3: Animate progress to 100% over a period (e.g., 2 seconds)
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Step 4: Animate to 100% (in 2000 milliseconds)
                                        progress_bar.setProgressCompat(100, true);

                                        // Step 5: Continue with the rest of the code after animation
                                        progress_bar.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // This runs after the progress reaches 100%
                                                tv_translated_text.setVisibility(View.VISIBLE);
                                                tv_translated_text.setText(translated_text);
                                                progress_bar.setProgressCompat(0, false);
                                                progress_bar.setVisibility(View.GONE);
                                                progress_bar.setIndeterminate(true);
                                            }
                                        }, 2000); // The duration of the animation (2 seconds in this case)
                                    }
                                }, 0);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar
                                        .make(findViewById(R.id.dl_drawer_layout), "Fail to translate: " + e.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                                progress_bar.setVisibility(View.GONE);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar
                                .make(findViewById(R.id.dl_drawer_layout), "Fail to download language Modal : " + e.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

}