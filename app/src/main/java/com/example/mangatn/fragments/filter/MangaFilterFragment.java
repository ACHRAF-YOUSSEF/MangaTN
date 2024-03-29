package com.example.mangatn.fragments.filter;

import static com.example.mangatn.Utils.userIsAuthenticated;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mangatn.R;
import com.example.mangatn.models.Enum.EMangaBookmark;
import com.example.mangatn.models.Enum.EMangaGenre;
import com.example.mangatn.models.Enum.EMangaStatus;
import com.example.mangatn.models.manga.filter.MangaFilter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MangaFilterFragment extends BottomSheetDialogFragment {
    private StatusFilterFragment statusFilterFragment;
    private GenreFilterFragment genreFilterFragment;
    private BookmarkFilterFragment bookmarkFilterFragment;
    private final MangaFilter mangaFilter;
    private OnFilterAppliedListener onFilterAppliedListener;

    public MangaFilterFragment(MangaFilter mangaFilter) {
        this.mangaFilter = mangaFilter;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFilterAppliedListener) {
            onFilterAppliedListener = (OnFilterAppliedListener) context;
        } else {
            throw new ClassCastException(context + " must implement OnFilterAppliedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        onFilterAppliedListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manga_filter, container, false);

        TabLayout tabLayout = view.findViewById(R.id.manga_filter_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.manga_filter_view_pager);
        Button applyFilterButton = view.findViewById(R.id.manga_filter_button_apply_filter);

        EMangaGenre[] genreOptions = EMangaGenre.values();
        EMangaStatus[] statusOptions = EMangaStatus.values();
        EMangaBookmark[] bookmarksOptions = EMangaBookmark.values();

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        List<String> selectedBookmarks = new ArrayList<>();

        if (userIsAuthenticated()) {
            List<EMangaBookmark> bookmarks = mangaFilter.getBookmarks();

            if (bookmarks != null) {
                selectedBookmarks.addAll(
                        bookmarks
                                .stream()
                                .map(EMangaBookmark::getCustomDisplay)
                                .collect(Collectors.toList())
                );
            }
        }

        bookmarkFilterFragment = new BookmarkFilterFragment(
                bookmarksOptions,
                selectedBookmarks
        );
        statusFilterFragment = new StatusFilterFragment(
                statusOptions,
                mangaFilter
                        .getStatuses()
                        .stream()
                        .map(EMangaStatus::getCustomDisplay)
                        .collect(Collectors.toList())
        );
        genreFilterFragment = new GenreFilterFragment(
                genreOptions,
                mangaFilter
                        .getGenres()
                        .stream()
                        .map(EMangaGenre::getCustomDisplayName)
                        .collect(Collectors.toList()))
        ;

        adapter.addFragment(statusFilterFragment, "Status");
        adapter.addFragment(genreFilterFragment, "Genre");

        if (userIsAuthenticated()) {
            adapter.addFragment(bookmarkFilterFragment, "Bookmark");
        }

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(
                tabLayout,
                viewPager,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();

        applyFilterButton.setOnClickListener(v -> {
            MangaFilter selectedFilters = getSelectedFilters();

            if (onFilterAppliedListener != null) {
                onFilterAppliedListener.onFilterApplied(selectedFilters);
            }

            dismiss();
        });

        return view;
    }

    public interface OnFilterAppliedListener {
        void onFilterApplied(MangaFilter selectedFilters);
    }

    private MangaFilter getSelectedFilters() {
        List<EMangaStatus> selectedStatus = new ArrayList<>();
        List<EMangaGenre> selectedGenres = new ArrayList<>();

        if (statusFilterFragment != null) {
            selectedStatus.addAll(
                    statusFilterFragment
                            .getSelectedStatus()
                            .stream()
                            .map(EMangaStatus::fromCustomDisplay)
                            .collect(Collectors.toList())
            );
        }

        if (genreFilterFragment != null) {
            selectedGenres.addAll(
                    genreFilterFragment
                            .getSelectedGenres()
                            .stream()
                            .map(EMangaGenre::fromCustomDisplayName)
                            .collect(Collectors.toList())
            );
        }

        MangaFilter filter = new MangaFilter("", selectedStatus, selectedGenres);

        if (userIsAuthenticated()) {
            if (bookmarkFilterFragment != null) {
                filter.setBookmarks(
                        bookmarkFilterFragment
                                .getSelectedBookmarks()
                                .stream()
                                .map(EMangaBookmark::fromCustomDisplay)
                                .collect(Collectors.toList())
                );
            }
        }

        return filter;
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}