package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.eats.R;

public class ChangeEmail extends AppCompatActivity {

    EditText mOldEmail;
    EditText mNewEmail;
    EditText mConfirmNewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        getSupportActionBar().hide();

        mOldEmail = findViewById(R.id.oldEmail);
        mNewEmail = findViewById(R.id.enterNewEmail);
        mConfirmNewEmail = findViewById(R.id.confirmNewEmail);

    }
}