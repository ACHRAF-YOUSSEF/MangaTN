package com.example.mangatn.fragments;

import static com.example.mangatn.Utils.getUserToken;
import static com.example.mangatn.Utils.userIsAuthenticated;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.activities.ItemViewerActivity;
import com.example.mangatn.activities.MainActivity;
import com.example.mangatn.activities.ResetPasswordActivity;
import com.example.mangatn.activities.SignInActivity;
import com.example.mangatn.activities.UpdateProfileActivity;
import com.example.mangatn.adapters.GridAdapter;
import com.example.mangatn.interfaces.bookmark.OnFetchBookmarkedMangasListener;
import com.example.mangatn.interfaces.manga.SelectListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.manga.MangaModel;

import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment implements SelectListener, OnFetchBookmarkedMangasListener {
    public GridView gridView;
    private View toolbar;
    private GridAdapter gridAdapter;
    private TextView textView, textView2;
    private RequestManager requestManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int pageSize = 5;
    private int pageNumber = 0;
    private final List<MangaModel> bookmarks = new ArrayList<>();

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
        toolbar = view1.findViewById(R.id.toolbar);

        requestManager = new RequestManager(container.getContext());

        gridAdapter = new GridAdapter(container.getContext(), bookmarks, this);
        gridView.setAdapter(gridAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!swipeRefreshLayout.isRefreshing() && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    updateData();
                }
            }
        });

        textView.setOnClickListener(v -> openSignInActivity(container));

        toolbar.findViewById(R.id.viewAccount).setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(container.getContext(), v);

            popupMenu.setForceShowIcon(true);

            Menu menu = popupMenu.getMenu();

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, menu);

            MenuItem updateItem = menu.findItem(R.id.action_update_user);
            MenuItem logoutItem = menu.findItem(R.id.action_logout);

            Log.i("getUserToken", "onCreateView: " + getUserToken());

            if (userIsAuthenticated()) {
                updateItem.setVisible(true);
                logoutItem.setVisible(true);
            } else {
                logoutItem.setVisible(false);
                updateItem.setVisible(false);
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.action_logout:
                        // Open the Update User activity
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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            pageNumber = 0;
            bookmarks.clear();

            updateData();
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

        updateData();
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

    public void updateData() {
        Log.i("token:", "onCreateView: " + getUserToken());

        if (userIsAuthenticated()) {
            // api call to get the bookmarked mangas
            swipeRefreshLayout.setRefreshing(true);
            requestManager.fetchBookmarked(this, pageNumber, pageSize);
        } else {
            textView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showManga(List<MangaModel> list) {
        if (!list.isEmpty()) {
            bookmarks.addAll(list);

            gridAdapter.notifyDataSetChanged();
            pageNumber++;
        }
    }

    @Override
    public void OnMangaClicked(MangaModel manga, Context context) {
        startActivity(
                new Intent(context, ItemViewerActivity.class)
                        .putExtra("mangaId", manga.getMangaId())
        );
    }

    @Override
    public void onFetchSuccess(List<MangaModel> bookmarkedMangas, String message, Context context) {
        if (bookmarkedMangas.isEmpty() &&  (pageNumber * pageSize < bookmarks.size())) {
            Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();

            textView.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            textView2.setVisibility(View.VISIBLE);
        } else {
            showManga(bookmarkedMangas);

            textView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFetchError(String error, Context context) {
        Toast.makeText(context, "An Error Occurred!!!: " + error, Toast.LENGTH_SHORT).show();
    }
}