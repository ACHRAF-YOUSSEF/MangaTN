package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.ChaptersListApiResponse;

public interface OnFetchMangaChaptersListListener {
    void onFetchData(ChaptersListApiResponse response, String  message, Context context);
    void onError(String message, Context context);
}
