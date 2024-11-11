package com.example.assignment;

public class Unsplash_Random_Photo {

    public String description;
    public String alt_description;
    public String url;

    public Unsplash_Random_Photo(String description, String alt_description, String url) {
        this.description = description;
        this.alt_description = alt_description;
        this.url = url;
    }

    public String getAlt_description() {
        return alt_description;
    }

    public void setAlt_description(String alt_description) {
        this.alt_description = alt_description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
