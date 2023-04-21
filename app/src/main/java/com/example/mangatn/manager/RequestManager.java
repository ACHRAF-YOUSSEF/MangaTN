package com.example.mangatn.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mangatn.interfaces.OnFetchDataListener;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.models.MangaApiResponse;
import com.example.mangatn.models.MangaModel;
import com.example.mangatn.models.SingleMangaApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
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

    public void getMangaList(OnFetchDataListener listener, String query) {
        CallMangaApi callMangaApi = retrofit.create(CallMangaApi.class);
        Call<MangaApiResponse> call = (query == null)? callMangaApi.callMangaList() : callMangaApi.callManga(query);

        try {
            call.enqueue(new Callback<MangaApiResponse>() {
                @Override
                public void onResponse(Call<MangaApiResponse> call, Response<MangaApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        listener.onFetchData(response.body().getData().getMangaList(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<MangaApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getManga(OnFetchSingleDataListener listener, String mangaId) {
        CallMangaApi callMangaApi = retrofit.create(CallMangaApi.class);
        Call<SingleMangaApiResponse> call = callMangaApi.callFetchManga(mangaId);

        try {
            call.enqueue(new Callback<SingleMangaApiResponse>() {
                @Override
                public void onResponse(Call<SingleMangaApiResponse> call, Response<SingleMangaApiResponse> response) {
                    if (!response.isSuccessful()) {
                        int statusCode = response.code();
                        String errorMessage = "Error!! HTTP Status Code: " + statusCode;

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        listener.onError("Request Failed!", context);
                    } else {
                        assert response.body() != null;

                        Log.i("body", "onResponse: " + response.body());

                        listener.onFetchData(response.body().getData().getManga(), response.message(), context);
                    }
                }

                @Override
                public void onFailure(Call<SingleMangaApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!", context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallMangaApi {
        @GET("get/All/20")
        Call<MangaApiResponse> callMangaList();

        @GET("get/{title}/20")
        Call<MangaApiResponse> callManga(
            @Path("title") String title
        );

        @GET("get/{mangaId}/all")
        Call<SingleMangaApiResponse> callFetchManga(
            @Path("mangaId") String mangaId
        );
    }
}
