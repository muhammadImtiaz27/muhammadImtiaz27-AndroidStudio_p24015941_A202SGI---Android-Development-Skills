package com.example.assignment;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String email;
    public String uri_profile_picture;

    // Constructor
    public User(){
        this.email = "";
        this.uri_profile_picture = "";
    }

    public User(String email, String uri_profile_picture) {
        this.email = email;
        this.uri_profile_picture = uri_profile_picture;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setUri_profile_picture(String uri_profile_picture) {
        this.uri_profile_picture = uri_profile_picture;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getUri_profile_picture() {
        return uri_profile_picture;
    }
}
