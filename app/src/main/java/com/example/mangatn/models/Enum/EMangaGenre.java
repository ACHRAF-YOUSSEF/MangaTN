package com.example.mangatn.models.Enum;

public enum EMangaGenre {
    ACTION_MANGA("Action"),
    ADVENTURE_MANGA("Adventure"),
    COMEDY_MANGA("Comedy"),
    COOKING_MANGA("Cooking"),
    DRAMA_MANGA("Drama"),
    FANTASY_MANGA("Fantasy"),
    HISTORICAL_MANGA("Historical"),
    ISEKAI_MANGA("Isekai"),
    JOSEI_MANGA("Josei"),
    MANHUA_MANGA("Manhua"),
    MANHWA_MANGA("Manhwa"),
    MARTIAL_ARTS_MANGA("Martial Arts"),
    MYSTERY_MANGA("Mystery"),
    ONE_SHOT_MANGA("One Shot"),
    PSYCHOLOGICAL_MANGA("Psychological"),
    ROMANCE_MANGA("Romance"),
    SCHOOL_LIFE_MANGA("School Life"),
    SCI_FI_MANGA("Sci-Fi"),
    SEINEN_MANGA("Seinen"),
    SHOUJO_MANGA("Shoujo"),
    SHOUJO_AI_MANGA("Shoujo Ai"),
    SHOUNEN_MANGA("Shounen"),
    SHOUNEN_AI_MANGA("Shounen Ai"),
    SLICE_OF_LIFE_MANGA("Slice of Life"),
    SPORTS_MANGA("Sports"),
    SUPERNATURAL_MANGA("Supernatural"),
    TRAGEDY_MANGA("Tragedy"),
    WEBTOONS_MANGA("Webtoons");

    private final String customDisplayName;

    EMangaGenre(String customDisplayName) {
        this.customDisplayName = customDisplayName;
    }

    public String getCustomDisplayName() {
        return customDisplayName;
    }

    public static String getName(EMangaGenre genre) {
        return genre.customDisplayName;
    }

    public static EMangaGenre fromCustomDisplayName(String customDisplayName) {
        for (EMangaGenre genre : values()) {
            if (genre.customDisplayName.equalsIgnoreCase(customDisplayName)) {
                return genre;
            }
        }

        throw new IllegalArgumentException("No enum constant with custom display name: " + customDisplayName);
    }
}
