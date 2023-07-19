package com.example.mangatn.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.mangatn.models.ChapterModel;
import com.example.mangatn.models.MangaModel;

import java.util.List;

public class Fragment1 extends Fragment implements SelectListener {
    public GridView gridView;
    private GridAdapter gridAdapter;
    private SearchView searchView;
    private ProgressDialog dialog;
    private RequestManager requestManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_1, container, false);

        gridView = view1.findViewById(R.id.gridView);
        searchView = view1.findViewById(R.id.search_view);

        dialog = new ProgressDialog(container.getContext());

        swipeRefreshLayout = view1.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            requestManager.getMangaList(listener, searchView.getQuery().toString(), 0, 100);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                requestManager = new RequestManager(container.getContext());

                swipeRefreshLayout.setRefreshing(true);
                requestManager.getMangaList(listener, query, 0, 100);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        requestManager = new RequestManager(container.getContext());

        swipeRefreshLayout.setRefreshing(true);
        requestManager.getMangaList(listener, "", 0, 100);

        return view1;
    }

    @Override
    public void OnMangaClicked(MangaModel manga, Context context) {
        startActivity(
                new Intent(context, ItemViewerActivity.class)
                        .putExtra("mangaId", manga.getMangaId())
        );
    }

    private final OnFetchDataListener listener = new OnFetchDataListener() {
        @Override
        public void onFetchData(List<MangaModel> list, String message, Context context) {
            if (list.isEmpty()) {
                Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                searchView.clearFocus();
            } else {
                showManga(list, context);

                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
                searchView.clearFocus();
            }
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!: " + message, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void showManga(List<MangaModel> list, Context context) {
        gridAdapter = new GridAdapter(context, list, this);
        gridView.setAdapter(gridAdapter);
    }
}