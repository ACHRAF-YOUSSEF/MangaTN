package com.example.mangatn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mangatn.R;
import com.example.mangatn.models.ChapterModel;

import java.util.List;

public class ChaptersAdapter extends ArrayAdapter<ChapterModel> {
    public ChaptersAdapter(@NonNull Context context, @NonNull List<ChapterModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, null);

        TextView tvChapter = convertView.findViewById(R.id.tvChapter);

        ChapterModel chapterModel = getItem(position);

        tvChapter.setText(chapterModel.getChapter());

        return convertView;
    }
}