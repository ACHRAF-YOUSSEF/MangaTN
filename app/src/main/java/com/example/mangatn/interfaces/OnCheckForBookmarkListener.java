package com.example.mangatn.interfaces;

import android.content.Context;

public interface OnCheckForBookmarkListener {
    void onFetchData(Boolean response, String  message, Context context);
    void onError(String message, Context context);
}
