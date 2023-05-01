package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.ChapterModel;
import com.example.mangatn.models.MangaModel;

import java.util.List;

public interface OnFetchChaptersDataListener<SingleMangaChaptersApiResponse> {
    void onFetchData(List<ChapterModel> list, String  message, Context context);
    void onError(String message, Context context);
}