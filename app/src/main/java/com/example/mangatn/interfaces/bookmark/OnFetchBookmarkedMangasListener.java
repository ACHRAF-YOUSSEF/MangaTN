package com.example.mangatn.interfaces.bookmark;

import android.content.Context;

import com.example.mangatn.models.manga.MangaModel;

import java.util.List;

public interface OnFetchBookmarkedMangasListener {
    void onFetchSuccess(List<MangaModel> bookmarkedMangas, String message, Context context);
    void onFetchError(String error, Context context);
}
