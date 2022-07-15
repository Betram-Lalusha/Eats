package com.example.eats.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.view.MenuItem;


import com.example.eats.Fragments.MapFragment;
import com.example.eats.Fragments.PostFragment;
import com.example.eats.Fragments.SearchFragment;
import com.example.eats.Fragments.TimelineFragment;
import com.example.eats.Fragments.UserProfileFragment;
import com.example.eats.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    
    Location mLastLocation;
    BottomNavigationView mBottomNavigationView;
    FusedLocationProviderClient mFusedLocationClient;
    final FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        mLastLocation = intent.getParcelableExtra("userLastLocation");
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mBottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                bundle.putDouble("userLat", mLastLocation == null ?  40.730610: mLastLocation.getLatitude());
                bundle.putDouble("userLong",mLastLocation == null ? -73.935242 : mLastLocation.getLongitude());
                switch (item.getItemId()) {
                    case R.id.homeButton:
                        fragment = new TimelineFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.food_nearyby:
                        fragment = new MapFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.postButton:
                        fragment = new PostFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.userProfile:
                        fragment = new UserProfileFragment();
                        break;
                    case R.id.exploreButton:
                        fragment = new SearchFragment();
                        fragment.setArguments(bundle);
                        break;
                    default:
                        fragment = new SearchFragment();
                        break;
                }

                mFragmentManager.beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
                return true;
            }
        });

        mBottomNavigationView.setSelectedItemId(R.id.exploreButton);
    }

}