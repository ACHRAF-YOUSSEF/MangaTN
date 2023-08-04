package com.example.mangatn.models;

import java.io.Serializable;
import java.util.List;

public class ChapterModel implements Serializable {
    private String title;
    private Integer reference;
    private List<String> imgPaths;
    private boolean completed;
    private boolean inProgress;

    public ChapterModel(String title, List<String> imgPaths, Integer reference, boolean completed, boolean inProgress) {
        this.title = title;
        this.imgPaths = imgPaths;
        this.reference =reference;
        this.completed = completed;
        this.inProgress = inProgress;
    }

    public ChapterModel() {}

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

    public Integer getReference() {
        return reference;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImgPaths() {
        return imgPaths;
    }

    public void setImgPaths(List<String> imgPaths) {
        this.imgPaths = imgPaths;
    }

    @Override
    public String toString() {
        return "ChapterModel{" +
                "title='" + title + '\'' +
                ", reference=" + reference +
                ", imgPaths=" + imgPaths +
                '}';
    }
}
