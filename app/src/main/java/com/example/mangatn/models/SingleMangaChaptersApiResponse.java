package com.example.mangatn.models;


import java.io.Serializable;
import java.util.List;

public class SingleMangaChaptersApiResponse implements Serializable {
    private String timeStamp;
    private int statusCode;
    private String status;
    private String message;
    private Data data;

    @Override
    public String toString() {
        return "MangaApiResponse{" +
                "timeStamp='" + timeStamp + '\'' +
                ", statusCode=" + statusCode +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private List<ChapterModel> chapters;

        public List<ChapterModel> getChapters() {
            return chapters;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "chapters=" + chapters +
                    '}';
        }
    }
}