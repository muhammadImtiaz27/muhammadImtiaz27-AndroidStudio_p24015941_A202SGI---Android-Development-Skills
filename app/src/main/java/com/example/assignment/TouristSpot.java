package com.example.assignment;

import java.io.Serializable;
import java.util.List;

public class TouristSpot implements Serializable {

    public String name;
    public String description;
    public String state;
    public String category;
    public String address;
    public String coordinates;
    public TouristSpot_Uri tourist_spot_uri;

    // Constructors
    public TouristSpot() {
        this.name = "";
        this.description = "";
        this.state = "";
        this.category = "";
        this.address = "";
        this.tourist_spot_uri = new TouristSpot_Uri();
    }

    public TouristSpot(String name, String description, String state, String category, String address, String coordinates, TouristSpot_Uri tourist_spot_uri) {
        this.name = name;
        this.description = description;
        this.state = state;
        this.category = category;
        this.address = address;
        this.coordinates = coordinates;
        this.tourist_spot_uri = tourist_spot_uri;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public String getAddress() {
        return address;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getCategory() {
        return category;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCoordinates(String coordinates){
        this.coordinates = coordinates;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setThumbnail_uri(String thumbnail_uri) {
        if (this.tourist_spot_uri == null) {
            this.tourist_spot_uri = new TouristSpot_Uri();
        }

        this.tourist_spot_uri.thumbnail_uri = thumbnail_uri;
    }

    public void setList_of_images_uri(List<String> list_of_images_uri) {
        this.tourist_spot_uri.list_of_images_uri = list_of_images_uri;
    }

    public void setVideo_uri(String video_uri) {
        if (this.tourist_spot_uri == null) {
            this.tourist_spot_uri = new TouristSpot_Uri();
        }

        this.tourist_spot_uri.video_uri = video_uri;
    }

}
