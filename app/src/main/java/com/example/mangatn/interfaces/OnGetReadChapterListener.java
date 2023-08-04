package com.example.mangatn.interfaces;

import android.content.Context;
import com.example.mangatn.models.ReadChapterModel;

public interface OnGetReadChapterListener {
    void onFetchData(ReadChapterModel response, String  message, Context context);
    void onError(String message, Context context);
}
