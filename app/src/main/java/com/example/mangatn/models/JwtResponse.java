package com.example.mangatn.models;

public class JwtResponse {
    private String token;
    private String userName;
    private String email;
    private String roles;

    public JwtResponse(String token, String userName, String email, String roles) {
        this.token = token;
        this.userName = userName;
        this.email = email;
        this.roles = roles;
    }

    public JwtResponse() {}

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
