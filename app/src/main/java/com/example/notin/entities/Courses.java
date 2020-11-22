package com.example.notin.entities;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Courses {
    private String name;
    private int sem;
    private String dept;

    public Courses() {
    }

    public Courses(String name, int sem, String dept) {
        this.name = name;
        this.sem = sem;
        this.dept = dept;
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

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
