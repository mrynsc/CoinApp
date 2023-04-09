package com.pow.networkapp.model;

public class Transaction {

    private String address;
    private String userId;
    private long time;
    private int withdrawal;
    private String id;

    public Transaction() {
    }


    public Transaction(String address, String userId, long time, int withdrawal, String id) {
        this.address = address;
        this.userId = userId;
        this.time = time;
        this.withdrawal = withdrawal;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getUserId() {
        return userId;
    }

    public long getTime() {
        return time;
    }

    public int getWithdrawal() {
        return withdrawal;
    }

    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "address='" + address + '\'' +
                ", userId='" + userId + '\'' +
                ", time=" + time +
                ", withdrawal=" + withdrawal +
                ", id='" + id + '\'' +
                '}';
    }
}
