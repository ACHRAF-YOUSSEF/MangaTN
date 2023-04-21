package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.MangaModel;

import java.util.List;

public interface OnFetchSingleDataListener {
    void onFetchData(MangaModel manga, String  message, Context context);
    void onError(String message, Context context);
}
