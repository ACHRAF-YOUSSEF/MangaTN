package com.example.mangatn.interfaces;

import android.content.Context;

public interface OnSignupListener {
    void onSignupSuccess(String message, Context context);
    void onSignupError(String error, Context context);
}

