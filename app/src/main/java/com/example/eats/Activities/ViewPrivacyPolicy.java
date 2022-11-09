package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.eats.R;

public class ViewPrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_privacy_policy);

        getSupportActionBar().hide();
    }
}