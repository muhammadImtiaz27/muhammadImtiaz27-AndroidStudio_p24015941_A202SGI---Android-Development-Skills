plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.assignment"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.assignment"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    // implementation(libs.material)
    implementation(libs.material.v190)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // For Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-ml-natural-language:22.0.1")
    implementation("com.google.firebase:firebase-ml-natural-language-language-id-model:20.0.8")
    implementation("com.google.firebase:firebase-ml-natural-language-translate-model:20.0.9")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // For image loading
    implementation("com.squareup.picasso:picasso:2.71828")

    // For JSON parsing
    implementation(libs.volley)

    // For getting user's current location
    implementation("com.google.android.gms:play-services-location:17.0.0")

    // For spinner
    implementation("com.github.skydoves:powerspinner:1.2.7")

    // For Picasso
    implementation("com.squareup.picasso:picasso:2.8")

    // Shimmer Effect
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Exo Player (For video purposes)
    implementation("com.google.android.exoplayer:exoplayer:2.18.7")

    // For reCAPTCHA
    implementation("com.google.android.recaptcha:recaptcha:18.7.0-beta01");

}