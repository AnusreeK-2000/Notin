package com.example.notin.entities;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RecentNotes {
    private String title;

    public RecentNotes() {
    }

    public RecentNotes(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

