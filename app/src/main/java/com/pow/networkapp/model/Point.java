package com.pow.networkapp.model;

public class Point {

    private int invitePoint;
    private int counterPoint;

    public Point(){

    }

    public Point(int invitePoint, int counterPoint) {
        this.invitePoint = invitePoint;
        this.counterPoint = counterPoint;
    }

    public int getInvitePoint() {
        return invitePoint;
    }

    public int getCounterPoint() {
        return counterPoint;
    }
}
