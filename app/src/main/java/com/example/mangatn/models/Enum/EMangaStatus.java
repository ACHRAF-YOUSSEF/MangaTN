package com.example.mangatn.models.Enum;

import java.util.Arrays;
import java.util.List;

public enum EMangaStatus {
    ONGOING("Ongoing"), // Custom display
    COMPLETED("Completed"); // Custom display

    private final String customDisplay;

    EMangaStatus(String customDisplay) {
        this.customDisplay = customDisplay;
    }

    public String getCustomDisplay() {
        return customDisplay;
    }

    public static String getName(EMangaStatus status) {
        return status.customDisplay;
    }

    public static EMangaStatus fromCustomDisplay(String customDisplay) {
        for (EMangaStatus status : EMangaStatus.values()) {
            if (status.customDisplay.equalsIgnoreCase(customDisplay)) {
                return status;
            }
        }

        throw new IllegalArgumentException("No enum constant with custom display: " + customDisplay);
    }

    public static List<EMangaStatus> getAll() {
        return Arrays.asList(EMangaStatus.values());
    }
}
