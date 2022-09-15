package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eats.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    TextView mUserName;
    Button mLoginButton;
    TextView mUserPassword;
    TextView mCreateAccount;
    TextView mErrorOccurred;
    ProgressBar mLoginProgressBar;
    private final String COMPLETED_ONBOARDING_PREF_NAME = "userSawTutorial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null) goToHome();

        mUserName = findViewById(R.id.userName);
        mErrorOccurred = findViewById(R.id.error);
        mLoginButton = findViewById(R.id.signInBtn);
        mUserPassword = findViewById(R.id.password);
        mCreateAccount = findViewById(R.id.createAccount);
        mLoginProgressBar = findViewById(R.id.loginProgressBar);

        getSupportActionBar().hide();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredName = mUserName.getText().toString();
                String enteredPassword = mUserPassword.getText().toString();

                authenticateUser(enteredName, enteredPassword);
            }
        });

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("cameFromDelete", false);
                startActivity(intent);
            }
        });

        //hide error message everytime user starts typing
        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorOccurred.setText("");
                mErrorOccurred.setVisibility(View.INVISIBLE);
            }
        });

        mUserPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorOccurred.setText("");
                mErrorOccurred.setVisibility(View.INVISIBLE);
            }
        });
    }

    //check if user exits and if entered password matches user password in db
    private void authenticateUser(String enteredName, String enteredPassword) {
        mLoginProgressBar.setVisibility(View.VISIBLE);
        //use parse to authenticate user
        ParseUser.logInInBackground(enteredName, enteredPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    mErrorOccurred.setText(e.getMessage());
                    mErrorOccurred.setVisibility(View.VISIBLE);
                    Log.i("LOGIN", "Login failed because " + e);
                    mLoginProgressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                mLoginProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "sucessfully logged in!", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                // Check if we need to display our OnboardingSupportFragment
                if (!sharedPreferences.getBoolean(
                       COMPLETED_ONBOARDING_PREF_NAME, false)) {
                    // The user hasn't seen the OnboardingSupportFragment yet, so show it
                    startActivity(new Intent(LoginActivity.this, OnBoardingActivity.class));
                    finish();
                } else {
                    goToHome();
                }
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, RequestLocation.class);
        startActivity(intent);
        finish();
    }
}