package com.example.mangatn.manager;

import static com.example.mangatn.Utils.*;
import static com.example.mangatn.Utils.getUserToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.mangatn.Utils;
import com.example.mangatn.interfaces.OnBookmarkListener;
import com.example.mangatn.interfaces.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.OnCheckForUpdateListener;
import com.example.mangatn.interfaces.OnCheckIfAChapterIsViewedListener;
import com.example.mangatn.interfaces.OnFetchBookmarkedMangasListener;
import com.example.mangatn.interfaces.OnFetchDataListener;
import com.example.mangatn.interfaces.OnFetchMangaChaptersListListener;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.OnFetchUpdateListener;
import com.example.mangatn.interfaces.OnMarkAsViewedOrNotListener;
import com.example.mangatn.interfaces.OnSignInListener;
import com.example.mangatn.interfaces.OnSignInWithTokenListener;
import com.example.mangatn.interfaces.OnSignupListener;
import com.example.mangatn.interfaces.OnUpdateUserListener;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.Bookmark;
import com.example.mangatn.models.ChaptersListApiResponse;
import com.example.mangatn.models.JwtResponse;
import com.example.mangatn.models.LoginModel;
import com.example.mangatn.models.MangaListApiResponse;
import com.example.mangatn.models.MangaModel;
import com.example.mangatn.models.SignupModel;
import com.example.mangatn.models.UpdateModel;
import com.example.mangatn.models.UserModel;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    private final Context context;
    private final OkHttpClient httpClient = new OkHttpClient
            .Builder()
            .connectTimeout(30 * 60, TimeUnit.SECONDS)
            .readTimeout(30 * 60, TimeUnit.SECONDS)
            .writeTimeout(30 * 60, TimeUnit.SECONDS)
            .build();
    private final Retrofit retrofit_chapter = new Retrofit.Builder()
            .baseUrl(API_URL + CHAPTER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
    private final Retrofit retrofit_manga = new Retrofit.Builder()
            .baseUrl(API_URL + MANGA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
    private final Retrofit retrofit_users = new Retrofit.Builder()
            .baseUrl(API_URL + USERS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void getMangaList(OnFetchDataListener listener, String query, int pageNumber, int pageSize) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<MangaListApiResponse> call = callMangaApi.callManga(query, pageNumber, pageSize);

        try {
            call.enqueue(new Callback<MangaListApiResponse>() {
                @Override
                public void onResponse(Call<MangaListApiResponse> call, Response<MangaListApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body().getMangas(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<MangaListApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getManga(OnFetchSingleDataListener listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<MangaModel> call = callMangaApi.callFetchManga(mangaId);

        try {
            call.enqueue(new Callback<MangaModel>() {
                @Override
                public void onResponse(Call<MangaModel> call, Response<MangaModel> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        Log.i("body", "onResponse: " + response.body());

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<MangaModel> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMangaChapters(OnFetchMangaChaptersListListener listener, String mangaId, int pageNumber, int pageSize) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ChaptersListApiResponse> call = callChapterApi.callGetChapters(mangaId, pageNumber, pageSize);

        call.enqueue(new Callback<ChaptersListApiResponse>() {
            @Override
            public void onResponse(Call<ChaptersListApiResponse> call, Response<ChaptersListApiResponse> response) {
                if (!response.isSuccessful()) {
                    int statusCode = response.code();
                    String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                    listener.onError(errorMessage, context);
                } else {
                    ChaptersListApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        listener.onFetchData(apiResponse, "success", context);
                    } else {
                        listener.onError("fetch Failed!", context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ChaptersListApiResponse> call, Throwable t) {
                listener.onError("fetch Failed!" + t.getMessage(), context);
            }
        });
    }

    public void checkForUpdate(OnCheckForUpdateListener listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<Boolean> call = callMangaApi.callMangaCheckForUpdate(mangaId);

        try {
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        Log.i("body", "onResponse: " + response.body());

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkForBookmark(OnCheckForBookmarkListener listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<Boolean> call = callMangaApi.checkForBookmark(getUserToken(), mangaId);

        try {
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        setUserToken(null);

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        Log.i("body", "onResponse: " + response.body());

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateManga(OnFetchUpdateListener<ApiResponse> listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<ApiResponse> call = callMangaApi.callUpdateManga(mangaId);

        try {
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        Log.i("body", "onResponse: " + response.body());

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(OnUpdateUserListener listener, UpdateModel updateRequest) {
        CallUsersApi callUsersApi = retrofit_users.create(CallUsersApi.class);
        Call<ApiResponse> call = callUsersApi.callUpdateUser(getUserToken(), updateRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    int statusCode = response.code();
                    String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                    setUserToken(null);

                    listener.onError("Update Failed!", context);
                } else {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getMessage().equals(USER_SUCCESSFULLY_UPDATED)) {
                        listener.onSuccess(apiResponse,"Update Successful!", context);
                    } else {
                        listener.onError("Update Failed!", context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                listener.onError(t.getMessage(), context);
            }
        });
    }

    public void signup(OnSignupListener listener, SignupModel signupRequest) {
        CallUsersApi callUsersApi = retrofit_users.create(CallUsersApi.class);
        Call<ApiResponse> call = callUsersApi.callSignup(signupRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    int statusCode = response.code();
                    String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                    listener.onSignupError("Signup Failed!", context);
                } else {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getMessage().equals(USER_SUCCESSFULLY_CREATED)) {
                        listener.onSignupSuccess("Signup Successful!", context);
                    } else {
                        listener.onSignupError("Signup Failed!", context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                listener.onSignupError("Signup Failed!", context);
            }
        });
    }

    public void signInWithToken(OnSignInWithTokenListener listener) {
        CallUsersApi callUsersApi = retrofit_users.create(CallUsersApi.class);
        Call<UserModel> call = callUsersApi.callFetchUserDetails(Utils.getUserToken());

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (!response.isSuccessful()) {
                    int statusCode = response.code();
                    String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                    setUserToken(null);

                    listener.onSignInError("Sign-In Failed!", context);
                } else {
                    UserModel apiResponse = response.body();

                    if (apiResponse != null) {
                        listener.onSignInSuccess(apiResponse,"Sign-In Successful!", context);
                    } else {
                        listener.onSignInError("Sign-In Failed!", context);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                listener.onSignInError("Sign-In Failed!:" + t.getMessage(), context);
            }
        });
    }

    public void signIn(OnSignInListener listener, LoginModel loginRequest) {
        CallUsersApi callUsersApi = retrofit_users.create(CallUsersApi.class);
        Call<JwtResponse> call = callUsersApi.callSignIn(loginRequest);

        call.enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (!response.isSuccessful()) {
                    int statusCode = response.code();
                    String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                    listener.onSignInError("Sign-In Failed!", context);
                } else {
                    JwtResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        // save login token
                        Log.i("token:", "onResponse: " + apiResponse.getToken());
                        setUserToken(apiResponse.getToken());

                        listener.onSignInSuccess("Sign-In Successful!", context);
                    } else {
                        listener.onSignInError("Sign-In Failed!", context);
                    }
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                listener.onSignInError("Sign-In Failed!", context);
            }
        });
    }

    public void fetchBookmarked(OnFetchBookmarkedMangasListener listener, int pageNumber, int pageSize) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<MangaListApiResponse> call = callMangaApi.fetchBookmarked(getUserToken(), pageNumber, pageSize);

        try {
            call.enqueue(new Callback<MangaListApiResponse>() {
                @Override
                public void onResponse(Call<MangaListApiResponse> call, Response<MangaListApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        setUserToken(null);

                        listener.onFetchError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchSuccess(response.body().getMangas(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<MangaListApiResponse> call, Throwable t) {
                    listener.onFetchError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bookmark(OnBookmarkListener listener, Bookmark bookmark) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<ApiResponse> call = callMangaApi.callBookmark(getUserToken(), bookmark);

        try {
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        setUserToken(null);

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        Log.i("body", "onResponse: " + response.body());

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markAsViewedOrNot(OnMarkAsViewedOrNotListener listener, Integer chapterReference, String mangaId, Boolean isViewed) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ApiResponse> call = isViewed ?
                callChapterApi.callMarkAsViewed(chapterReference, getUserToken(), mangaId)
                : callChapterApi.callMarkAsNotViewed(chapterReference, getUserToken(), mangaId);

        try {
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkIfAChapterIsViewed(OnCheckIfAChapterIsViewedListener listener, Integer chapterReference, String mangaId) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<Boolean> call = callChapterApi.callCheckIfTheChapterIsViewed(chapterReference, getUserToken(), mangaId);

        try {
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallUsersApi {
        @POST("signup")
        Call<ApiResponse> callSignup(
                @Body SignupModel signupRequest
        );

        @POST("signin")
        Call<JwtResponse> callSignIn(
                @Body LoginModel loginRequest
        );

        @GET("sign-in-with-token")
        Call<UserModel> callFetchUserDetails(
                @Header("Authorization") String token
        );

        @PUT("updateUser")
        Call<ApiResponse> callUpdateUser(
                @Header("Authorization") String token,
                @Body UpdateModel updateUser
        );
    }

    public interface CallChapterApi {
        @GET("{mangaId}/chapters")
        Call<ChaptersListApiResponse> callGetChapters(
                @Path("mangaId") String mangaId,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSiz
        );

        @POST("{mangaId}/{chapterReference}/markAsViewed")
        Call<ApiResponse> callMarkAsViewed(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @DELETE("{mangaId}/{chapterReference}/markAsNotViewed")
        Call<ApiResponse> callMarkAsNotViewed(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @GET("{mangaId}/{chapterReference}/viewed")
        Call<Boolean> callCheckIfTheChapterIsViewed(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );
    }

    public interface CallMangaApi {
        @GET("all")
        Call<MangaListApiResponse> callManga(
            @Query("query") String query,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize
        );

        @GET("{mangaId}")
        Call<MangaModel> callFetchManga(
            @Path("mangaId") String mangaId
        );

        @GET("{mangaId}/check")
        Call<Boolean> callMangaCheckForUpdate(
                @Path("mangaId") String mangaId
        );

        @POST("{mangaId}/update")
        Call<ApiResponse> callUpdateManga(
                @Path("mangaId") String mangaId
        );

        @GET("{mangaId}/bookmarked")
        Call<Boolean> checkForBookmark(
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @POST("bookmark")
        Call<ApiResponse> callBookmark(
                @Header("Authorization") String token,
                @Body Bookmark bookmark
        );

        @GET("bookmarked")
        Call<MangaListApiResponse> fetchBookmarked(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );
    }
}
