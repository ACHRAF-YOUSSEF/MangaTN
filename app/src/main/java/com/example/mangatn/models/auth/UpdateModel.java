package com.example.mangatn.models.auth;

public class UpdateModel {
    private String email;
    private String userName;
    private String password;

    public UpdateModel(String email, String userName, String password) {
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    public UpdateModel() {}

    @Override
    public String toString() {
        return "UpdateModel{" +
                "email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
