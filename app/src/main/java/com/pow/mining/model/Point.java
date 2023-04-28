package com.pow.mining.model;

public class Point {

    private int invitePoint;
    private int counterPoint;
    private int appVersion;

    public Point() {

    }

    public Point(int invitePoint, int counterPoint, int appVersion) {
        this.invitePoint = invitePoint;
        this.counterPoint = counterPoint;
        this.appVersion = appVersion;
    }

    public int getInvitePoint() {
        return invitePoint;
    }

    public int getCounterPoint() {
        return counterPoint;
    }

    public int getAppVersion() {
        return appVersion;
    }
}
