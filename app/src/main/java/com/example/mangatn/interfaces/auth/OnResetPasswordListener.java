package com.example.mangatn.interfaces.auth;

import android.content.Context;

public interface OnResetPasswordListener<ApiResponse> {
    void onFetchData(ApiResponse response, String  message, Context context);
    void onError(String message, Context context);
}