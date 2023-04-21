package com.example.mangatn.fragments;

import static com.example.mangatn.activities.MainActivity.WEBSITE_URL;
import static com.example.mangatn.activities.MainActivity.mangalList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mangatn.activities.ItemViewerActivity;
import com.example.mangatn.R;
import com.example.mangatn.adapters.GridAdapter;
import com.example.mangatn.interfaces.OnFetchDataListener;
import com.example.mangatn.interfaces.SelectListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.MangaApiResponse;
import com.example.mangatn.models.MangaModel;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Fragment1 extends Fragment implements SelectListener {
    public GridView gridView;
    private GridAdapter gridAdapter;
    private SearchView searchView;
    private ProgressDialog dialog;
    private RequestManager requestManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_1, container, false);

        gridView = view1.findViewById(R.id.gridView);
        searchView = view1.findViewById(R.id.search_view);

        dialog = new ProgressDialog(container.getContext());
        dialog.setTitle("Fetching manga");
        dialog.show();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                requestManager = new RequestManager(container.getContext());

                requestManager.getMangaList(listener, query);

                dialog.setTitle("Fetching manga search results");
                dialog.show();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        requestManager = new RequestManager(container.getContext());

        requestManager.getMangaList(listener, null);

        return view1;
    }

    @Override
    public void OnMangaClicked(MangaModel manga, Context context) {
        startActivity(
                new Intent(context, ItemViewerActivity.class)
                        .putExtra("mangaId", manga.getMangaId())
        );
    }

    private final OnFetchDataListener<MangaApiResponse> listener = new OnFetchDataListener<MangaApiResponse>() {
        @Override
        public void onFetchData(List<MangaModel> list, String message, Context context) {
            if (list.isEmpty()) {
                Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();
            } else {
                showManga(list, context);

                dialog.dismiss();
            }
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!", Toast.LENGTH_SHORT).show();
        }
    };

    private void showManga(List<MangaModel> list, Context context) {
        gridAdapter = new GridAdapter(context, list, this);
        gridView.setAdapter(gridAdapter);
    }
}