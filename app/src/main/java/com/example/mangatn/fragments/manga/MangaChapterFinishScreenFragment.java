package com.example.mangatn.fragments.manga;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.mangatn.R;
import com.example.mangatn.activities.manga.MangaChapterViewerActivity;
import com.example.mangatn.interfaces.chapter.OnFetchMangaChapterListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.chapter.ChapterModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MangaChapterFinishScreenFragment extends Fragment implements View.OnTouchListener {
    private final Integer chapterReference;
    private final String mangaId;
    private final String current;
    private final boolean isNext;
    private final boolean leftToRight;
    private ChapterModel chapterModelToOpen;
    private CircularProgressIndicator progressBar;
    private GestureDetector gestureDetector;
    private FragmentActivity activity;

    public MangaChapterFinishScreenFragment(Integer chapterReference, String mangaId, String current, boolean isNextOrPrevious, boolean leftToRight) {
        this.chapterReference = chapterReference;
        this.mangaId = mangaId;
        this.current = current;
        this.isNext = isNextOrPrevious;
        this.leftToRight = leftToRight;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manga_chapter_finish_screen, container, false);

        TextView currentChapterNameTextView = view.findViewById(R.id.currentChapterNameTextView);
        TextView nextChapterNameTextView = view.findViewById(R.id.nextChapterNameTextView);
        TextView previousChapterNameTextView = view.findViewById(R.id.previousChapterNameTextView);
        TextView finishedChapterNameTextView = view.findViewById(R.id.finishedChapterNameTextView);

        LinearLayout currentAndNextLayout = view.findViewById(R.id.currentAndNextLayout);
        LinearLayout previousAndCurrentLayout = view.findViewById(R.id.previousAndCurrentLayout);

        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        previousAndCurrentLayout.setVisibility(View.GONE);
        currentAndNextLayout.setVisibility(View.GONE);

        Context context = container.getContext();

        RequestManager requestManager = new RequestManager(context);

        if (isNext) {
            requestManager.getNextMangaChapter(new OnFetchMangaChapterListener() {
                @Override
                public void onFetchData(ChapterModel response, String message, Context context) {
                    chapterModelToOpen = response;

                    setData(currentChapterNameTextView, nextChapterNameTextView, previousChapterNameTextView, finishedChapterNameTextView, currentAndNextLayout, previousAndCurrentLayout);
                }

                @Override
                public void onError(String message, Context context) {

                }
            }, chapterReference, mangaId);
        } else {
            requestManager.getPreviousMangaChapter(new OnFetchMangaChapterListener() {
                @Override
                public void onFetchData(ChapterModel response, String message, Context context) {
                    chapterModelToOpen = response;

                    setData(currentChapterNameTextView, nextChapterNameTextView, previousChapterNameTextView, finishedChapterNameTextView, currentAndNextLayout, previousAndCurrentLayout);
                }

                @Override
                public void onError(String message, Context context) {

                }
            }, chapterReference, mangaId);
        }

        gestureDetector = new GestureDetector(context, new SwipeGestureListener());

        view.setOnTouchListener(this);

        activity = getActivity();

        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int MIN_SWIPE_DISTANCE = 150;
        private static final int MAX_SWIPE_DURATION = 150;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e2.getX() - e1.getX();
            float deltaY = e2.getY() - e1.getY();

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE && Math.abs(velocityX) > MAX_SWIPE_DURATION) {
                    if (deltaX > 0) {
                        Log.i("isNext|leftToRight|reference", "handleSwipeRight: " + isNext + "|" + leftToRight + "|" + chapterModelToOpen.getReference());

                        handleSwipeRight();
                    } else {
                        Log.i("isNext|leftToRight|reference", "handleSwipeLeft: " + isNext + "|" + leftToRight + "|" + chapterModelToOpen.getReference());

                        handleSwipeLeft();
                    }

                    return true;
                }
            }

            return false;
        }

        private void handleSwipeLeft() {
            if (!isNext && !leftToRight) {
                Integer reference = chapterModelToOpen.getReference();

                if (reference != null) {
                    MangaChapterViewerActivity activityForTheFragment = getActivityForTheFragment();

                    if (activityForTheFragment != null) {
                        activityForTheFragment.setChapterReference(reference);

                        openNextOrPreviousOrCurrentChapter(true, false);
                    }
                }
            } else if (!isNext || !leftToRight) {
                openNextOrPreviousOrCurrentChapter(false, true);
            } else {
                Integer reference = chapterModelToOpen.getReference();

                if (reference != null) {
                    MangaChapterViewerActivity activityForTheFragment = getActivityForTheFragment();

                    if (activityForTheFragment != null) {
                        activityForTheFragment.setChapterReference(reference);

                        openNextOrPreviousOrCurrentChapter(true, false);
                    }
                }
            }
        }

        private void handleSwipeRight() {
            if (isNext && !leftToRight) {
                Integer reference = chapterModelToOpen.getReference();

                if (reference != null) {
                    MangaChapterViewerActivity activityForTheFragment = getActivityForTheFragment();

                    if (activityForTheFragment != null) {
                        activityForTheFragment.setChapterReference(reference);

                        openNextOrPreviousOrCurrentChapter(true, false);
                    }
                }
            } else if (!isNext && leftToRight) {
                Integer reference = chapterModelToOpen.getReference();

                if (reference != null) {
                    MangaChapterViewerActivity activityForTheFragment = getActivityForTheFragment();

                    if (activityForTheFragment != null) {
                        activityForTheFragment.setChapterReference(reference);

                        openNextOrPreviousOrCurrentChapter(false, false);
                    }
                }
            } else {
                openNextOrPreviousOrCurrentChapter(false, true);
            }
        }
    }

    private MangaChapterViewerActivity getActivityForTheFragment() {
        if (activity != null) {
            if (activity instanceof MangaChapterViewerActivity) {
                return ((MangaChapterViewerActivity) activity);
            }
        }

        return null;
    }

    private void openNextOrPreviousOrCurrentChapter(boolean isNextChapter, boolean isCurrentChapter) {
        MangaChapterViewerActivity activityForTheFragment = getActivityForTheFragment();

        if (activityForTheFragment != null) {
            if (isCurrentChapter) {
                activityForTheFragment.replaceWithCurrentMangaChapter(leftToRight);
            } else {
                if (isNextChapter) {
                    activityForTheFragment.replaceWithNextMangaChapter(chapterModelToOpen, leftToRight);
                } else {
                    activityForTheFragment.replaceWithPreviousMangaChapter(chapterModelToOpen, leftToRight);
                }
            }
        }
    }

    private void setData(TextView currentChapterNameTextView, TextView nextChapterNameTextView, TextView previousChapterNameTextView, TextView finishedChapterNameTextView, LinearLayout currentAndNextLayout, LinearLayout previousAndCurrentLayout) {
        String title = chapterModelToOpen.getTitle();

        if (isNext) {
            currentChapterNameTextView.setText(current);
            nextChapterNameTextView.setText(title);
        } else {
            previousChapterNameTextView.setText(title);
            finishedChapterNameTextView.setText(current);
        }

        if (isNext) {
            currentAndNextLayout.setVisibility(View.VISIBLE);
            previousAndCurrentLayout.setVisibility(View.GONE);
        } else {
            currentAndNextLayout.setVisibility(View.GONE);
            previousAndCurrentLayout.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);
    }
}