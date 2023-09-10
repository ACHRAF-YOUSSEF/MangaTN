package com.example.mangatn.interfaces.manga.genre;

import android.content.Context;

import com.example.mangatn.models.manga.MangaModel;

import java.util.List;

public interface OnFetchAllGenreListener {
    void onFetchData(List<String> list, String  message, Context context);
    void onError(String message, Context context);
}
