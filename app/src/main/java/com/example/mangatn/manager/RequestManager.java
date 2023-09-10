package com.example.mangatn.manager;

import static com.example.mangatn.Utils.*;
import static com.example.mangatn.Utils.getUserToken;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mangatn.Utils;
import com.example.mangatn.interfaces.bookmark.OnBookmarkListener;
import com.example.mangatn.interfaces.bookmark.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.manga.genre.OnFetchAllGenreListener;
import com.example.mangatn.interfaces.update.OnCheckForUpdateListener;
import com.example.mangatn.interfaces.update.OnCheckIfAChapterIsViewedListener;
import com.example.mangatn.interfaces.bookmark.OnFetchBookmarkedMangasListener;
import com.example.mangatn.interfaces.manga.OnFetchDataListener;
import com.example.mangatn.interfaces.chapter.OnFetchMangaChapterListener;
import com.example.mangatn.interfaces.chapter.OnFetchMangaChaptersListListener;
import com.example.mangatn.interfaces.manga.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.update.OnFetchUpdateListener;
import com.example.mangatn.interfaces.chapter.OnGetReadChapterListener;
import com.example.mangatn.interfaces.update.OnMarkAsViewedOrNotListener;
import com.example.mangatn.interfaces.auth.OnSignInListener;
import com.example.mangatn.interfaces.auth.OnSignInWithTokenListener;
import com.example.mangatn.interfaces.auth.OnSignupListener;
import com.example.mangatn.interfaces.auth.OnUpdateUserListener;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.bookmark.BookmarkModel;
import com.example.mangatn.models.chapter.ChapterModel;
import com.example.mangatn.models.chapter.ChaptersListApiResponse;
import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.JwtResponse;
import com.example.mangatn.models.auth.LoginModel;
import com.example.mangatn.models.manga.MangaListApiResponse;
import com.example.mangatn.models.manga.MangaModel;
import com.example.mangatn.models.chapter.ReadChapterModel;
import com.example.mangatn.models.auth.SignupModel;
import com.example.mangatn.models.auth.UpdateModel;
import com.example.mangatn.models.auth.UserModel;

import java.util.List;
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

    public void getAllMangaGenre(OnFetchAllGenreListener listener) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<List<String>> call = callMangaApi.callGetGenres();

        try {
            call.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMangaList(OnFetchDataListener listener, String query, List<EMangaStatus> statusList, int pageNumber, int pageSize) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<MangaListApiResponse> call = callMangaApi.callManga(query, pageNumber, pageSize, statusList);

        try {
            call.enqueue(new Callback<MangaListApiResponse>() {
                @Override
                public void onResponse(Call<MangaListApiResponse> call, Response<MangaListApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

        if (getUserToken() != null) {
            if (!getUserToken().isEmpty()) {
                call = callChapterApi.callGetChapters(getUserToken(), mangaId, pageNumber, pageSize);
            }
        }

        call.enqueue(new Callback<ChaptersListApiResponse>() {
            @Override
            public void onResponse(Call<ChaptersListApiResponse> call, Response<ChaptersListApiResponse> response) {
                if (!response.isSuccessful()) {
                    int statusCode = response.code();
                    String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                    if (statusCode == 401) {
                        setUserToken(null);
                    }

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

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

                    if (statusCode == 401) {
                        setUserToken(null);
                    }

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

                    if (statusCode == 401) {
                        setUserToken(null);
                    }

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

                    if (statusCode == 401) {
                        setUserToken(null);
                    }

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

                    if (statusCode == 401) {
                        setUserToken(null);
                    }

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

    public void bookmark(OnBookmarkListener listener, BookmarkModel bookmarkModel) {
        CallMangaApi callMangaApi = retrofit_manga.create(CallMangaApi.class);
        Call<ApiResponse> call = callMangaApi.callBookmark(getUserToken(), bookmarkModel);

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

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

    public void createOrDeleteReadChapter(OnMarkAsViewedOrNotListener listener, Integer chapterReference, String mangaId, Boolean isViewed) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ApiResponse> call = isViewed ?
                callChapterApi.callCreateReadChapter(chapterReference, getUserToken(), mangaId)
                : callChapterApi.callDeleteReadChapter(chapterReference, getUserToken(), mangaId);

        try {
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

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

    public void getReadChapter(OnGetReadChapterListener listener, Integer chapterReference, String mangaId) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ReadChapterModel> call = callChapterApi.callGetReadChapter(chapterReference, getUserToken(), mangaId);

        try {
            call.enqueue(new Callback<ReadChapterModel>() {
                @Override
                public void onResponse(Call<ReadChapterModel> call, Response<ReadChapterModel> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ReadChapterModel> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLastReadAndInProgressChapter(OnGetReadChapterListener listener, String mangaId) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ReadChapterModel> call = callChapterApi.callGetLastReadAndInProgressChapter(getUserToken(), mangaId);

        try {
            call.enqueue(new Callback<ReadChapterModel>() {
                @Override
                public void onResponse(Call<ReadChapterModel> call, Response<ReadChapterModel> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ReadChapterModel> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFirstChapter(OnGetReadChapterListener listener, String mangaId) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ReadChapterModel> call = callChapterApi.callGetFirstChapter(mangaId);

        try {
            call.enqueue(new Callback<ReadChapterModel>() {
                @Override
                public void onResponse(Call<ReadChapterModel> call, Response<ReadChapterModel> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ReadChapterModel> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateReadChapter(OnFetchUpdateListener listener, Integer chapterReference, String mangaId, ReadChapterModel readChapterModel) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ApiResponse> call = callChapterApi.callUpdateReadChapter(chapterReference, getUserToken(), mangaId, readChapterModel);

       try {
           call.enqueue(new Callback<ApiResponse>() {
               @Override
               public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                   if (!response.isSuccessful()) {
                       int statusCode = response.code();
                       String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                       if (statusCode == 401) {
                           setUserToken(null);
                       }

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

    public void getMangaChapter(OnFetchMangaChapterListener listener, Integer chapterReference, String mangaId) {
        CallChapterApi callChapterApi = retrofit_chapter.create(CallChapterApi.class);
        Call<ChapterModel> call = callChapterApi.callGetChapter(mangaId, chapterReference);

        try {
            call.enqueue(new Callback<ChapterModel>() {
                @Override
                public void onResponse(Call<ChapterModel> call, Response<ChapterModel> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        if (statusCode == 401) {
                            setUserToken(null);
                        }

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<ChapterModel> call, Throwable t) {
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

        @GET("{mangaId}/{chapterReference}")
        Call<ChapterModel> callGetChapter(
                @Path("mangaId") String mangaId,
                @Path("chapterReference") Integer chapterTitle
        );

        @GET("{mangaId}/chapters/get")
        Call<ChaptersListApiResponse> callGetChapters(
                @Header("Authorization") String token,
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

        @POST("{mangaId}/{chapterReference}/createReadChapter")
        Call<ApiResponse> callCreateReadChapter(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @PUT("{mangaId}/{chapterReference}/updateReadChapter")
        Call<ApiResponse> callUpdateReadChapter(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId,
                @Body ReadChapterModel updateReadChapterDto
        );

        @DELETE("{mangaId}/{chapterReference}/deleteReadChapter")
        Call<ApiResponse> callDeleteReadChapter(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @GET("{mangaId}/{chapterReference}/getReadChapter")
        Call<ReadChapterModel> callGetReadChapter(
                @Path("chapterReference") Integer chapterReference,
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @GET("{mangaId}/lastViewedChapter")
        Call<ReadChapterModel> callGetLastReadAndInProgressChapter(
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @GET("{mangaId}/getFirst")
        Call<ReadChapterModel> callGetFirstChapter(
                @Path("mangaId") String mangaId
        );
    }

    public interface CallMangaApi {
        @GET("genres")
        Call<List<String>> callGetGenres();

        @GET("all")
        Call<MangaListApiResponse> callManga(
            @Query("query") String query,
            @Query("pageNumber") int pageNumber,
            @Query("pageSize") int pageSize,
            @Query("statusList") List<EMangaStatus> statusList
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

        @POST("bookmarkModel")
        Call<ApiResponse> callBookmark(
                @Header("Authorization") String token,
                @Body BookmarkModel bookmarkModel
        );

        @GET("bookmarked")
        Call<MangaListApiResponse> fetchBookmarked(
                @Header("Authorization") String token,
                @Query("pageNumber") int pageNumber,
                @Query("pageSize") int pageSize
        );
    }
}
