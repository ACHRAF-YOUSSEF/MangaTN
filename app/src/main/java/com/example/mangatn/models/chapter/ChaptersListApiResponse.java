package com.example.mangatn.models.chapter;

import com.example.mangatn.models.pagination.ContentDto;

import java.io.Serializable;
import java.util.List;

public class ChaptersListApiResponse extends ContentDto<ChapterModel> implements Serializable {
    public ChaptersListApiResponse() {
    }

    public ChaptersListApiResponse(long count, List<ChapterModel> content) {
        super(count, content);
    }
}
