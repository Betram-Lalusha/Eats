package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.eats.R;
import com.parse.ParseUser;

public class SettingsActivity extends AppCompatActivity {
    TextView mAboutEats;
    Switch mSwicthMode;
    Button mLogoutButton;
    TextView mPaymentInfo;
    TextView mChangeEmail;
    TextView mBecomeAVendor;
    TextView mPrivacyPolicy;
    TextView mDeleteAccount;
    TextView mChangeUserName;
    TextView mChangePassword;
    TextView mChangeNotifications;



    SharedPreferences mSharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();

        mAboutEats = findViewById(R.id.aboutEats);
        mPaymentInfo = findViewById(R.id.paymentInfo);
        mChangeEmail = findViewById(R.id.changeEmail);
        mLogoutButton = findViewById(R.id.logOutButton);
        mBecomeAVendor = findViewById(R.id.becomeAVendor);
        mDeleteAccount = findViewById(R.id.deleteAccount);
        mSwicthMode = findViewById(R.id.switchModeButton);
        mChangeUserName = findViewById(R.id.changeUserName);
        mChangePassword = findViewById(R.id.changePassword);
        mPrivacyPolicy = findViewById(R.id.viewPrivacyPolicy);
        mChangeNotifications = findViewById(R.id.changeNotifcations);

        //log out user
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //swicthing between dark mode and light mode
        mSharedPreferences = getSharedPreferences("night", 0);
        Boolean booleanValue = mSharedPreferences.getBoolean("night mode", false);

        if(booleanValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mSwicthMode.setChecked(true);
        }

        mSwicthMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    mSwicthMode.setChecked(true);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("night mode", true);
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    mSwicthMode.setChecked(false);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("night mode", false);
                    editor.commit();
                }
            }
        });

    }
}