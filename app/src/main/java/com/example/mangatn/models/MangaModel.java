package com.example.mangatn.models;

public class MangaModel {
    private int id;
    private static int count;
    private String title;
    private String chapters;
    private String imgPath;
    private String mangaId;

    public MangaModel(String title, String chapters, String imgPath, String mangaId) {
        id = ++count;

        this.title = title;
        this.chapters = chapters;
        this.imgPath = imgPath;
        this.mangaId = mangaId;
    }

    public static void setCount(int count) {
        MangaModel.count = count;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapters() {
        return chapters;
    }

    public void setChapters(String chapters) {
        this.chapters = chapters;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
