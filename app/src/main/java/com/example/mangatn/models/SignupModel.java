package com.example.mangatn.models;

public class SignupModel {
    private String username;
    private String email;
    private String password;

    public SignupModel(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public SignupModel() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
