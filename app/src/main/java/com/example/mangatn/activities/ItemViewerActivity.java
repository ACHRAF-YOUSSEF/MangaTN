package com.example.mangatn.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.adapters.MyPagerAdapter;
import com.example.mangatn.fragments.TabFragment;
import com.example.mangatn.interfaces.OnBookmarkListener;
import com.example.mangatn.interfaces.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.OnFetchUpdateListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.Bookmark;
import com.example.mangatn.models.MangaModel;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

public class ItemViewerActivity extends AppCompatActivity {
    private MangaModel mangaModel;
    private boolean bookmarked = false;
    private RequestManager requestManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mangaId;
    private ImageButton bookmark;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        tabLayout = findViewById(R.id.view_tabLayout);
        viewPager = findViewById(R.id.view_viewPager);

        getSupportActionBar().hide();

        mangaModel = new MangaModel();
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mangaId = getIntent().getStringExtra("mangaId");

        requestManager = new RequestManager(this);
        requestManager.getManga(listener4, mangaId);

        swipeRefreshLayout = findViewById(R.id.refresh_item_view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            requestManager.updateManga(listener1, mangaId);
        });

        bookmark = findViewById(R.id.bookmark);

        //  api call to check if this manga is bookmarked or not
        if (!Utils.getUserToken().isEmpty()) {
            requestManager.checkForBookmark(listener2, mangaId);
        }

        bookmark.setOnClickListener(v -> {
            if (!Utils.getUserToken().isEmpty()) {
                bookmarked = !bookmarked;
                requestManager.bookmark(listener3, new Bookmark(mangaId, bookmarked));
            } else {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void switchBookmark() {
        if (bookmarked) {
            bookmark.setImageResource(R.drawable.baseline_bookmark_24);
        } else {
            bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
        }
    }

    private final OnFetchSingleDataListener listener4 = new OnFetchSingleDataListener() {
        @Override
        public void onFetchData(MangaModel manga, String message, Context context) {
            mangaModel = manga;

            if (!Utils.getUserToken().isEmpty()) {
                requestManager.checkForBookmark(listener2, mangaId);
            }

            showChapters(mangaModel.getCount());
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private final OnBookmarkListener listener3 = new OnBookmarkListener() {
        @Override
        public void onFetchData(ApiResponse response, String message, Context context) {
            switchBookmark();
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!: " + message, Toast.LENGTH_SHORT).show();
        }
    };

    private final OnCheckForBookmarkListener listener2 = new OnCheckForBookmarkListener() {
        @Override
        public void onFetchData(Boolean response, String message, Context context) {
            bookmarked = response;

            switchBookmark();
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!: " + message, Toast.LENGTH_SHORT).show();
        }
    };

    private final OnFetchUpdateListener<ApiResponse> listener1 = new OnFetchUpdateListener<ApiResponse>() {
        @Override
        public void onFetchData(ApiResponse apiResponse, String message, Context context) {
            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

            requestManager.getManga(listener4, mangaId);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void showChapters(int count) {
        Log.i("chapters", "showChapters: " + count);
        Log.i("chapters", "showChapters: " + count / 50);

        String title;
        Bundle bundle = new Bundle();
        bundle.putString("mangaId", mangaId);

        if (pagerAdapter.getCount() <= 0) {
            for (int i = 0; i <= (int) (count / 50); i++) {
                int from = i * 50 + 1;
                int to = i * 50 + 50;

                if (to > count) to = count;

                title = String.format("%d-%d", from, to);

                Fragment fragment = new TabFragment();
                pagerAdapter.addFragment(fragment, title);
                fragment.setArguments(bundle);
            }
        } else {
            for (int i = pagerAdapter.getCount(); i <= (int) (count / 50); i++) {
                int from = i * 50 + 1;
                int to = i * 50 + 50;

                if (to > count) to = count;

                title = String.format("%d-%d", from, to);

                Fragment fragment = new TabFragment();
                pagerAdapter.addFragment(fragment, title);
                fragment.setArguments(bundle);
            }
        }

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        ImageView coverImage = findViewById(R.id.coverImage);
        TextView titleDetail = findViewById(R.id.title_detail);

        titleDetail.setText(mangaModel.getTitle());
        Picasso.get().load(mangaModel.getCoverImgPath()).into(coverImage);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Utils.getUserToken().isEmpty()) {
            requestManager.checkForBookmark(listener2, mangaId);
        }
    }
}