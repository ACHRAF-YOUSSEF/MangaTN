package com.example.mangatn.activities.manga;

import static com.example.mangatn.Utils.userIsAuthenticated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mangatn.R;
import com.example.mangatn.activities.auth.SignInActivity;
import com.example.mangatn.adapters.MyPagerAdapter;
import com.example.mangatn.fragments.home.TabFragment;
import com.example.mangatn.interfaces.bookmark.OnBookmarkListener;
import com.example.mangatn.interfaces.bookmark.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.chapter.OnGetHasReadChapterListener;
import com.example.mangatn.interfaces.chapter.OnGetReadChapterListener;
import com.example.mangatn.interfaces.manga.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.update.OnFetchUpdateListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.bookmark.BookmarkModel;
import com.example.mangatn.models.chapter.ReadChapterModel;
import com.example.mangatn.models.manga.MangaModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

public class ItemViewerActivity extends AppCompatActivity {
    private MangaModel mangaModel;
    private boolean bookmarked = false;
    private RequestManager requestManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mangaId;
    private ImageButton bookmark, expanded_icon;
    private ImageView status_icon;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton floatingActionButton;
    private MyPagerAdapter pagerAdapter;
    private TextView collapsed_summary_detail, expanded_summary_detail, authors, status_details;
    private LinearLayout collapsedContent, expandedContent;
    private Integer lastViewedChapterId = null;
    private MenuItem bookmarkedIcon;
    private MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        collapsed_summary_detail = findViewById(R.id.collapsed_summary_detail);
        expanded_summary_detail = findViewById(R.id.expanded_summary_detail);
        authors = findViewById(R.id.authors_detail);

        expanded_icon = findViewById(R.id.expanded_icon);

        status_details = findViewById(R.id.status_details);
        status_icon = findViewById(R.id.status_icon);

        MaterialCardView cardView = findViewById(R.id.cardView);
        collapsedContent = findViewById(R.id.collapsedContent);
        expandedContent = findViewById(R.id.expandedContent);

        expanded_icon.setOnClickListener(v -> toggleCardViewContent());
        cardView.setOnClickListener(v -> toggleCardViewContent());

        tabLayout = findViewById(R.id.view_tabLayout);
        viewPager = findViewById(R.id.view_viewPager);
        floatingActionButton = findViewById(R.id.continueLastViewedChapterButton);

        mangaModel = new MangaModel();
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());

        mangaId = getIntent().getStringExtra("mangaId");

        requestManager = new RequestManager(this);
        requestManager.getManga(listener4, mangaId);

        swipeRefreshLayout = findViewById(R.id.refresh_item_view);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            requestManager.updateManga(listener1, mangaId);
        });

        bookmark = findViewById(R.id.bookmarkModel);

        materialToolbar = findViewById(R.id.topAppBar);

        materialToolbar.setNavigationOnClickListener(v -> finish());

        bookmarkedIcon = materialToolbar.getMenu().findItem(R.id.bookmarked);

        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.bookmarked) {
                addOrDeleteBookmark();

                return true;
            }

            return false;
        });

        //  api call to check if this manga is bookmarked or not
        if (userIsAuthenticated()) {
            requestManager.checkForBookmark(listener2, mangaId);
        }

        bookmark.setOnClickListener(v -> addOrDeleteBookmark());

        getLastViewedChapterOrFirstChapter();

        floatingActionButton.setOnClickListener(v -> {
            if (lastViewedChapterId != null) openChapter(lastViewedChapterId);
        });
    }

    private void addOrDeleteBookmark() {
        if (userIsAuthenticated()) {
            bookmarked = !bookmarked;
            requestManager.bookmark(listener3, new BookmarkModel(mangaId, bookmarked));
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    private void getLastViewedChapterOrFirstChapter() {
        lastViewedChapterId = null;

        requestManager.getHasReadChapter(new OnGetHasReadChapterListener() {
            @Override
            public void onFetchData(Boolean response, String message, Context context) {
                if (response) {
                    floatingActionButton.setText(R.string.Resume);
                } else {
                    floatingActionButton.setText(R.string.Start);
                }
            }

            @Override
            public void onError(String message, Context context) {
                floatingActionButton.setText(R.string.Start);
            }
        }, mangaId);

        // api call to fetch the last viewed + in progress chapter
        if (userIsAuthenticated()) {
            requestManager.getLastReadAndInProgressChapter(new OnGetReadChapterListener() {
                @Override
                public void onFetchData(ReadChapterModel response, String message, Context context) {
                    lastViewedChapterId = response.getChapter().getReference();
                }

                @Override
                public void onError(String message, Context context) {

                }
            }, mangaId);
        } else {
            requestManager.getFirstChapter(new OnGetReadChapterListener() {
                @Override
                public void onFetchData(ReadChapterModel response, String message, Context context) {
                    lastViewedChapterId = response.getChapter().getReference();
                }

                @Override
                public void onError(String message, Context context) {

                }
            }, mangaId);
        }
    }

    private void toggleCardViewContent() {
        if (expandedContent.getVisibility() == View.VISIBLE) {
            expanded_icon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            collapsedContent.setVisibility(View.VISIBLE);
            expandedContent.setVisibility(View.GONE);
        } else {
            expanded_icon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            collapsedContent.setVisibility(View.GONE);
            expandedContent.setVisibility(View.VISIBLE);
        }
    }

    private void openChapter(Integer reference) {
        startActivity(
                MangaChapterViewerActivity
                        .newIntent(this, mangaId, reference)
        );
    }

    private void switchBookmark() {
        if (bookmarked) {
            bookmark.setImageResource(R.drawable.baseline_bookmark_24);
            bookmarkedIcon.setIcon(R.drawable.baseline_bookmark_24);
        } else {
            bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
            bookmarkedIcon.setIcon(R.drawable.baseline_bookmark_border_24);
        }
    }

    private final OnFetchSingleDataListener listener4 = new OnFetchSingleDataListener() {
        @Override
        public void onFetchData(MangaModel manga, String message, Context context) {
            mangaModel = manga;

            if (userIsAuthenticated()) {
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
        String title;
        Bundle bundle = new Bundle();
        bundle.putString("mangaId", mangaId);

        if (pagerAdapter.getItemCount() <= 0) {
            for (int i = 0; i <= (count / 50); i++) {
                int from = i * 50 + 1;
                int to = i * 50 + 50;

                if (to > count) to = count;

                title = String.format("%d-%d", from, to);

                Fragment fragment = new TabFragment();
                pagerAdapter.addFragment(fragment, title);
                fragment.setArguments(bundle);
            }
        } else {
            for (int i = pagerAdapter.getItemCount(); i <= (count / 50); i++) {
                int from = i * 50 + 1;
                int to = i * 50 + 50;

                if (to > count) {
                    to = count;
                }

                title = String.format("%d-%d", from, to);

                Fragment fragment = new TabFragment();
                pagerAdapter.addFragment(fragment, title);
                fragment.setArguments(bundle);
            }
        }

        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(pagerAdapter.getFragmentTitleList().get(position))
        ).attach();

        ImageView coverImage = findViewById(R.id.coverImage);
        TextView titleDetail = findViewById(R.id.title_detail);

        materialToolbar.setTitle(mangaModel.getTitle());
        titleDetail.setText(mangaModel.getTitle());
        Picasso.get().load(mangaModel.getCoverImgPath()).into(coverImage);

        String summaryText = mangaModel.getSummary();

        if (summaryText.length() >= 100) {
            expanded_summary_detail.setText(summaryText);
            collapsed_summary_detail.setText(String.format("%s...", summaryText.substring(0, 100)));
        } else {
            expanded_summary_detail.setText(summaryText);
            collapsed_summary_detail.setText(summaryText);
        }

        EMangaStatus status = mangaModel.getStatus();

        if (status != null) {
            findViewById(R.id.manga_status).setVisibility(View.VISIBLE);

            if (status.equals(EMangaStatus.COMPLETED)) {
                status_icon.setImageResource(R.drawable.outline_check_circle_24);
            } else {
                status_icon.setImageResource(R.drawable.outline_access_time_24);
            }

            status_details.setText(EMangaStatus.getName(status));
        } else {
            findViewById(R.id.manga_status).setVisibility(View.GONE);
        }

        authors.setText(mangaModel.getAuthors());

        LinearLayout chipGroup = findViewById(R.id.chip_manga_genre_group);

        for (String genre : mangaModel.getGenres()) {
            Chip newChip = new Chip(ItemViewerActivity.this);

            newChip.setText(
                    String.format("%s%s",
                            genre.charAt(0),
                            genre.substring(1)
                                    .toLowerCase()
                                    .replaceAll("_", "")
                                    .replaceAll("manga", "")
                    )
            );
            newChip.setTextSize(20);
            newChip.setChipBackgroundColorResource(R.color.grey);
            newChip.setTextColor(Color.WHITE);

            // Create LayoutParams for the chip
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // Set margins for the chip (adjust values as needed)
            layoutParams.setMargins(10, 0, 10, 10);

            // Apply the LayoutParams to the chip
            newChip.setLayoutParams(layoutParams);

            chipGroup.addView(newChip);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userIsAuthenticated()) {
            requestManager.checkForBookmark(listener2, mangaId);
        }

        getLastViewedChapterOrFirstChapter();
    }
}