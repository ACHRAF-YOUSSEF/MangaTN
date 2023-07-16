package com.example.mangatn.models;

public class Bookmark {
    private String mangaId;
    private Boolean isBookmarked;

    public Bookmark(String mangaId, Boolean isBookmarked) {
        this.mangaId = mangaId;
        this.isBookmarked = isBookmarked;
    }

    public Bookmark() {}

    public String getMangaId() {
        return mangaId;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "mangaId='" + mangaId + '\'' +
                ", isBookmarked=" + isBookmarked +
                '}';
    }
}
