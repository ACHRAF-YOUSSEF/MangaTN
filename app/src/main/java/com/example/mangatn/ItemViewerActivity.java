package com.example.mangatn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mangatn.adapters.ChaptersAdapter;
import com.example.mangatn.models.ChapterModel;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemViewerActivity extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ArrayList<ChapterModel> chapterModels;
    private ChaptersAdapter chaptersAdapter;
    private String mangaId;
    private final boolean added = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        ImageView coverImage = findViewById(R.id.coverImage);
        TextView titleDetail = findViewById(R.id.title_detail);
        ListView chaptersListView = findViewById(R.id.chaptersListView);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        mangaId = intent.getStringExtra("mangaId");

        titleDetail.setText(title);
        Glide.with(this).load(intent.getStringExtra("cover")).into(coverImage);
        chapterModels = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            arrayList = getMangaChapters("https://kissmanga.org/manga/" + mangaId);

            runOnUiThread(() -> {
                if (!arrayList.isEmpty()) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        String chapterTitle = getMangaChapterTitle(arrayList, i);
                        chapterModels.add(new ChapterModel(chapterTitle, getMangaChapter(arrayList, i)));
                    }

                    chaptersAdapter.notifyDataSetChanged();
                }
            });
        });

        executorService.shutdown();

        chaptersAdapter = new ChaptersAdapter(this, chapterModels);
        chaptersListView.setAdapter(chaptersAdapter);
        chaptersListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent1 = new Intent(this, MangaChapterViewerActivity.class);

            intent1.putExtra("added", added);
            intent1.putExtra("title", title);
            intent1.putExtra("mangaId", mangaId);
            intent1.putExtra("mangaChapter", chapterModels.get(position));

            startActivity(intent1);
        });
    }

    private ArrayList<String> getMangaChapters(String url) {
        try {
            return formattedResultIntoAList(Jsoup
                    .connect(url)
                    .userAgent("Mozilla")
                    .get()
                    .body()
                    .select("a[href*=/chapter/]")
                    .outerHtml()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> formattedResultIntoAList(String elements) {
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < elements.length() - 2; i++) {
            if (elements.charAt(i) == '<' && elements.charAt(i + 1) == 'a') {
                int j = i;

                while (true) {
                    if (j >= elements.length() - 2) {
                        break;
                    } else if (elements.charAt(j) != '<' || elements.charAt(j + 1) != '/') {
                        j++;
                    } else {
                        arrayList.add(elements.substring(i, j + 4));
                        break;
                    }
                }
            }
        }

        return arrayList;
    }

    private String getMangaChapterTitle(ArrayList<String> arrayList, int index) {
        StringBuilder temp = new StringBuilder(arrayList.get(index));

        String temp2 = temp.substring(0, temp.indexOf("</a>"));

        return temp2.substring(temp2.indexOf(">") + 1);
    }

    private String getMangaChapter(ArrayList<String> arrayList, int index) {
        String temp = arrayList.get(index);

        int i = temp.indexOf("/chapter-");

        String temp2 = temp.substring(i + 1);

        int i2 = temp2.indexOf(34);

        return temp2.substring(0, i2);
    }
}