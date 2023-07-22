package com.example.mangatn.models;

import android.content.Intent;

import java.io.Serializable;
import java.util.List;

public class ChapterModel implements Serializable {
    private String title;
    private Integer reference;
    private List<String> imgPaths;

    public ChapterModel(String title, List<String> imgPaths, Integer reference) {
        this.title = title;
        this.imgPaths = imgPaths;
        this.reference =reference;
    }

    public ChapterModel() {}

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
