package com.example.notin.Student;

public class UploadPDFDetails {
    public String subject;
    public String name;
    public String url;
    public String author;
    public String dept;

    public UploadPDFDetails() {
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public UploadPDFDetails(String name, String url, String spinner, String author,String dept) {
        this.name = name;
        this.url = url;
        this.subject=spinner;
        this.author=author;
        this.dept=dept;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getUrl() {
        return url;
    }

    public String getSubject() {
        return subject;
    }
}
