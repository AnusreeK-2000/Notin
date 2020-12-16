package com.example.notin.entities;

public class UploadVideoDetails {
    public String subject;
    public String name;
    public String url;
    public String author;

    public UploadVideoDetails() {
    }

    public UploadVideoDetails(String subject, String name, String url, String author) {
        this.subject = subject;
        this.name = name;
        this.url = url;
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
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
}
