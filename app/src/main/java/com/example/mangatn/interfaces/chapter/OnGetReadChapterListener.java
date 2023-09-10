package com.example.mangatn.interfaces.chapter;

import android.content.Context;
import com.example.mangatn.models.chapter.ReadChapterModel;

public interface OnGetReadChapterListener {
    void onFetchData(ReadChapterModel response, String  message, Context context);
    void onError(String message, Context context);
}
