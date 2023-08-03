package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.ApiResponse;

public interface OnMarkAsViewedOrNotListener {
    void onFetchData(ApiResponse response, String  message, Context context);
    void onError(String message, Context context);
}
