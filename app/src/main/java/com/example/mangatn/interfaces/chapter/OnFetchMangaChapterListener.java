package com.example.mangatn.interfaces.chapter;

import android.content.Context;

import com.example.mangatn.models.chapter.ChapterModel;

public interface OnFetchMangaChapterListener {
    void onFetchData(ChapterModel response, String  message, Context context);
    void onError(String message, Context context);
}
