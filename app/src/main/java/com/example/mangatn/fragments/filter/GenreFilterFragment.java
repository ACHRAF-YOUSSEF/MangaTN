package com.example.mangatn.fragments.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.mangatn.R;
import com.example.mangatn.models.Enum.EMangaGenre;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenreFilterFragment extends Fragment {
    private final EMangaGenre[] genreOptions;
    private final List<String> appliedGenres;
    private MaterialCheckBox parentCheckBox;
    private final Map<String, MaterialCheckBox> checkBoxMap = new HashMap<>();

    public GenreFilterFragment(EMangaGenre[] genreOptions, List<String> appliedGenres) {
        this.genreOptions = genreOptions;
        this.appliedGenres = appliedGenres;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_filter, container, false);

        LinearLayout checkboxLayout = view.findViewById(R.id.checkbox_layout_genre_container);
        parentCheckBox = view.findViewById(R.id.checkbox_parent);

        parentCheckBox.setChecked(false);

        for (EMangaGenre option : genreOptions) {
            MaterialCheckBox checkBox = new MaterialCheckBox(requireContext());

            checkBox.setText(option.getCustomDisplayName());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateParentCheckBoxState());

            checkBoxMap.put(option.getCustomDisplayName().toLowerCase(), checkBox);
            checkboxLayout.addView(checkBox);
        }

        parentCheckBox
                .setOnCheckedChangeListener((buttonView, isChecked) -> setChildCheckBoxesChecked(isChecked));

        updateFilterItems();

        return view;
    }

    private void updateFilterItems() {
        for (EMangaGenre option : genreOptions) {
            MaterialCheckBox checkBox = checkBoxMap.get(option.getCustomDisplayName().toLowerCase());

            if (checkBox != null) {
                checkBox.setChecked(appliedGenres.contains(option.getCustomDisplayName()));
            }
        }
    }

    private void updateParentCheckBoxState() {
        boolean allChecked = true;
        boolean anyChecked = false;

        for (EMangaGenre option : genreOptions) {
            MaterialCheckBox checkBox = checkBoxMap.get(option.getCustomDisplayName().toLowerCase());

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
        for (EMangaGenre option : genreOptions) {
            MaterialCheckBox checkBox = checkBoxMap.get(option.getCustomDisplayName().toLowerCase());

            if (checkBox != null) {
                checkBox.setChecked(isChecked);
            }
        }
    }

    public List<String> getSelectedGenres() {
        List<String> selectedGenres = new ArrayList<>();

        checkBoxMap.forEach((s, materialCheckBox) -> {
            if (materialCheckBox.isChecked()) {
                selectedGenres.add(s);
            }
        });

        return selectedGenres;
    }
}