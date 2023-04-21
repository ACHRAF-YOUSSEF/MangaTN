package com.example.mangatn.models;

import java.io.Serializable;
import java.util.List;

public class MangaApiResponse implements Serializable {
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
        private List<MangaModel> mangaList;

        public List<MangaModel> getMangaList() {
            return mangaList;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "manga=" + mangaList +
                    '}';
        }
    }
}
