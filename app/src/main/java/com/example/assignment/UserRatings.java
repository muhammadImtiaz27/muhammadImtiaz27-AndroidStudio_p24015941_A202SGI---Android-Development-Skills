package com.example.assignment;

import java.io.Serializable;

public class UserRatings implements Serializable {

    public String user_email;
    public String tour_name;
    public int num_of_stars;
    public String description;
    public String photo_uri;

    public UserRatings() {
        this.user_email = "";
        this.tour_name = "";
        this.description = "";
        this.num_of_stars = 0;
        this.photo_uri = "";
    }

    public UserRatings(String user_email, String tour_name, String description, int num_of_stars, String photo_uri){
        this.user_email = user_email;
        this.tour_name = tour_name;
        this.description = description;
        this.num_of_stars = num_of_stars;
        this.photo_uri = photo_uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNum_of_stars(int num_of_stars) {
        this.num_of_stars = num_of_stars;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public void setTour_name(String tour_name) {
        this.tour_name = tour_name;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getNum_of_stars() {
        return num_of_stars;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public String getTour_name() {
        return tour_name;
    }

    public String getUser_email() {
        return user_email;
    }


}
