package com.example.mangatn.interfaces.auth;

import android.content.Context;

public interface OnSignInListener {
    void onSignInSuccess(String message, Context context);
    void onSignInError(String error, Context context);
}
