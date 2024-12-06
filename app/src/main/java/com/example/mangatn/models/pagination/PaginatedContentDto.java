package com.example.mangatn.models.pagination;

import java.util.List;

public class PaginatedContentDto<T> extends ContentDto<T> {
    private final PaginationDto pagination;

    public PaginatedContentDto(PaginationDto pagination) {
        this.pagination = pagination;
    }

    public PaginatedContentDto(long count, List<T> content, PaginationDto pagination) {
        super(count, content);
        this.pagination = pagination;
    }
}
