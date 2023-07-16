package com.example.mangatn.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mangatn.interfaces.OnCheckForUpdateListener;
import com.example.mangatn.interfaces.OnFetchDataListener;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.OnFetchUpdateListener;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.Bookmark;
import com.example.mangatn.models.MangaListApiResponse;
import com.example.mangatn.models.MangaModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    private final Context context;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.31.40:8080/api/v1/manga/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void getMangaList(OnFetchDataListener listener, String query, int pageNumber, int pageSize) {
        CallMangaApi callMangaApi = retrofit.create(CallMangaApi.class);
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
        CallMangaApi callMangaApi = retrofit.create(CallMangaApi.class);
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

    public void checkForUpdate(OnCheckForUpdateListener listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit.create(CallMangaApi.class);
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

    public void updateManga(OnFetchUpdateListener<ApiResponse> listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit.create(CallMangaApi.class);
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
        Call<Boolean> callCheckForBookmark(
                @Header("Authorization") String token,
                @Path("mangaId") String mangaId
        );

        @POST("bookmark")
        Call<Boolean> callBookmark(
                @Header("Authorization") String token,
                @Body Bookmark bookmark
        );
    }
}
