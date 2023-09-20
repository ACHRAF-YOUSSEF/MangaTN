package com.example.mangatn.activities.manga;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mangatn.R;
import com.example.mangatn.fragments.manga.MangaChapterFinishScreenFragment;
import com.example.mangatn.fragments.manga.MangaChapterFragment;
import com.example.mangatn.models.chapter.ChapterModel;

public class MangaChapterViewerActivity extends AppCompatActivity {
    private static final String EXTRA_MANGA_ID = "manga_id";
    private static final String EXTRA_CHAPTER_REFERENCE = "chapter_reference";
    private String mangaId;
    private Integer chapterReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_chapter_viewer);

        mangaId = getIntent().getStringExtra(EXTRA_MANGA_ID);
        chapterReference = getIntent().getIntExtra(EXTRA_CHAPTER_REFERENCE, 0);

        if (savedInstanceState == null) {
            MangaChapterFragment mangaChapterFragment = new MangaChapterFragment(mangaId, chapterReference, false);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mangaChapterFragment)
                    .commit();
        }
    }

    public void setChapterReference(Integer chapterReference) {
        this.chapterReference = chapterReference;
    }

    public static Intent newIntent(Context context, String mangaId, int chapterReference) {
        Intent intent = new Intent(context, MangaChapterViewerActivity.class);

        intent.putExtra(EXTRA_MANGA_ID, mangaId);
        intent.putExtra(EXTRA_CHAPTER_REFERENCE, chapterReference);

        return intent;
    }

    public void replaceWithNextMangaChapter(ChapterModel chapterModel, boolean leftToRight) {
        MangaChapterFragment mangaChapterFragment = new MangaChapterFragment(mangaId, chapterModel.getReference(), leftToRight);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mangaChapterFragment)
                .commit();
    }

    public void replaceWithPreviousMangaChapter(ChapterModel chapterModel, boolean leftToRight) {
        MangaChapterFragment mangaChapterFragment = new MangaChapterFragment(mangaId, chapterModel.getReference(), leftToRight);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mangaChapterFragment)
                .commit();
    }

    public void replaceWithFinishScreenFragment(Integer chapterReference, String mangaId, String current, boolean isNext, boolean leftToRight) {
        MangaChapterFinishScreenFragment finishScreenFragment = new MangaChapterFinishScreenFragment(chapterReference, mangaId, current, isNext, leftToRight);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, finishScreenFragment)
                .commit();
    }

    public void replaceWithCurrentMangaChapter(boolean leftToRight) {
        MangaChapterFragment mangaChapterFragment = new MangaChapterFragment(mangaId, chapterReference, leftToRight);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mangaChapterFragment)
                .commit();
    }
}