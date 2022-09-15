package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eats.Models.User;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    int mDefaultId;
    TextView mSetEmail;
    Button mSignUpButton;
    TextView mSetPassword;
    TextView mSetUserName;
    ImageView mSetUserPfp;
    private File mPhotoFile;
    TextView mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        mSetEmail = findViewById(R.id.setEmail);
        mSetPassword = findViewById(R.id.setPassword);
        mSetUserName = findViewById(R.id.setUserName);
        mSetUserPfp = findViewById(R.id.setUserfp);
        mSignUpButton = findViewById(R.id.signUpButton);
        mConfirmPassword = findViewById(R.id.confirmPassword);

        mSetUserPfp.setTag(R.drawable.default_image);
        mDefaultId = (int) mSetUserPfp.getTag();

        mSetUserPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUpActivity.this, "change this later by visiting your profile", Toast.LENGTH_LONG).show();
                //onPickPhoto(v);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = mSetEmail.getText().toString();
                String userName = mSetUserName.getText().toString();
                String userPassword = mSetPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();

                if(userEmail.isEmpty() || userName.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "ensure all fields are filled in!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!userPassword.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "passwords must match", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveUser(userEmail, userName, userPassword, mPhotoFile);
            }
        });

    }

    private void saveUser(String userEmail, String userName, String password, File photoFile) {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(userEmail);


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    goToOnBoarding();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void goToOnBoarding() {
        Intent intent = new Intent(this, OnBoardingActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if(intent == null ) {
            return;
        }

        Boolean fromDelete = intent.getBooleanExtra("cameFromDelete", false);
        if(fromDelete) {
            Intent intent2 = new Intent(SignUpActivity.this, SignUpActivity.class);
            intent2.putExtra("cameFromDelete", true);
            startActivity(intent2);
            finish();
        } else {
            Intent intent2 = new Intent(SignUpActivity.this, LoginActivity.class);
            intent2.putExtra("cameFromDelete", false);
            startActivity(intent2);
        }

    }

}