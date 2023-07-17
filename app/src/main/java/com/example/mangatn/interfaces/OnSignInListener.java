package com.example.mangatn.interfaces;

import android.content.Context;

public interface OnSignInListener {
    void onSignInSuccess(String message, Context context);
    void onSignInError(String error, Context context);
}
