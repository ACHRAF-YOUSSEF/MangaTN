package com.example.mangatn.fragments;

import static com.example.mangatn.MainActivity.WEBSITE_URL;
import static com.example.mangatn.MainActivity.mangalList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.mangatn.ItemViewerActivity;
import com.example.mangatn.R;
import com.example.mangatn.adapters.GridAdapter;
import com.example.mangatn.models.MangaModel;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Fragment1 extends Fragment {
    public GridView gridView;
    private GridAdapter gridAdapter;
    private SearchView searchView;
    private pl.droidsonroids.gif.GifTextView gifTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_1, container, false);

        gifTextView = view1.findViewById(R.id.loading);
        gridView = view1.findViewById(R.id.gridView);
        searchView = view1.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    new SearchTask().execute(query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        gridAdapter = new GridAdapter(container.getContext(), mangalList);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(container.getContext(), ItemViewerActivity.class);

            intent.putExtra("title", mangalList.get(position).getTitle());
            intent.putExtra("cover", mangalList.get(position).getImgPath());
            String mangaId = mangalList.get(position).getMangaId();
            intent.putExtra(
                    "chaptersUrl",
                    WEBSITE_URL + "/manga/" + mangaId
            );
            intent.putExtra("mangaId", mangaId);

            startActivity(intent);
        });

        return view1;
    }

    private class SearchTask extends AsyncTask<String, Void, List<MangaModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            gridView.setVisibility(View.GONE);
            gifTextView.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MangaModel> doInBackground(String... strings) {
            ArrayList<String> arrayList;
            List<MangaModel> mangaModels = new ArrayList<>();
            String elements;

            try {
                elements = Jsoup
                        .connect(WEBSITE_URL +
                                "/manga_list?q=" +
                                makeUrl(strings[0]) +
                                "&action=search"
                        )
                        .userAgent("Mozilla")
                        .get()
                        .body()
                        .select("a[href*=/manga/]")
                        .outerHtml();
            } catch (IOException e) {
                return null;
            }

            arrayList = (ArrayList<String>) Arrays
                    .stream(elements.split("<a"))
                    .distinct()
                    .map(s -> (s.contains("class"))? "<a" + s : s)
                    .collect(Collectors.toList());

            arrayList.remove("");

            ArrayList<String> mangaIds = (ArrayList<String>) Arrays
                    .stream(arrayList.toArray())
                    .map(manga -> getMangaId((String) manga))
                    .collect(Collectors.toList());

            ArrayList<String> titles = (ArrayList<String>) Arrays
                    .stream(arrayList.toArray())
                    .map(manga -> getMangaTitle((String) manga))
                    .collect(Collectors.toList());

            ArrayList<String> imagesPaths = (ArrayList<String>) Arrays
                    .stream(mangaIds.toArray())
                    .map(mangaId -> WEBSITE_URL +
                            "/mangaimage/" +
                            mangaId +
                            ".jpg"
                    )
                    .collect(Collectors.toList());

            ArrayList<String> chaptersList = (ArrayList<String>) Arrays
                    .stream(mangaIds.toArray())
                    .map(mangaId -> {
                        try {
                            return String.valueOf(Jsoup
                                    .connect(
                                            WEBSITE_URL +
                                            "/manga/" +
                                            mangaId
                                    )
                                    .userAgent("Mozilla")
                                    .get()
                                    .body()
                                    .select("a[href*=/chapter/]")
                                    .outerHtml()
                                    .split("<a").length - 1
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            if (!arrayList.isEmpty()) {
                String title , chapters, imgPath, mangaId;

                for (int i = 0; i < arrayList.size(); i++) {
                    mangaId = mangaIds.get(i);
                    title = titles.get(i);
                    imgPath = imagesPaths.get(i);
                    chapters = chaptersList.get(i);

                    mangaModels.add(new MangaModel(title, chapters, imgPath, mangaId));
                }
            }

            return mangaModels;
        }

        @Override
        protected void onPostExecute(List<MangaModel> mangaModels) {
            super.onPostExecute(mangaModels);

            gridView.setVisibility(View.VISIBLE);
            gifTextView.setVisibility(View.GONE);

            if (mangaModels != null && mangaModels.size() > 0) {
                mangalList.clear();
                mangalList.addAll(mangaModels);
            }

            gridAdapter.notifyDataSetChanged();
            searchView.clearFocus();
        }
    }

    private String  makeUrl(String query) {
        return query.replace(" ", "+");
    }

    private String getMangaTitle(String manga) {
        StringBuilder sb = new StringBuilder();

        sb.append(manga.substring(manga.indexOf("\">") + 2));

        return sb.substring(0, sb.indexOf("</"));
    }

    private String getMangaId(String temp) {
        StringBuilder sb = new StringBuilder();

        sb.append(temp.substring(temp.indexOf("/manga-") + 1));

        return sb.substring(0, sb.indexOf("\""));
    }
}