package com.example.mangatn.activities.home;

import static com.example.mangatn.Utils.getUserToken;
import static com.example.mangatn.Utils.setUserToken;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mangatn.R;
import com.example.mangatn.fragments.filter.MangaFilterFragment.OnFilterAppliedListener;
import com.example.mangatn.fragments.home.SearchFragment;
import com.example.mangatn.models.manga.MangaModel;
import com.example.mangatn.models.manga.filter.MangaFilter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnFilterAppliedListener {
    public static List<MangaModel> mangalList;
    private static final int REQUEST_PERMISSION = 1;
    private static final SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, searchFragment)
                    .commit();
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_NETWORK_STATE
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE
            }, REQUEST_PERMISSION);
            finish();
        } else {
            initData();
        }
    }

    private void initData() {
        mangalList = new ArrayList<>();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token", getUserToken());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token != null) {
            if (!token.isEmpty()) {
                setUserToken(token);
            } else {
                setUserToken(null);
            }
        }
    }

    @Override
    public void onFilterApplied(MangaFilter selectedFilters) {
        searchFragment.onFilterApplied(selectedFilters);
    }
}