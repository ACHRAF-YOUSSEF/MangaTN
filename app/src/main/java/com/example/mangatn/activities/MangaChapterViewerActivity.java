package com.example.mangatn.activities;

import static com.example.mangatn.Utils.userIsAuthenticated;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mangatn.R;
import com.example.mangatn.interfaces.chapter.OnFetchMangaChapterListener;
import com.example.mangatn.interfaces.chapter.OnGetReadChapterListener;
import com.example.mangatn.interfaces.update.OnFetchUpdateListener;
import com.example.mangatn.interfaces.update.OnMarkAsViewedOrNotListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.chapter.ChapterModel;
import com.example.mangatn.models.chapter.ReadChapterModel;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MangaChapterViewerActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> mangaChapterImagesUrls;
    private ChapterModel chapterModel;
    private PhotoView imageView;
    private SeekBar seekBar;
    private TextView chapterStart, chapterEnd, progress_text;
    private ImageButton bookmark;
    private ToggleButton LTR;
    private String mangaId;
    int index = 0;
    private boolean active = true;
    private boolean LeftToRight = false;
    private boolean added;
    private RequestManager requestManager;
    private Integer chapterReference;
    private CircularProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_chapter_viewer);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);

        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.chapters_progressBar);

        findViewById(R.id.retry).setOnClickListener(v -> loadChapterImage());

        //
        ImageButton back = toolbar.findViewById(R.id.btnBack);
        bookmark = toolbar.findViewById(R.id.saveChapter);
        TextView title = toolbar.findViewById(R.id.title);
        LTR = toolbar.findViewById(R.id.LeftToRightBtn);
        imageView = findViewById(R.id.imageView);

        progress_text = findViewById(R.id.progress_counter_text);

        //
        init();

        Intent intent = getIntent();

        mangaId = intent.getStringExtra("mangaId");
        chapterReference = intent.getIntExtra("chapterReference", 0);
        added = intent.getBooleanExtra("added", false);

        if (!added) {
            bookmark.setImageResource(R.drawable.baseline_save_alt_24);
        } else {
            bookmark.setImageResource(R.drawable.baseline_offline_pin_24);
        }

        requestManager = new RequestManager(this);

        requestManager.getMangaChapter(new OnFetchMangaChapterListener() {
            @Override
            public void onFetchData(ChapterModel response, String message, Context context) {
                chapterModel = response;

                //
                String modelTitle = chapterModel.getTitle();

                title.setText(
                        String.format(
                                "%s",
                                modelTitle.length() > 60 ?
                                        modelTitle.substring(0, 60) + "..." :
                                        modelTitle
                        )
                );

                mangaChapterImagesUrls = chapterModel.getImgPaths();

                if (mangaChapterImagesUrls.isEmpty()) {
                    Toast.makeText(MangaChapterViewerActivity.this, "error!", Toast.LENGTH_SHORT).show();
                } else {
                    progress_text.setText(format("%d/%d", index + 1, mangaChapterImagesUrls.size()));

                    loadChapterImage();
                }

                updateSeekBar();

                if (userIsAuthenticated()) {
                    requestManager.getReadChapter(new OnGetReadChapterListener() {
                        @Override
                        public void onFetchData(ReadChapterModel response, String message, Context context) {
                            if (!response.isInProgress() && !response.isCompleted()) {
                                requestManager.createOrDeleteReadChapter(new OnMarkAsViewedOrNotListener() {
                                    @Override
                                    public void onFetchData(ApiResponse response, String message, Context context) {

                                    }

                                    @Override
                                    public void onError(String message, Context context) {
                                        Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
                                    }
                                }, chapterModel.getReference(), mangaId, true);
                            } else if (response.isInProgress()) {
                                if (!mangaChapterImagesUrls.isEmpty()) {
                                    index = response.getProgress();
                                    seekBar.setProgress(response.getProgress());

                                    progress_text.setText(String.format("%d/%d", index + 1, mangaChapterImagesUrls.size()));
                                    loadChapterImage();

                                    updateSeekBar();
                                    checkIfTheChapterIsNearlyCompletedOrCompleted();
                                }
                            }
                        }

                        @Override
                        public void onError(String message, Context context) {
                            Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
                        }
                    }, chapterModel.getReference(), mangaId);
                }
            }

            @Override
            public void onError(String message, Context context) {

            }
        }, chapterReference, mangaId);

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

                findViewById(R.id.progress_counter).setVisibility(View.GONE);

                getSupportActionBar().show();
            } else {
                if (!LeftToRight) {
                    findViewById(R.id.progressRightToLeft).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.progressLeftToRight).setVisibility(View.GONE);
                }

                findViewById(R.id.progress_counter).setVisibility(View.VISIBLE);

                getSupportActionBar().hide();
            }

            active = !active;
        });
    }

    private void loadChapterImage() {
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.error_screen).setVisibility(View.GONE);

        Picasso.get()
                .load(mangaChapterImagesUrls.get(index))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);

                        findViewById(R.id.error_screen).setVisibility(View.VISIBLE);
                    }
                });
    }

    private void rightSwipe() {
        --index;

        if (index < 0) {
            index = 0;
        }

        if (!mangaChapterImagesUrls.isEmpty()) {
            chapterStart.setText(valueOf(index + 1));
            seekBar.setProgress(index);

            progress_text.setText(String.format("%d/%d", index + 1, mangaChapterImagesUrls.size()));
            loadChapterImage();
        }

        checkIfTheChapterIsNearlyCompletedOrCompleted();
    }

    private void leftSwipe() {
        ++index;

        if (index == mangaChapterImagesUrls.size()) {
            index = mangaChapterImagesUrls.size() - 1;
        }

        if (!mangaChapterImagesUrls.isEmpty()) {
            chapterStart.setText(valueOf(index + 1));
            seekBar.setProgress(index);

            progress_text.setText(String.format("%d/%d", index + 1, mangaChapterImagesUrls.size()));
            loadChapterImage();
        }

        checkIfTheChapterIsNearlyCompletedOrCompleted();
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

        chapterStart.setText(valueOf(px + 1));
        chapterEnd.setText(valueOf(mangaChapterImagesUrls.size()));

        seekBar.setMax(mangaChapterImagesUrls.size() - 1);
        seekBar.setProgress(px);

        progress_text.setText(String.format("%d/%d", px + 1, mangaChapterImagesUrls.size()));

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

                    progress_text.setText(String.format("%d/%d", index + 1, mangaChapterImagesUrls.size()));
                    chapterStart.setText(valueOf(index + 1));

                    if (!mangaChapterImagesUrls.isEmpty()) {
                        loadChapterImage();
                    }

                    seekBar.setProgress(index);

                    checkIfTheChapterIsNearlyCompletedOrCompleted();
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

    private void checkIfTheChapterIsNearlyCompletedOrCompleted() {
        int progress = seekBar.getProgress();
        int max = seekBar.getMax();

        Log.i("progress", "checkIfTheChapterIsNearlyCompletedOrCompleted: " + progress + ":" + max);

        if (userIsAuthenticated()) {
            requestManager.getReadChapter(new OnGetReadChapterListener() {
                @Override
                public void onFetchData(ReadChapterModel response, String message, Context context) {
                    if (!response.isCompleted()) {
                        if (progress == max) {
                            // api call to mark the chapter as viewed for the current user
                            requestManager.updateReadChapter(new OnFetchUpdateListener() {
                                @Override
                                public void onFetchData(Object o, String message, Context context) {

                                }

                                @Override
                                public void onError(String message, Context context) {
                                    Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
                                }
                            }, chapterModel.getReference(), mangaId, new ReadChapterModel(true, false, progress, chapterModel, mangaId));
                        } else if (progress < max) {
                            if (userIsAuthenticated()) {
                                requestManager.updateReadChapter(new OnFetchUpdateListener() {
                                    @Override
                                    public void onFetchData(Object o, String message, Context context) {

                                    }

                                    @Override
                                    public void onError(String message, Context context) {
                                        Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
                                    }
                                }, chapterModel.getReference(), mangaId, new ReadChapterModel(false, true, progress, chapterModel, mangaId));
                            }
                        }
                    }
                }

                @Override
                public void onError(String message, Context context) {
                    Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
                }
            }, chapterModel.getReference(), mangaId);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == LTR) {
            LeftToRight = !LeftToRight;
        }

        updateSeekBar();
    }
}