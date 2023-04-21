package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.MangaModel;

import java.util.List;

public interface OnFetchDataListener<MangaApiResponse> {
    void onFetchData(List<MangaModel> list, String  message, Context context);
    void onError(String message, Context context);
}
