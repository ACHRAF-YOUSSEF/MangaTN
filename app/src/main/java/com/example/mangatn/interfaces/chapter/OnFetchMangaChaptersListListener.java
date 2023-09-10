package com.example.mangatn.interfaces.chapter;

import android.content.Context;

import com.example.mangatn.models.chapter.ChaptersListApiResponse;

public interface OnFetchMangaChaptersListListener {
    void onFetchData(ChaptersListApiResponse response, String  message, Context context);
    void onError(String message, Context context);
}
