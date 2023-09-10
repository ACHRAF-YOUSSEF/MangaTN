package com.example.mangatn.interfaces.auth;

import android.content.Context;

import com.example.mangatn.models.ApiResponse;

public interface OnUpdateUserListener {
    void onSuccess(ApiResponse response, String  message, Context context);
    void onError(String message, Context context);
}
