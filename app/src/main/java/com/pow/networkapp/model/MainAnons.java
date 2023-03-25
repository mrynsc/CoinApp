package com.pow.networkapp.model;

public class MainAnons {

    private String anons;
    private String id;
    private int status;


    public MainAnons(){

    }

    public MainAnons(String anons, String id,int status) {
        this.anons = anons;
        this.id = id;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getAnons() {
        return anons;
    }

    public String getId() {
        return id;
    }
}
