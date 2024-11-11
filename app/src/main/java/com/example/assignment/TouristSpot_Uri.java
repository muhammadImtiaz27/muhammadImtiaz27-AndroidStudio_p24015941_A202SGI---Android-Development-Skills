package com.example.assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TouristSpot_Uri implements Serializable {

    public String thumbnail_uri;
    public List<String> list_of_images_uri;
    public String video_uri;

    // Constructors
    public TouristSpot_Uri(){
        thumbnail_uri = "";
        list_of_images_uri = new ArrayList<>();
        video_uri = "";
    }

    public TouristSpot_Uri(String thumbnail_uri, List<String> list_of_images_uri, String video_uri) {
        this.list_of_images_uri = list_of_images_uri;
        this.thumbnail_uri = thumbnail_uri;
        this.video_uri = video_uri;
    }

    public List<String> getList_of_images_uri() {
        return list_of_images_uri;
    }

    public void setList_of_images_uri(List<String> list_of_images_uri) {
        this.list_of_images_uri = list_of_images_uri;
    }

    public String getThumbnail_uri() {
        return thumbnail_uri;
    }

    public void setThumbnail_uri(String thumbnail_uri) {
        this.thumbnail_uri = thumbnail_uri;
    }

    public String getVideo_uri() {
        return video_uri;
    }

    public void setVideo_uri(String video_uri) {
        this.video_uri = video_uri;
    }

}
