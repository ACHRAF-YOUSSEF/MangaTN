package com.example.mangatn.fragments.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.mangatn.R;
import com.example.mangatn.activities.manga.MangaChapterViewerActivity;
import com.example.mangatn.adapters.ChaptersAdapter;
import com.example.mangatn.interfaces.chapter.OnFetchMangaChaptersListListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.chapter.ChapterModel;
import com.example.mangatn.models.chapter.ChaptersListApiResponse;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class TabFragment extends Fragment implements OnFetchMangaChaptersListListener {
    private ChaptersAdapter chaptersAdapter;
    private RequestManager requestManager;
    private ListView chaptersListView;
    private CircularProgressIndicator progressBar;
    private boolean added = false;
    private TabLayout tabLayout;
    private String mangaId;

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        progressBar.setVisibility(View.VISIBLE);
        chaptersListView.setVisibility(View.GONE);
        int selectedIndex = tabLayout.getSelectedTabPosition();

        if (selectedIndex != -1) {
            Log.i("selectedIndex", "onCreateView: " + selectedIndex);
            requestManager.getMangaChapters(this, mangaId, selectedIndex, 50);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_tab, container, false);

        chaptersListView = view1.findViewById(R.id.chaptersListView);
        progressBar = view1.findViewById(R.id.progressBar);

        Bundle bundle = getArguments();
        FragmentActivity activity = getActivity();

        requestManager = new RequestManager(getContext());

        if (bundle != null) {
            mangaId = bundle.getString("mangaId");
            tabLayout = activity.findViewById(R.id.view_tabLayout);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    updateData();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    // Not used
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    // Not used
                }
            });
        }

        return view1;
    }

    private void showChapters(Context context, List<ChapterModel> chapters) {
        chaptersAdapter = new ChaptersAdapter(context, chapters, mangaId);
        chaptersListView.setAdapter(chaptersAdapter);
        chaptersListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent1 = new Intent(getContext(), MangaChapterViewerActivity.class);

            intent1.putExtra("added", added);
            intent1.putExtra("chapterReference", chapters.get(position).getReference());
            intent1.putExtra("mangaId", mangaId);

            startActivity(intent1);
        });

        chaptersListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFetchData(ChaptersListApiResponse response, String message, Context context) {
        showChapters(getContext(), response.getChapters());
    }

    @Override
    public void onError(String message, Context context) {
        Toast.makeText(context, "An Error Occurred!!!" + message, Toast.LENGTH_SHORT).show();
    }
}