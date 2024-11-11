package com.example.assignment;

public class Weather {

    private String time;
    private String temperature;
    private String icon;
    private String windSpeed;

    public Weather(String icon, String temperature, String time, String windSpeed) {
        this.icon = icon;
        this.temperature = temperature;
        this.time = time;
        this.windSpeed = windSpeed;
    }

    public String getIcon() {
        return icon;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getTime() {
        return time;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
