package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.UserModel;

public interface OnSignInWithTokenListener {
    void onSignInSuccess(UserModel response, String message, Context context);
    void onSignInError(String error, Context context);
}
