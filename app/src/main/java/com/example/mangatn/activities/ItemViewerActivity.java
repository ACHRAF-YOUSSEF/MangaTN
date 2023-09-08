package com.example.mangatn.activities;

import static com.example.mangatn.Utils.getUserToken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.mangatn.R;
import com.example.mangatn.adapters.MyPagerAdapter;
import com.example.mangatn.fragments.TabFragment;
import com.example.mangatn.interfaces.OnBookmarkListener;
import com.example.mangatn.interfaces.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.OnFetchUpdateListener;
import com.example.mangatn.interfaces.OnGetReadChapterListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.Bookmark;
import com.example.mangatn.models.MangaModel;
import com.example.mangatn.models.ReadChapterModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

public class ItemViewerActivity extends AppCompatActivity {
    private MangaModel mangaModel;
    private boolean bookmarked = false;
    private RequestManager requestManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mangaId;
    private ImageButton bookmark, expanded_icon;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private MyPagerAdapter pagerAdapter;
    private TextView collapsed_summary_detail, expanded_summary_detail, authors;
    private boolean isExpanded = false;
    private LinearLayout collapsedContent;
    private CardView cardView;
    private ScrollView expandedContentScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        collapsed_summary_detail = findViewById(R.id.collapsed_summary_detail);
        expanded_summary_detail = findViewById(R.id.expanded_summary_detail);
        authors = findViewById(R.id.authors_detail);

        expanded_icon = findViewById(R.id.expanded_icon);

        cardView = findViewById(R.id.cardView);
        collapsedContent = findViewById(R.id.collapsedContent);
        expandedContentScrollView = findViewById(R.id.expandedContentScrollView);

        expanded_icon.setOnClickListener(v -> {
            toggleCardViewContent();
        });

        cardView.setOnClickListener(v -> {
            toggleCardViewContent();
        });

        tabLayout = findViewById(R.id.view_tabLayout);
        viewPager = findViewById(R.id.view_viewPager);
        floatingActionButton = findViewById(R.id.continueLastViewedChapterButton);

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
        if (getUserToken() != null) {
            if (!getUserToken().isEmpty()) {
                requestManager.checkForBookmark(listener2, mangaId);
            }
        }

        bookmark.setOnClickListener(v -> {
            if (getUserToken() != null) {
                if (!getUserToken().isEmpty()) {
                    bookmarked = !bookmarked;
                    requestManager.bookmark(listener3, new Bookmark(mangaId, bookmarked));
                }
            } else {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
            }
        });

        floatingActionButton.setOnClickListener(v -> {
            // api call to fetch the last viewed + in progress chapter
            if (getUserToken() != null) {
                if (!getUserToken().isEmpty()) {
                    requestManager.getLastReadAndInProgressChapter(new OnGetReadChapterListener() {
                        @Override
                        public void onFetchData(ReadChapterModel response, String message, Context context) {
                            openChapter(response.getChapter().getReference());
                        }

                        @Override
                        public void onError(String message, Context context) {

                        }
                    }, mangaId);
                } else {
                    requestManager.getFirstChapter(new OnGetReadChapterListener() {
                        @Override
                        public void onFetchData(ReadChapterModel response, String message, Context context) {
                            openChapter(response.getChapter().getReference());
                        }

                        @Override
                        public void onError(String message, Context context) {

                        }
                    }, mangaId);
                }
            } else {
                requestManager.getFirstChapter(new OnGetReadChapterListener() {
                    @Override
                    public void onFetchData(ReadChapterModel response, String message, Context context) {
                        openChapter(response.getChapter().getReference());
                    }

                    @Override
                    public void onError(String message, Context context) {

                    }
                }, mangaId);
            }
        });
    }

    private void toggleCardViewContent() {
        if (isExpanded) {
            expanded_icon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            expandedContentScrollView.setVisibility(View.GONE);
            collapsedContent.setVisibility(View.VISIBLE);
        } else {
            expanded_icon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            collapsedContent.setVisibility(View.GONE);
            expandedContentScrollView.setVisibility(View.VISIBLE);
        }

        isExpanded = !isExpanded;
    }

    private void openChapter(Integer reference) {
        Intent intent1 = new Intent(ItemViewerActivity.this, MangaChapterViewerActivity.class);

        intent1.putExtra("added", false);
        intent1.putExtra("chapterReference", reference);
        intent1.putExtra("mangaId", mangaId);

        startActivity(intent1);
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

           if (getUserToken() != null) {
               if (!getUserToken().isEmpty()) {
                   requestManager.checkForBookmark(listener2, mangaId);
               }
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

        expanded_summary_detail.setText(mangaModel.getSummary());
        collapsed_summary_detail.setText(String.format("%s...", mangaModel.getSummary().substring(0, 180)));
        authors.setText(mangaModel.getAuthors());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getUserToken() != null) {
            if (!getUserToken().isEmpty()) {
                requestManager.checkForBookmark(listener2, mangaId);
            }
        }
    }
}