package com.example.mangatn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mangatn.R;
import com.example.mangatn.interfaces.manga.SelectListener;
import com.example.mangatn.models.manga.MangaModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private final Context context;
    private LayoutInflater inflater;
    private final List<MangaModel> mangaModelList;
    private final SelectListener listener;

    public GridAdapter(Context context, List<MangaModel> mangaModelList, SelectListener listener) {
        this.context = context;
        this.mangaModelList = mangaModelList;
        this.listener = listener;
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

        MangaModel mangaModel = mangaModelList.get(position);

        textView1.setText(String.valueOf(mangaModel.getCount()));
        textView2.setText(mangaModel.getTitle());

        Picasso.get().load(mangaModel.getCoverImgPath()).into(imageView);

        convertView.setOnClickListener(v -> listener.OnMangaClicked(mangaModel, context));

        return convertView;
    }
}