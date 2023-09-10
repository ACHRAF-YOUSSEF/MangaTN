package com.example.mangatn.models.manga;

import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.chapter.ChapterModel;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class MangaModel implements Serializable {
    private String title;
    private List<ChapterModel> chapters;
    private String mangaId;
    private String coverImgPath;
    private String summary;
    private String authors;
    private EMangaStatus status;
    private Set<String> genres;
    private int count;
    private Boolean upToDate;

    public MangaModel(String title, List<ChapterModel> chapters, String mangaId, String coverImgPath, String summary, String authors, EMangaStatus status, Set<String> genres, int count, Boolean upToDate) {
        this.title = title;
        this.chapters = chapters;
        this.mangaId = mangaId;
        this.coverImgPath = coverImgPath;
        this.summary = summary;
        this.authors = authors;
        this.status = status;
        this.genres = genres;
        this.count = count;
        this.upToDate = upToDate;
    }

    public MangaModel() {}

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

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

    public EMangaStatus getStatus() {
        return status;
    }

    public void setStatus(EMangaStatus status) {
        this.status = status;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }
}
