package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null) goToHome();

        mUserName = findViewById(R.id.userName);
        mLoginButton = findViewById(R.id.signInBtn);
        mUserPassword = findViewById(R.id.password);
        mCreateAccount = findViewById(R.id.createAccount);

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
                startActivity(intent);
            }
        });
    }

    //check if user exits and if entered password matches user password in db
    private void authenticateUser(String enteredName, String enteredPassword) {
        //use parse to authenticate user
        ParseUser.logInInBackground(enteredName, enteredPassword, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //TODO...show user errors tthat occured
                if (e != null) {
                    Log.i("LOGIN", "Login failed because " + e);
                    return;
                }

                Toast.makeText(LoginActivity.this, "sucessfully logged in!", Toast.LENGTH_SHORT).show();
                goToHome();
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}