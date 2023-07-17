package com.example.mangatn.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.adapters.ChaptersAdapter;
import com.example.mangatn.interfaces.OnBookmarkListener;
import com.example.mangatn.interfaces.OnCheckForBookmarkListener;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.interfaces.OnFetchUpdateListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.Bookmark;
import com.example.mangatn.models.ChapterModel;
import com.example.mangatn.models.MangaModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemViewerActivity extends AppCompatActivity {
    private ChaptersAdapter chaptersAdapter;
    private MangaModel mangaModel;
    private List<ChapterModel> chaptersList;
    private boolean added = false;
    private boolean bookmarked = false;
    private RequestManager requestManager;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mangaId;
    private ImageButton bookmark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        getSupportActionBar().hide();

        mangaId = getIntent().getStringExtra("mangaId");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching manga chapters");
        dialog.show();

        requestManager = new RequestManager(this);
        requestManager.getManga(listener, mangaId);

        swipeRefreshLayout = findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            requestManager.updateManga(listener1, mangaId);
        });

        bookmark = findViewById(R.id.bookmark);

        //  api call to check if this manga is bookmarked or not
        requestManager.checkForBookmark(listener2, mangaId);

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

    private final OnFetchSingleDataListener listener = new OnFetchSingleDataListener() {
        @Override
        public void onFetchData(MangaModel manga, String message, Context context) {
            if (manga == null) {
                Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();
            } else {
                mangaModel = manga;
                chaptersList = mangaModel.getChapters();

                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

                showChapters(chaptersList);
            }
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!", Toast.LENGTH_SHORT).show();
        }
    };

    private final OnFetchUpdateListener<ApiResponse> listener1 = new OnFetchUpdateListener<ApiResponse>() {
        @Override
        public void onFetchData(ApiResponse apiResponse, String message, Context context) {
            if (apiResponse.getMessage().equals("Manga already up to date!")) {
                Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                requestManager.getManga(listener, mangaId);

                Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void showChapters(List<ChapterModel> chapters) {
        ListView chaptersListView = findViewById(R.id.chaptersListView);
        ImageView coverImage = findViewById(R.id.coverImage);
        TextView titleDetail = findViewById(R.id.title_detail);

        titleDetail.setText(mangaModel.getTitle());
        Picasso.get().load(mangaModel.getCoverImgPath()).into(coverImage);

        chaptersAdapter = new ChaptersAdapter(this, chapters);
        chaptersListView.setAdapter(chaptersAdapter);
        chaptersListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent1 = new Intent(this, MangaChapterViewerActivity.class);

            intent1.putExtra("added", added);
            intent1.putExtra("data", chapters.get(position));

            startActivity(intent1);
        });
    }
}