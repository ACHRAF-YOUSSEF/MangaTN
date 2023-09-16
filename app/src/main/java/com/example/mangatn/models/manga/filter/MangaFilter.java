package com.example.mangatn.models.manga.filter;

import com.example.mangatn.models.Enum.EMangaGenre;
import com.example.mangatn.models.Enum.EMangaStatus;

import java.util.List;

public class MangaFilter {
    private String query;
    private List<EMangaStatus> statuses;
    private List<EMangaGenre> genres;

    public MangaFilter(String query, List<EMangaStatus> statuses, List<EMangaGenre> genres) {
        this.query = query;
        this.statuses = statuses;
        this.genres = genres;
    }

    public MangaFilter() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<EMangaStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<EMangaStatus> statuses) {
        this.statuses = statuses;
    }

    public List<EMangaGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<EMangaGenre> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "MangaFilter{" +
                "query='" + query + '\'' +
                ", statuses=" + statuses +
                ", genres=" + genres +
                '}';
    }
}
