package com.example.mangatn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mangatn.R;
import com.example.mangatn.models.ChapterModel;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MangaChapterViewerActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> mangaChapterImagesUrls;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        //
        ImageButton back = getSupportActionBar().getCustomView().findViewById(R.id.btnBack);
        bookmark = getSupportActionBar().getCustomView().findViewById(R.id.saveChapter);
        TextView title = getSupportActionBar().getCustomView().findViewById(R.id.title);
        LTR = getSupportActionBar().getCustomView().findViewById(R.id.LeftToRightBtn);
        imageView = findViewById(R.id.imageView);

        //
        init();

        Intent intent = getIntent();
        ChapterModel chapterModel = (ChapterModel) intent.getSerializableExtra("data");
        added = intent.getBooleanExtra("added", false);

        if (!added) {
            bookmark.setImageResource(R.drawable.baseline_save_alt_24);
        } else {
            bookmark.setImageResource(R.drawable.baseline_offline_pin_24);
        }

        //
        title.setText(chapterModel.getTitle());

        mangaChapterImagesUrls = chapterModel.getImgPaths();

        if (mangaChapterImagesUrls.isEmpty()) {
            Toast.makeText(MangaChapterViewerActivity.this, "error!", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(mangaChapterImagesUrls.get(index)).into(imageView);
        }

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

                getSupportActionBar().getCustomView().setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            } else {
                if (!LeftToRight) {
                    findViewById(R.id.progressRightToLeft).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.progressLeftToRight).setVisibility(View.GONE);
                }

                getSupportActionBar().getCustomView().setVisibility(View.GONE);
                getSupportActionBar().hide();
            }

            active = !active;
        });

        updateSeekBar();
    }

    private void rightSwipe() {
        --index;

        if (index < 0) {
            index = 0;
        }

        if (!mangaChapterImagesUrls.isEmpty()) {
            chapterStart.setText(String.valueOf(index + 1));
            seekBar.setProgress(index);

            Picasso.get().load(mangaChapterImagesUrls.get(index)).into(imageView);
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

            Picasso.get().load(mangaChapterImagesUrls.get(index)).into(imageView);
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
                        Picasso.get().load(mangaChapterImagesUrls.get(index)).into(imageView);
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