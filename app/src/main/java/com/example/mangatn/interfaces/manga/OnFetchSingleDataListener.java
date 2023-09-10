package com.example.mangatn.interfaces.manga;

import android.content.Context;

import com.example.mangatn.models.manga.MangaModel;

public interface OnFetchSingleDataListener {
    void onFetchData(MangaModel manga, String  message, Context context);
    void onError(String message, Context context);
}
