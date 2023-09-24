package com.example.mangatn.fragments.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.mangatn.R;
import com.example.mangatn.models.Enum.EMangaBookmark;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookmarkFilterFragment extends Fragment {
    private final EMangaBookmark[] bookmarkOptions;
    private final List<String> appliedStatus;
    private MaterialCheckBox parentCheckBox;
    private final Map<String, MaterialCheckBox> checkBoxMap = new HashMap<>();

    public BookmarkFilterFragment(EMangaBookmark[] bookmarkOptions, List<String> appliedStatus) {
        this.bookmarkOptions = bookmarkOptions;
        this.appliedStatus = appliedStatus;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        LinearLayout checkboxLayout = view.findViewById(R.id.checkbox_layout_bookmarks_container);
        parentCheckBox = view.findViewById(R.id.checkbox_parent);

        parentCheckBox.setChecked(false);

        for (EMangaBookmark option : bookmarkOptions) {
            MaterialCheckBox checkBox = new MaterialCheckBox(requireContext());

            checkBox.setText(option.getCustomDisplay());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateParentCheckBoxState());

            checkBoxMap.put(option.getCustomDisplay().toLowerCase(), checkBox);
            checkboxLayout.addView(checkBox);
        }

        parentCheckBox
                .setOnCheckedChangeListener((buttonView, isChecked) -> setChildCheckBoxesChecked(isChecked));

        updateFilterItems();

        return view;
    }

    private void updateFilterItems() {
        for (EMangaBookmark option : bookmarkOptions) {
            MaterialCheckBox checkBox = checkBoxMap.get(option.getCustomDisplay().toLowerCase());

            if (checkBox != null) {
                checkBox.setChecked(appliedStatus.contains(option.getCustomDisplay()));
            }
        }
    }

    private void updateParentCheckBoxState() {
        boolean allChecked = true;
        boolean anyChecked = false;

        for (EMangaBookmark option : bookmarkOptions) {
            MaterialCheckBox checkBox = checkBoxMap.get(option.getCustomDisplay().toLowerCase());

            if (checkBox != null) {
                if (checkBox.isChecked()) {
                    anyChecked = true;
                } else {
                    allChecked = false;
                }
            }
        }

        if (allChecked) {
            parentCheckBox.setChecked(true);
        } else if (anyChecked) {
            parentCheckBox.setCheckedState(MaterialCheckBox.STATE_INDETERMINATE);
        } else {
            parentCheckBox.setChecked(false);
        }
    }

    private void setChildCheckBoxesChecked(boolean isChecked) {
        for (EMangaBookmark option : bookmarkOptions) {
            MaterialCheckBox checkBox = checkBoxMap.get(option.getCustomDisplay().toLowerCase());

            if (checkBox != null) {
                checkBox.setChecked(isChecked);
            }
        }
    }

    public List<String> getSelectedBookmarks() {
        List<String> selectedStatus = new ArrayList<>();

        checkBoxMap.forEach((s, materialCheckBox) -> {
            if (materialCheckBox.isChecked()) {
                selectedStatus.add(s);
            }
        });

        return selectedStatus;
    }
}