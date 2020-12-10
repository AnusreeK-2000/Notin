package com.example.notin.entities;

public class Member {
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Member() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    private String Name;
    private String email;
    private String department;
    private String semester;
}
