package com.example.mangatn.interfaces.manga;

import android.content.Context;

import com.example.mangatn.models.manga.MangaModel;

public interface SelectListener {
    void OnMangaClicked(MangaModel manga, Context context);
}
