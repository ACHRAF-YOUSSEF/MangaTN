package com.example.mangatn.activities;

import static com.example.mangatn.Utils.getUserToken;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.mangatn.interfaces.bookmark.OnBookmarkListener;
import com.example.mangatn.interfaces.bookmark.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.manga.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.update.OnFetchUpdateListener;
import com.example.mangatn.interfaces.chapter.OnGetReadChapterListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.bookmark.BookmarkModel;
import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.manga.MangaModel;
import com.example.mangatn.models.chapter.ReadChapterModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
    private ViewPager viewPager;
    private ExtendedFloatingActionButton floatingActionButton;
    private MyPagerAdapter pagerAdapter;
    private TextView collapsed_summary_detail, expanded_summary_detail, authors, status_details;
    private boolean isExpanded = false;
    private LinearLayout collapsedContent;
    private CardView cardView;
    private ScrollView expandedContentScrollView;
    private LinearLayout chipGroup;

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

        bookmark = findViewById(R.id.bookmarkModel);

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
                    requestManager.bookmark(listener3, new BookmarkModel(mangaId, bookmarked));
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
        tabLayout.setupWithViewPager(viewPager);

        ImageView coverImage = findViewById(R.id.coverImage);
        TextView titleDetail = findViewById(R.id.title_detail);

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

        chipGroup = findViewById(R.id.chip_manga_genre_group);

        for (String genre: mangaModel.getGenres()) {
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

        if (getUserToken() != null) {
            if (!getUserToken().isEmpty()) {
                requestManager.checkForBookmark(listener2, mangaId);
            }
        }
    }
}