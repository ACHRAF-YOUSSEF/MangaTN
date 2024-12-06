package com.example.mangatn.models.manga;

import com.example.mangatn.models.pagination.PaginatedContentDto;
import com.example.mangatn.models.pagination.PaginationDto;

import java.io.Serializable;
import java.util.List;

public class MangaListApiResponse extends PaginatedContentDto<MangaModel> implements Serializable {
    public MangaListApiResponse(PaginationDto pagination) {
        super(pagination);
    }

    public MangaListApiResponse(long count, List<MangaModel> content, PaginationDto pagination) {
        super(count, content, pagination);
    }
}
