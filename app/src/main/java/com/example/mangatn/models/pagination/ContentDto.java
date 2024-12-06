package com.example.mangatn.models.pagination;

import java.util.List;

public class ContentDto<T> {
    private long count;
    private List<T> content;

    public ContentDto() {
    }

    @Override
    public String toString() {
        return "ContentDto{" +
                "count=" + count +
                ", content=" + content +
                '}';
    }

    public ContentDto(long count, List<T> content) {
        this.count = count;
        this.content = content;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
