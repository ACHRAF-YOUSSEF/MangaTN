package com.example.mangatn.models.Enum;

import java.util.Arrays;
import java.util.List;

public enum EMangaBookmark {
    BOTH("Both"),
    BOOKMARKED("Bookmarked"),
    NOT_BOOKMARKED("Not Bookmarked");

    private final String customDisplay;

    EMangaBookmark(String customDisplay) {
        this.customDisplay = customDisplay;
    }

    public String getCustomDisplay() {
        return customDisplay;
    }

    public static String getName(EMangaBookmark bookmark) {
        return bookmark.customDisplay;
    }

    public static EMangaBookmark fromCustomDisplay(String customDisplay) {
        for (EMangaBookmark bookmark : EMangaBookmark.values()) {
            if (bookmark.customDisplay.equalsIgnoreCase(customDisplay)) {
                return bookmark;
            }
        }

        throw new IllegalArgumentException("No enum constant with custom display: " + customDisplay);
    }

    public static List<EMangaBookmark> getAll() {
        return Arrays.asList(EMangaBookmark.values());
    }
}