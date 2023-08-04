package com.example.mangatn.adapters;

import static com.example.mangatn.Utils.getUserToken;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mangatn.R;
import com.example.mangatn.interfaces.OnCheckIfAChapterIsViewedListener;
import com.example.mangatn.interfaces.OnGetReadChapterListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ChapterModel;
import com.example.mangatn.models.ReadChapterModel;

import java.util.List;

public class ChaptersAdapter extends ArrayAdapter<ChapterModel> {
    private RequestManager requestManager;
    private String mangaId;

    public ChaptersAdapter(@NonNull Context context, @NonNull List<ChapterModel> objects, String mangaId) {
        super(context, 0, objects);
        this.mangaId = mangaId;
        requestManager = new RequestManager(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, null);

        TextView tvChapter = convertView.findViewById(R.id.tvChapter);

        ChapterModel chapterModel = getItem(position);

        tvChapter.setText(chapterModel.getTitle());

        // api call to check if the chapter has already been read
        if (getUserToken() != null) {
            if (!getUserToken().isEmpty()) {
                if (chapterModel.isInProgress()) {
                    tvChapter.setTextColor(Color.parseColor("#9E9E9E"));
                }

                if (chapterModel.isCompleted()) {
                    tvChapter.setTextColor(Color.parseColor("#4CAF50"));
                }
            }
        }

        return convertView;
    }
}