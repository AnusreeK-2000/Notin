package com.example.notin.entities;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RecentNotes {
    private String name;
    private int sem;
    private String subject;

    public RecentNotes() {
    }

    public RecentNotes(String name, int sem, String subject) {
        this.name = name;
        this.sem = sem;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

