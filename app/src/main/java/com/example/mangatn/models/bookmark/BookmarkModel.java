package com.example.mangatn.models.bookmark;

public class BookmarkModel {
    private String mangaId;
    private Boolean isBookmarked;

    public BookmarkModel(String mangaId, Boolean isBookmarked) {
        this.mangaId = mangaId;
        this.isBookmarked = isBookmarked;
    }

    public BookmarkModel() {}

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
        return "BookmarkModel{" +
                "mangaId='" + mangaId + '\'' +
                ", isBookmarked=" + isBookmarked +
                '}';
    }
}
