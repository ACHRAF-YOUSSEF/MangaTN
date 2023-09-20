package com.example.mangatn.fragments.manga;

import static com.example.mangatn.Utils.userIsAuthenticated;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mangatn.R;
import com.example.mangatn.activities.manga.MangaChapterViewerActivity;
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

public class MangaChapterFragment extends Fragment implements View.OnClickListener {
    private List<String> mangaChapterImagesUrls;
    private ChapterModel chapterModel;
    private PhotoView imageView;
    private SeekBar seekBar;
    private TextView chapterStart, chapterEnd, progress_text;
    private ToggleButton LTR;
    private final String mangaId;
    int index = 0;
    private boolean active = true;
    private boolean leftToRight;
    private RequestManager requestManager;
    private final Integer chapterReference;
    private CircularProgressIndicator progressBar;
    private View view;

    public MangaChapterFragment(String mangaId, Integer chapterReference, boolean leftToRight) {
        this.mangaId = mangaId;
        this.leftToRight = leftToRight;
        this.chapterReference = chapterReference;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manga_chapter, container, false);

        Context context = container.getContext();

        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        progressBar = view.findViewById(R.id.chapters_progressBar);

        view.findViewById(R.id.retry).setOnClickListener(v -> loadChapterImage());

        //
        ImageButton back = toolbar.findViewById(R.id.btnBack);
        TextView title = toolbar.findViewById(R.id.title);
        LTR = toolbar.findViewById(R.id.LeftToRightBtn);
        imageView = view.findViewById(R.id.imageView);

        progress_text = view.findViewById(R.id.progress_counter_text);

        //
        init();

        requestManager = new RequestManager(context);

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
                    Toast.makeText(context, "error!", Toast.LENGTH_SHORT).show();
                } else {
                    progress_text.setText(format("%d/%d", index + 1, mangaChapterImagesUrls.size()));

                    loadChapterImage();
                }

                updateSeekBar();
                toggleToolBarAndSeekBar();

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

        back.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        imageView.setOnClickListener(v -> toggleToolBarAndSeekBar());

        return view;
    }

    private void toggleToolBarAndSeekBar() {
        if (!active) {
            if (!leftToRight) {
                view.findViewById(R.id.progressRightToLeft).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.progressLeftToRight).setVisibility(View.VISIBLE);
            }

            view.findViewById(R.id.progress_counter).setVisibility(View.GONE);

            ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        } else {
            view.findViewById(R.id.progressRightToLeft).setVisibility(View.GONE);
            view.findViewById(R.id.progressLeftToRight).setVisibility(View.GONE);

            view.findViewById(R.id.progress_counter).setVisibility(View.VISIBLE);

            ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        }

        active = !active;
    }

    private void loadChapterImage() {
        progressBar.setVisibility(View.VISIBLE);
        view.findViewById(R.id.error_screen).setVisibility(View.GONE);

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

                        view.findViewById(R.id.error_screen).setVisibility(View.VISIBLE);
                    }
                });
    }

    private void rightSwipe() {
        --index;

        if (index < 0) {
            index = 0;

            ((MangaChapterViewerActivity) requireActivity())
                    .replaceWithFinishScreenFragment(
                            chapterReference,
                            mangaId,
                            chapterModel.getTitle(),
                            false,
                            leftToRight
                    );
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

            ((MangaChapterViewerActivity) requireActivity())
                    .replaceWithFinishScreenFragment(
                            chapterReference,
                            mangaId,
                            chapterModel.getTitle(),
                            true,
                            leftToRight
                    );
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
        if (!leftToRight) {
            LTR.setChecked(false);
            view.findViewById(R.id.progressRightToLeft).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressLeftToRight).setVisibility(View.GONE);
            seekBar = view.findViewById(R.id.seekBarRightToLeft);
            chapterStart = view.findViewById(R.id.chapterStartRightToLeft);
            chapterEnd = view.findViewById(R.id.chapterEndRightToLeft);
        } else {
            LTR.setChecked(true);
            view.findViewById(R.id.progressLeftToRight).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressRightToLeft).setVisibility(View.GONE);
            seekBar = view.findViewById(R.id.seekBarLeftToRight);
            chapterStart = view.findViewById(R.id.chapterStartLeftToRight);
            chapterEnd = view.findViewById(R.id.chapterEndLeftToRight);
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
                    if (leftToRight) {
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
                        if (isFinished()) {
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

    private boolean isFinished() {
        return seekBar.getProgress() == seekBar.getMax();
    }

    @Override
    public void onClick(View v) {
        if (v == LTR) {
            leftToRight = !leftToRight;
        }

        updateSeekBar();
    }
}