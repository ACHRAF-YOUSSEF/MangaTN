package com.example.mangatn;

import org.jetbrains.annotations.Contract;

public class Utils {
    public static final String API_URL = "http://192.168.31.40:8081/api";
    public static final String MANGA_URL = "/v1/manga/";
    public static final String CHAPTER_URL = "/v1/chapter/";
    public static final String USERS_URL = "/users/";
    public static final String USER_SUCCESSFULLY_CREATED = "Account has been successfully created.";
    public static final String USER_SUCCESSFULLY_UPDATED = "Account has been successfully updated.";

    private static String userToken = "";

    @Contract(pure = true)
    public static String getUserToken() {
        return userToken;
    }

    public static void setUserToken(String userToken) {
        Utils.userToken = userToken;
    }

    @Contract(pure = true)
    public static boolean userIsAuthenticated() {
        return userToken != null && !userToken.isEmpty();
    }
}
