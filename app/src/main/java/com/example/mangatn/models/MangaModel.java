package com.example.mangatn.models;

import java.io.Serializable;
import java.util.List;

public class MangaModel implements Serializable {
    private String title;
    private List<ChapterModel> chapters;
    private String mangaId;
    private String coverImgPath;
    private int count;
    private Boolean upToDate;

    public MangaModel(String title, List<ChapterModel> chapters, String mangaId, String coverImgPath, int count, Boolean upToDate) {
        this.title = title;
        this.chapters = chapters;
        this.mangaId = mangaId;
        this.coverImgPath = coverImgPath;
        this.count = count;
        this.upToDate = upToDate;
    }

    public MangaModel() {}

    public Boolean getUpToDate() {
        return upToDate;
    }

    public void setUpToDate(Boolean upToDate) {
        this.upToDate = upToDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChapterModel> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterModel> chapters) {
        this.chapters = chapters;
    }

    public String getMangaId() {
        return mangaId;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public String getCoverImgPath() {
        return coverImgPath;
    }

    public void setCoverImgPath(String coverImgPath) {
        this.coverImgPath = coverImgPath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "MangaModel{" +
                "title='" + title + '\'' +
                ", chapters=" + chapters +
                ", mangaId='" + mangaId + '\'' +
                ", coverImgPath='" + coverImgPath + '\'' +
                ", count=" + count +
                '}';
    }
}
