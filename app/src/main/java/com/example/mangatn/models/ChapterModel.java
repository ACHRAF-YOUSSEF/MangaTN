package com.example.mangatn.models;

import java.io.Serializable;
import java.util.List;

public class ChapterModel implements Serializable {
    private String title;
    private List<String> imgPaths;

    public ChapterModel(String title, List<String> imgPaths) {
        this.title = title;
        this.imgPaths = imgPaths;
    }

    public ChapterModel() {}

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
                ", imgPaths=" + imgPaths +
                '}';
    }
}
