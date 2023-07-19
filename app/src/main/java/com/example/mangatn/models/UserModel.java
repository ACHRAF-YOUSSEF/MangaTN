package com.example.mangatn.models;

public class UserModel {
    private String email;
    private String userName;

    public UserModel(String email, String userName) {
        this.email = email;
        this.userName = userName;
    }

    public UserModel() {}

    @Override
    public String toString() {
        return "UserModel{" +
                "email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
