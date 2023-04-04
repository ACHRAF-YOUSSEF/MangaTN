package com.example.mangatn.models;

import java.io.Serializable;

public class ChapterModel implements Serializable {
    private int id;
    private static int count;
    private String chapter;
    private String nb;
    private MangaModel mangaModel;

    public ChapterModel(String chapter, String nb, MangaModel mangaModel) {
        id = ++count;

        this.mangaModel =  mangaModel;
        this.chapter = chapter;
        this.nb = nb;
    }

    public ChapterModel(String chapter, String nb) {
        id = ++count;

        this.chapter = chapter;
        this.nb = nb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        ChapterModel.count = count;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public void setNb(String nb) {
        this.nb = nb;
    }

    public String getChapter() {
        return this.chapter;
    }

    public String getNb() {
        return this.nb;
    }

    public MangaModel getMangaModel() {
        return mangaModel;
    }

    public void setMangaModel(MangaModel mangaModel) {
        this.mangaModel = mangaModel;
    }
}
