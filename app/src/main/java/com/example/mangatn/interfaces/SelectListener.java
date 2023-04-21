package com.example.mangatn.interfaces;

import android.content.Context;

import com.example.mangatn.models.MangaModel;

public interface SelectListener {
    void OnMangaClicked(MangaModel manga, Context context);
}
