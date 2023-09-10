package com.example.mangatn.interfaces.manga;

import android.content.Context;

import com.example.mangatn.models.manga.MangaModel;

import java.util.List;

public interface OnFetchDataListener {
    void onFetchData(List<MangaModel> list, String  message, Context context);
    void onError(String message, Context context);
}
