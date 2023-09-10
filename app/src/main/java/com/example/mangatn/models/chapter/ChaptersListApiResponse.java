package com.example.mangatn.models.chapter;

import java.io.Serializable;
import java.util.List;

public class ChaptersListApiResponse implements Serializable {
    private long count;
    private List<ChapterModel> chapters;

    public long getCount() {
        return count;
    }

    public List<ChapterModel> getChapters() {
        return chapters;
    }

    @Override
    public String toString() {
        return "ChaptersListApiResponse{" +
                "count=" + count +
                ", chapters=" + chapters +
                '}';
    }
}
