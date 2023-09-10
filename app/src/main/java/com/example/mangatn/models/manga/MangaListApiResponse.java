package com.example.mangatn.models.manga;

import java.io.Serializable;
import java.util.List;

public class MangaListApiResponse implements Serializable {
    private long count;
    private List<MangaModel> mangas;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "count=" + count +
                ", mangas=" + mangas +
                '}';
    }

    public long getCount() {
        return count;
    }

    public List<MangaModel> getMangas() {
        return mangas;
    }
}
