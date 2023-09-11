package com.example.mangatn.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mangatn.R;
import com.example.mangatn.activities.ItemViewerActivity;
import com.example.mangatn.adapters.CustomSpinnerAdapter;
import com.example.mangatn.adapters.GridAdapter;
import com.example.mangatn.interfaces.manga.OnFetchDataListener;
import com.example.mangatn.interfaces.manga.SelectListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.manga.MangaModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment1 extends Fragment implements SelectListener {
    public GridView gridView;
    private GridAdapter gridAdapter;
    private SearchView searchView;
    private Spinner spinnerFilter;
    private CustomSpinnerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int pageSize = 6;
    private int pageNumber = 0;
    private final List<MangaModel> mangaModels = new ArrayList<>();
    private List<EMangaStatus> statusList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_1, container, false);

        gridView = view1.findViewById(R.id.gridView);
        searchView = view1.findViewById(R.id.search_view);

        spinnerFilter = view1.findViewById(R.id.spinnerFilter);

        adapter = new CustomSpinnerAdapter(
                container.getContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.filter_options))
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setSelection(0);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedFilter = parentView
                        .getItemAtPosition(position)
                        .toString();

                adapter.setSelectedItemPosition(position);

                switch (selectedFilter) {
                    case "Completed": {
                        statusList = new ArrayList<>();

                        statusList.add(EMangaStatus.COMPLETED);

                        break;
                    }
                    case "Ongoing": {
                        statusList = new ArrayList<>();

                        statusList.add(EMangaStatus.ONGOING);

                        break;
                    }
                    default: {
                        statusList = new ArrayList<>();

                        statusList.add(EMangaStatus.COMPLETED);
                        statusList.add(EMangaStatus.ONGOING);

                        break;
                    }
                }

                mangaModels.clear();
                pageNumber = 0;

                loadData(container, searchView.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        swipeRefreshLayout = view1.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);

            mangaModels.clear();
            pageNumber = 0;

            loadData(container, searchView.getQuery().toString());
        });

        statusList.add(EMangaStatus.ONGOING);
        statusList.add(EMangaStatus.COMPLETED);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mangaModels.clear();
                pageNumber = 0;

                loadData(container, query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });

        gridAdapter = new GridAdapter(container.getContext(), this.mangaModels, this);
        gridView.setAdapter(gridAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!swipeRefreshLayout.isRefreshing() && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    loadData(container, searchView.getQuery().toString());
                }
            }
        });

        /*RequestManager requestManager = new RequestManager(container.getContext());
        requestManager.getAllMangaGenre(new OnFetchAllGenreListener() {
            @Override
            public void onFetchData(List<String> list, String message, Context context) {
                Log.i("all genre", "onFetchData: " + list);
            }

            @Override
            public void onError(String message, Context context) {

            }
        });*/

        return view1;
    }

    private void loadData(ViewGroup container, String searchView) {
        RequestManager requestManager = new RequestManager(container.getContext());

        swipeRefreshLayout.setRefreshing(true);
        requestManager.getMangaList(listener, searchView, statusList, pageNumber, pageSize);
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
            if (list.isEmpty() && (pageNumber * pageSize < mangaModels.size())) {
                Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();
            } else {
                showManga(list);
            }

            swipeRefreshLayout.setRefreshing(false);
            searchView.clearFocus();
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!: " + message, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void showManga(List<MangaModel> list) {
        if (!list.isEmpty()) {
            mangaModels.addAll(list);

            gridAdapter.notifyDataSetChanged();
            pageNumber++;
        }
    }
}