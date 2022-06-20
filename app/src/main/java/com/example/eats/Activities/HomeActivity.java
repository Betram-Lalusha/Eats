package com.example.eats.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.eats.Fragments.PostFragment;
import com.example.eats.Fragments.TimelineFragment;
import com.example.eats.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView mBottomNavigationView;
    final FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.homeButton:
                        fragment = new TimelineFragment();
                        break;
                    case R.id.food_nearyby:
                        fragment = new TimelineFragment();
                        break;
                    case R.id.postButton:
                        fragment = new PostFragment();
                        break;
                    case R.id.userProfile:
                        fragment = new TimelineFragment();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        break;
                }

                mFragmentManager.beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
                return true;
            }
        });

        mBottomNavigationView.setSelectedItemId(R.id.homeButton);
    }
}