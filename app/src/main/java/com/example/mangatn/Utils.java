package com.example.mangatn;

public class Utils {
    public static final String API_URL = "http://192.168.31.40:8080/api";
    public static final String MANGA_URL = "/v1/manga/";
    public static final String CHAPTER_URL = "/v1/chapter/";
    public static final String USERS_URL = "/users/";
    public static final String USER_SUCCESSFULLY_CREATED = "Compte a été créé avec succès.";
    public static final String USER_SUCCESSFULLY_UPDATED = "Compte a été mis à jour avec succès.";

    private static String userToken = "";

    public static String getUserToken() {
        return userToken;
    }

    public static void setUserToken(String userToken) {
        Utils.userToken = userToken;
    }
}
