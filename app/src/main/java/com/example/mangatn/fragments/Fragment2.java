package com.example.mangatn.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.activities.ItemViewerActivity;
import com.example.mangatn.activities.SignInActivity;
import com.example.mangatn.adapters.GridAdapter;
import com.example.mangatn.interfaces.OnFetchBookmarkedMangasListener;
import com.example.mangatn.interfaces.SelectListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.MangaModel;

import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment implements SelectListener, OnFetchBookmarkedMangasListener {
    public GridView gridView;
    private TextView textView, textView2;
    private GridAdapter gridAdapter;
    private RequestManager requestManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_2, container, false);

        gridView = view1.findViewById(R.id.gridView_favorites);
        textView = view1.findViewById(R.id.textViewSignIn_favorites);
        textView2 = view1.findViewById(R.id.noData);
        swipeRefreshLayout = view1.findViewById(R.id.refresh);

        requestManager = new RequestManager(container.getContext());

        updateData();

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(container.getContext(), SignInActivity.class);
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
        });

        return view1;
    }

    public void updateData() {
        Log.i("token:", "onCreateView: " + Utils.getUserToken());

        if (!Utils.getUserToken().isEmpty()) {
            // api call to get the bookmarked mangas
            swipeRefreshLayout.setRefreshing(true);
            requestManager.fetchBookmarked(this, 0, 100);
        } else {
            textView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showManga(List<MangaModel> list, Context context) {
        gridAdapter = new GridAdapter(context, list, this);
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public void OnMangaClicked(MangaModel manga, Context context) {
        Intent intent = new Intent(context, ItemViewerActivity.class);

        intent.putExtra("mangaId", manga.getMangaId());
        intent.putExtra("title", manga.getTitle());
        intent.putExtra("coverImgPath", manga.getCoverImgPath());
        intent.putExtra("count", manga.getCount());
        intent.putExtra("upToDate", manga.getUpToDate());

        startActivity(intent);
    }

    @Override
    public void onFetchSuccess(List<MangaModel> bookmarkedMangas, String message, Context context) {
        if (bookmarkedMangas.isEmpty()) {
            Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();

            textView.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            textView2.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            swipeRefreshLayout.setRefreshing(false);
        } else {
            showManga(bookmarkedMangas, context);

            textView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onFetchError(String error, Context context) {
        Toast.makeText(context, "An Error Occurred!!!: " + error, Toast.LENGTH_SHORT).show();
    }
}