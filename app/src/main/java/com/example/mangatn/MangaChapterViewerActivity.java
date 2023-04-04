package com.example.mangatn;

import static com.example.mangatn.MainActivity.WEBSITE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.mangatn.models.ChapterModel;
import com.github.chrisbanes.photoview.PhotoView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MangaChapterViewerActivity extends AppCompatActivity implements View.OnClickListener {
    private String url = WEBSITE_URL + "/chapter/";
    private ArrayList<String> mangaChapterImagesUrls, arrayList, arrayList1;
    private PhotoView imageView;
    private SeekBar seekBar;
    private TextView chapterStart, chapterEnd;
    private ImageButton bookmark;
    private ToggleButton LTR;
    int index = 0;
    private boolean active = true;
    private boolean LeftToRight = false;
    private boolean added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_chapter_viewer);

        //
        ImageButton back = findViewById(R.id.btnBack);
        bookmark = findViewById(R.id.saveChapter);
        TextView title = findViewById(R.id.title);
        LTR = findViewById(R.id.LeftToRightBtn);
        imageView = findViewById(R.id.imageView);

        //
        init();

        Intent intent = getIntent();
        ChapterModel chapterModel = (ChapterModel) intent.getSerializableExtra("mangaChapter");
        String mangaId = intent.getStringExtra("mangaId");
        String titleText = intent.getStringExtra("title");
        added = intent.getBooleanExtra("added", false);

        if (!added) {
            bookmark.setImageResource(R.drawable.baseline_save_alt_24);
        } else {
            bookmark.setImageResource(R.drawable.baseline_offline_pin_24);
        }

        //
        title.setText(titleText .concat(":\n").concat(chapterModel.getChapter()));

        url += mangaId + "/"+ chapterModel.getNb();

        mangaChapterImagesUrls = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            arrayList = getMangaChapterImages(url);
            arrayList1 = new ArrayList<>();

            for (int j = 0; j < arrayList.size() ;j++) {
                StringBuilder sb = new StringBuilder(arrayList.get(j));

                sb = new StringBuilder(sb.substring(sb.indexOf("\"") + 1));

                arrayList1.add(sb.substring(0, sb.indexOf("\"")));
            }

            runOnUiThread(() -> {
                if (!arrayList1.isEmpty()) {
                    mangaChapterImagesUrls = arrayList1;

                    chapterStart.setText("1");
                    chapterEnd.setText(String.valueOf(mangaChapterImagesUrls.size()));
                    seekBar.setMax(mangaChapterImagesUrls.size() - 1);

                    Glide.with(MangaChapterViewerActivity.this).load(mangaChapterImagesUrls.get(index)).into(imageView);
                } else {
                    Toast.makeText(MangaChapterViewerActivity.this, "error!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // kill the process after it finishes!
        executorService.shutdown();

        //
        LTR.setOnClickListener(this);
        bookmark.setOnClickListener(v -> {
            added = !added;

            if (!added) {
                bookmark.setImageResource(R.drawable.baseline_save_alt_24);
            } else {
                bookmark.setImageResource(R.drawable.baseline_offline_pin_24);
            }

            Toast.makeText(this, "not implemented yet!!", Toast.LENGTH_SHORT).show();
        });
        back.setOnClickListener(v -> finish());
        imageView.setOnClickListener(v -> {
            if (!active) {
                if (!LeftToRight) {
                    findViewById(R.id.progressRightToLeft).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.progressLeftToRight).setVisibility(View.VISIBLE);
                }

                findViewById(R.id.optionBar).setVisibility(View.VISIBLE);
            } else {
                if (!LeftToRight) {
                    findViewById(R.id.progressRightToLeft).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.progressLeftToRight).setVisibility(View.GONE);
                }

                findViewById(R.id.optionBar).setVisibility(View.GONE);
            }

            active = !active;
        });

        updateSeekBar();
    }

    private ArrayList<String> getMangaChapterImages(String url) {
        Document document;

        String elements;

        try {
            document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla")
                    .get();

            elements = Objects
                    .requireNonNull(document.getElementById("centerDivVideo"))
                    .html();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> arrayList = new ArrayList<>();

        for (int i =0; i< elements.length() - 1; i++) {
            if (elements.charAt(i) == '<' && elements.charAt(i + 1) == 'i') {
                for (int j = i; j < elements.length(); j++) {
                    if (elements.charAt(j) == '>') {
                        StringBuilder sb = new StringBuilder();

                        for (int k = i; k < j + 1; k ++) {
                            sb.append(elements.charAt(k));
                        }

                        arrayList.add(sb.toString());
                        break;
                    }
                }
            }
        }

        return arrayList;
    }

    private void rightSwipe() {
        --index;

        if (index < 0) {
            index = 0;
        }

        if (!mangaChapterImagesUrls.isEmpty()) {
            chapterStart.setText(String.valueOf(index + 1));
            seekBar.setProgress(index);

            Glide.with(this).load(mangaChapterImagesUrls.get(index)).into(imageView);
        }
    }

    private void leftSwipe() {
        ++index;

        if (index == mangaChapterImagesUrls.size()) {
            index = mangaChapterImagesUrls.size() - 1;
        }

        if (!mangaChapterImagesUrls.isEmpty()) {
            chapterStart.setText(String.valueOf(index + 1));
            seekBar.setProgress(index);

            Glide.with(this).load(mangaChapterImagesUrls.get(index)).into(imageView);
        }
    }

    private void init() {
        if (!LeftToRight) {
            LTR.setChecked(false);
            findViewById(R.id.progressRightToLeft).setVisibility(View.VISIBLE);
            findViewById(R.id.progressLeftToRight).setVisibility(View.GONE);
            seekBar = findViewById(R.id.seekBarRightToLeft);
            chapterStart = findViewById(R.id.chapterStartRightToLeft);
            chapterEnd = findViewById(R.id.chapterEndRightToLeft);
        } else {
            LTR.setChecked(true);
            findViewById(R.id.progressLeftToRight).setVisibility(View.VISIBLE);
            findViewById(R.id.progressRightToLeft).setVisibility(View.GONE);
            seekBar = findViewById(R.id.seekBarLeftToRight);
            chapterStart = findViewById(R.id.chapterStartLeftToRight);
            chapterEnd = findViewById(R.id.chapterEndLeftToRight);
        }
    }

    private void updateSeekBar() {
        int px = seekBar.getProgress();

        init();

        chapterStart.setText(String.valueOf(px + 1));
        chapterEnd.setText(String.valueOf(mangaChapterImagesUrls.size()));

        seekBar.setMax(mangaChapterImagesUrls.size() - 1);
        seekBar.setProgress(px);

        imageView.setOnSingleFlingListener((e1, e2, velocityX, velocityY) -> {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > 150 && Math.abs(velocityX) > 150) {
                    if (LeftToRight) {
                        if (diffX > 0) {
                            // Swipe right
                            rightSwipe();
                        } else {
                            // Swipe left
                            leftSwipe();
                        }
                    } else {
                        if (diffX > 0) {
                            // Swipe left
                            leftSwipe();
                        } else {
                            // Swipe right
                            rightSwipe();
                        }
                    }
                }
            }

            return true;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    index = progress;

                    chapterStart.setText(String.valueOf(index + 1));

                    if (!mangaChapterImagesUrls.isEmpty()) {
                        Glide.with(MangaChapterViewerActivity.this).load(mangaChapterImagesUrls.get(index)).into(imageView);
                    }

                    seekBar.setProgress(index);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == LTR) {
            LeftToRight = !LeftToRight;
        }

        updateSeekBar();
    }
}