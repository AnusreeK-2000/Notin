package com.example.notin.Student;

public class UploadPDFDetails {
    public String subject;
    public String name;
    public String url;
    public String author;

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

    public UploadPDFDetails(String name, String url, String spinner, String author) {
        this.name = name;
        this.url = url;
        this.subject=spinner;
        this.author=author;
    }

    public String getName() {
        return name;
    }


    public String getUrl() {
        return url;
    }

    public String getSubject() {
        return subject;
    }
}
