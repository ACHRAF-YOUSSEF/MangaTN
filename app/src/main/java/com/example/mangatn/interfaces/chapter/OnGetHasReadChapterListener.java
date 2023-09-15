package com.example.mangatn.interfaces.chapter;

import android.content.Context;

public interface OnGetHasReadChapterListener {
    void onFetchData(Boolean response, String message, Context context);

    void onError(String message, Context context);
}
