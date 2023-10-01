package com.example.mangatn.activities.home;

import static com.example.mangatn.Utils.getUserToken;
import static com.example.mangatn.Utils.setUserToken;
import static com.example.mangatn.Utils.userIsAuthenticated;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mangatn.R;
import com.example.mangatn.db.MangaDatabaseHelper;
import com.example.mangatn.fragments.filter.MangaFilterFragment.OnFilterAppliedListener;
import com.example.mangatn.fragments.home.SearchFragment;
import com.example.mangatn.models.manga.MangaModel;
import com.example.mangatn.models.manga.filter.MangaFilter;
import com.example.mangatn.monitor.ConnectionStateMonitor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnFilterAppliedListener {
    public static List<MangaModel> mangalList;
    private static final int REQUEST_PERMISSION = 1;
    private static ConnectionStateMonitor connectionStateMonitor;
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
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    },
                    REQUEST_PERMISSION
            );
            finish();
        } else {
            initData();
        }

        MangaDatabaseHelper helper = MangaDatabaseHelper
                .getInstance(this);

        getConnectionStateMonitor(this)
                .startMonitoring(isConnected -> {
                    if (isConnected) {
                        if (userIsAuthenticated()) {
                            if (helper.processJournalEntries(this)) {
                                Toast.makeText(this, "Data successfully synced", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
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
    protected void onDestroy() {
        super.onDestroy();

        if (connectionStateMonitor != null) {
            connectionStateMonitor.stopMonitoring();
        }
    }

    @Override
    public void onFilterApplied(MangaFilter selectedFilters) {
        searchFragment.onFilterApplied(selectedFilters);
    }

    public static synchronized ConnectionStateMonitor getConnectionStateMonitor(Context context) {
        if (connectionStateMonitor == null) {
            connectionStateMonitor = new ConnectionStateMonitor(context.getApplicationContext());
        }

        return connectionStateMonitor;
    }
}