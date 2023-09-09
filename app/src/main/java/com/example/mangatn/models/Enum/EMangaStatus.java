package com.example.mangatn.models.Enum;

public enum EMangaStatus {
    ONGOING,
    COMPLETED;

    public static String getName(EMangaStatus status) {
        String name = status.name();

        return name.charAt(0)  + name.substring(1).toLowerCase();
    }
}
