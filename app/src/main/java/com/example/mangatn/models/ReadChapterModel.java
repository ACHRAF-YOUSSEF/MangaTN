package com.example.mangatn.models;

public class ReadChapterModel {
    private boolean completed;
    private boolean inProgress;
    private int progress;
    private ChapterModel chapter;
    private String mangaId;

    public ReadChapterModel() {}

    public ReadChapterModel(boolean completed, boolean inProgress, int progress, ChapterModel chapter, String mangaId) {
        this.completed = completed;
        this.inProgress = inProgress;
        this.progress = progress;
        this.chapter = chapter;
        this.mangaId = mangaId;
    }

    public ChapterModel getChapter() {
        return chapter;
    }

    public void setChapter(ChapterModel chapter) {
        this.chapter = chapter;
    }

    public String getMangaId() {
        return mangaId;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }
}
