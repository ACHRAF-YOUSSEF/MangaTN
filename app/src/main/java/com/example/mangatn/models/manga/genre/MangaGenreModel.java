package com.example.mangatn.models.manga.genre;

public class MangaGenreModel {
    private String name;

    public MangaGenreModel(String name) {
        this.name = name;
    }

    public MangaGenreModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MangaGenreModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
