package com.example.notin.Student;

public class UploadPDFDetails {
    public String subject;
    public String name;
    public String url;

    public UploadPDFDetails() {
    }

    public UploadPDFDetails(String name, String url,String spinner) {
        this.name = name;
        this.url = url;
        this.subject=spinner;
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
