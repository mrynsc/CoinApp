package com.pow.mining.model;


public class Config {

    private String appVersionName;

    public Config() {

    }
    public Config(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionName() {
        return appVersionName;
    }
}
