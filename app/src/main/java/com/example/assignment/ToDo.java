package com.example.assignment;

import java.io.Serializable;

public class ToDo implements Serializable {

    private String email;
    private String title;
    private String description;
    private String due_date;
    private String due_time;

    public ToDo(){
        this.email = "";
        this.title = "";
        this.description = "";
        this.due_date = "";
        this.due_time = "";
    }

    public ToDo(String email, String title, String description, String due_date, String due_time){
        this.email = email;
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.due_time = due_time;
    }

    // GETTERS
    public String getEmail(){
        return email;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return due_date;
    }

    public String getDueTime() {
        return due_time;
    }

    // SETTERS
    public void setEmail(String email){
        this.email = email;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String due_date) {
        this.due_date = due_date;
    }

    public void setDueTime(String due_time) {
        this.due_time = due_time;
    }
}
