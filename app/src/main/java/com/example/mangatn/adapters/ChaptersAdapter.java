package com.example.mangatn.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mangatn.R;
import com.example.mangatn.models.chapter.ChapterModel;

import java.util.List;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ViewHolder> {
    private final List<ChapterModel> chapters;
    private OnItemClickListener onItemClickListener;

    public ChaptersAdapter(List<ChapterModel> chapters) {
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChapterModel chapterModel = chapters.get(position);
        holder.tvChapter.setText(chapterModel.getTitle());

        if (chapterModel.isInProgress()) {
            holder.tvChapter.setTextColor(Color.parseColor("#9E9E9E"));
        }
        if (chapterModel.isCompleted()) {
            holder.tvChapter.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvChapter;

        public ViewHolder(View itemView) {
            super(itemView);

            tvChapter = itemView.findViewById(R.id.tvChapter);

            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
