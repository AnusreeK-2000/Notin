package com.example.notin.entities;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Courses {
    private String name;

    public Courses() {
    }

    public Courses(String name) {
        this.name = name;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }
}
