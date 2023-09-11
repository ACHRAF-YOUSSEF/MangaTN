package com.example.mangatn.activities;

import static com.example.mangatn.Utils.getUserToken;
import static com.example.mangatn.Utils.setUserToken;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mangatn.R;
import com.example.mangatn.adapters.VPAdapter;
import com.example.mangatn.fragments.Fragment1;
import com.example.mangatn.fragments.Fragment2;
import com.example.mangatn.models.manga.MangaModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<MangaModel> mangalList;
    private static final int REQUEST_PERMISSION = 1;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragments(new Fragment1(), "SEARCH");
        vpAdapter.addFragments(new Fragment2(), "FAVORITES");

        viewPager.setAdapter(vpAdapter);

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this,
                            android.Manifest.permission.ACCESS_NETWORK_STATE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[] {
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
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Get the editor to make changes to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save the token to SharedPreferences
        editor.putString("token", getUserToken());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Retrieve the token from SharedPreferences
        String token = sharedPreferences.getString("token", null);
        // Use the retrieved token as needed
        if (token != null) {
            // Token is available, use it
            if (!token.isEmpty()) {
                setUserToken(token);
            }
        } /*else {
            // Token is not available
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }*/
    }
}