package com.example.mangatn.interfaces;

import android.content.Context;

public interface OnFetchUpdateListener<ApiResponse> {
    void onFetchData(ApiResponse response, String  message, Context context);
    void onError(String message, Context context);
}