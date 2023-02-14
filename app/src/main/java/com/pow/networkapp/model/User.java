package com.pow.networkapp.model;

public class User {

    private String username;
    private String email;
    private String image;
    private int claimed;
    private int referral;
    private long registerDate;
    private long lastSeen;
    private int accountType;
    private String userId;
    private String referralLink;
    private int balance;

    public User(){

    }

    public User(String username, String email, String image, int claimed, long registerDate, long lastSeen, int accountType, String userId, String referralLink
    ,int referral,int balance) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.claimed = claimed;
        this.registerDate = registerDate;
        this.lastSeen = lastSeen;
        this.accountType = accountType;
        this.userId = userId;
        this.referralLink = referralLink;
        this.referral = referral;
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getReferral() {
        return referral;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public int getClaimed() {
        return claimed;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getUserId() {
        return userId;
    }

    public String getReferralLink() {
        return referralLink;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", claimed=" + claimed +
                ", referral=" + referral +
                ", registerDate=" + registerDate +
                ", lastSeen=" + lastSeen +
                ", accountType=" + accountType +
                ", userId='" + userId + '\'' +
                ", referralLink='" + referralLink + '\'' +
                ", balance=" + balance +
                '}';
    }
}
