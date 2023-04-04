package com.example.mangatn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mangatn.R;
import com.example.mangatn.models.MangaModel;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private final Context context;
    private LayoutInflater inflater;
    private final List<MangaModel> mangaModelList;

    public GridAdapter(Context context, List<MangaModel> mangaModelList) {
        this.context = context;
        this.mangaModelList = mangaModelList;
    }

    @Override
    public int getCount() {
        return mangaModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mangaModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_design, null);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);

        TextView textView1 = convertView.findViewById(R.id.textView1);
        TextView textView2 = convertView.findViewById(R.id.textView2);

        textView1.setText(mangaModelList.get(position).getChapters());
        textView2.setText(mangaModelList.get(position).getTitle());

        Glide.with(convertView).load(mangaModelList.get(position).getImgPath()).into(imageView);

        return convertView;
    }
}