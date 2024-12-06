package com.example.mangatn.models.pagination;

public class PaginationDto {
    private long length;
    private long size;
    private long page;
    private long lastPage;
    private long startIndex;
    private long endIndex;

    public PaginationDto() {
    }

    public PaginationDto(long length, long size, long page, long lastPage, long startIndex, long endIndex) {
        this.length = length;
        this.size = size;
        this.page = page;
        this.lastPage = lastPage;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getLastPage() {
        return lastPage;
    }

    public void setLastPage(long lastPage) {
        this.lastPage = lastPage;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return "PaginationDto{" +
                "length=" + length +
                ", size=" + size +
                ", page=" + page +
                ", lastPage=" + lastPage +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
