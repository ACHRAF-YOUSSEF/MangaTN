package com.example.mangatn.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mangatn.R;
import com.example.mangatn.adapters.ChaptersAdapter;
import com.example.mangatn.interfaces.OnFetchSingleDataListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ChapterModel;
import com.example.mangatn.models.MangaModel;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemViewerActivity extends AppCompatActivity {
    private ChaptersAdapter chaptersAdapter;
    private MangaModel mangaModel;
    private final boolean added = false;
    private RequestManager requestManager;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        getSupportActionBar().hide();

        String mangaId = getIntent().getStringExtra("mangaId");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching manga chapters");
        dialog.show();

        requestManager = new RequestManager(this);
        requestManager.getManga(listener, mangaId);
    }

    private final OnFetchSingleDataListener listener = new OnFetchSingleDataListener() {
        @Override
        public void onFetchData(MangaModel manga, String message, Context context) {
            if (manga == null) {
                Toast.makeText(context, "No data found!!!", Toast.LENGTH_SHORT).show();
            } else {
                mangaModel = manga;
                dialog.dismiss();

                showChapters(manga.getChapters());
            }
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, "An Error Occurred!!!", Toast.LENGTH_SHORT).show();
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