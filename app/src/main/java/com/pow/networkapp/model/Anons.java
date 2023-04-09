package com.pow.networkapp.model;


public class Anons {

    private String title;
    private String description;
    private String time;
    private int status;

    public Anons() {

    }

    public Anons(String title, String description, String time, int status) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}
