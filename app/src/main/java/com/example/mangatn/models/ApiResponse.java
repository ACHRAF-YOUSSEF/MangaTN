package com.example.mangatn.models;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    private String message;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }
}
