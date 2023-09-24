package com.example.mangatn.fragments.home;

import static com.example.mangatn.Utils.userIsAuthenticated;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.activities.auth.ResetPasswordActivity;
import com.example.mangatn.activities.auth.SignInActivity;
import com.example.mangatn.activities.auth.UpdateProfileActivity;
import com.example.mangatn.activities.manga.ItemViewerActivity;
import com.example.mangatn.adapters.GridAdapter;
import com.example.mangatn.fragments.filter.MangaFilterFragment;
import com.example.mangatn.fragments.filter.MangaFilterFragment.OnFilterAppliedListener;
import com.example.mangatn.interfaces.manga.OnFetchDataListener;
import com.example.mangatn.interfaces.manga.SelectListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.Enum.EMangaBookmark;
import com.example.mangatn.models.Enum.EMangaGenre;
import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.manga.MangaModel;
import com.example.mangatn.models.manga.filter.MangaFilter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SelectListener, OnFilterAppliedListener {
    public GridView gridView;
    private GridAdapter gridAdapter;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int pageSize = 6;
    private int pageNumber = 0;
    private final List<MangaModel> mangaModels = new ArrayList<>();
    private MangaFilter mangaFilterDto;
    private ViewGroup container;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.search_fragment, container, false);

        this.container = container;

        gridView = view1.findViewById(R.id.gridView);
        searchView = view1.findViewById(R.id.search_view);

        view1.findViewById(R.id.viewAccount).setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(container.getContext(), v);

            popupMenu.setForceShowIcon(true);

            Menu menu = popupMenu.getMenu();

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, menu);

            MenuItem logInItem = menu.findItem(R.id.action_sign_in);
            MenuItem updateItem = menu.findItem(R.id.action_update_user);
            MenuItem resetItem = menu.findItem(R.id.action_reset_password);
            MenuItem logoutItem = menu.findItem(R.id.action_logout);

            if (userIsAuthenticated()) {
                logInItem.setVisible(false);
                updateItem.setVisible(true);
                logoutItem.setVisible(true);
                resetItem.setVisible(false);
            } else {
                logInItem.setVisible(true);
                resetItem.setVisible(true);
                logoutItem.setVisible(false);
                updateItem.setVisible(false);
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_sign_in:
                        // log-out
                        openSignInActivity(container);

                        return true;
                    case R.id.action_logout:
                        // log-out
                        logout(container);

                        return true;
                    case R.id.action_update_user:
                        // Open the Update User activity
                        openUpdateUserActivity(container);

                        return true;
                    case R.id.action_reset_password:
                        // Open the Reset Password activity
                        openResetPasswordActivity(container);

                        return true;
                    default:
                        return false;
                }
            });

            // Show the popup menu
            popupMenu.show();
        });

        ImageButton mangaFilter = view1.findViewById(R.id.mangaFilter);

        mangaFilter.setOnClickListener(view -> openModal());

        mangaFilterDto = new MangaFilter("", EMangaStatus.getAll(), EMangaGenre.getAll(), EMangaBookmark.getAll());

        swipeRefreshLayout = view1.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);

            mangaModels.clear();
            pageNumber = 0;

            loadData(container, searchView.getQuery().toString());
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mangaModels.clear();
                pageNumber = 0;

                loadData(container, query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
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

        return view1;
    }

    private void logout(ViewGroup container) {
        Context context = container.getContext();

        // Perform the logout logic, such as clearing session data or revoking access tokens
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Get the editor to make changes to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save the token to SharedPreferences
        editor.putString("token", null);
        Utils.setUserToken(null);

        editor.apply();

        swipeRefreshLayout.setRefreshing(true);

        mangaFilterDto = new MangaFilter("", EMangaStatus.getAll(), EMangaGenre.getAll());

        mangaModels.clear();
        pageNumber = 0;

        loadData(container, searchView.getQuery().toString());
    }

    private void openSignInActivity(ViewGroup container) {
        Intent intent = new Intent(container.getContext(), SignInActivity.class);
        startActivity(intent);
    }

    private void openUpdateUserActivity(ViewGroup container) {
        Intent intent = new Intent(container.getContext(), UpdateProfileActivity.class);
        startActivity(intent);
    }

    private void openResetPasswordActivity(ViewGroup container) {
        Intent intent = new Intent(container.getContext(), ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void openModal() {
        MangaFilterFragment bottomSheetFragment = new MangaFilterFragment(mangaFilterDto);

        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }

    private void loadData(ViewGroup container, String searchView) {
        RequestManager requestManager = new RequestManager(container.getContext());

        swipeRefreshLayout.setRefreshing(true);
        requestManager.getMangaList(listener, searchView, mangaFilterDto, pageNumber, pageSize);
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

    @Override
    public void onFilterApplied(MangaFilter selectedFilters) {
        mangaModels.clear();
        pageNumber = 0;

        mangaFilterDto = selectedFilters;

        loadData(container, searchView.getQuery().toString());
    }
}