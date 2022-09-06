package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eats.Emails.ValidEmailTester;
import com.example.eats.R;
import com.parse.ParseUser;

public class ChangeEmail extends AppCompatActivity {

    EditText mOldEmail;
    EditText mNewEmail;
    ParseUser mCurrentUser;
    EditText mConfirmNewEmail;
    ProgressBar mCheckingOldEmail;
    TextView mIncorrectEmailError;
    ValidEmailTester mValidEmailTester;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        getSupportActionBar().hide();

        mOldEmail = findViewById(R.id.oldEmail);
        mCurrentUser = ParseUser.getCurrentUser();
        mNewEmail = findViewById(R.id.enterNewEmail);
        mConfirmNewEmail = findViewById(R.id.confirmNewEmail);
        mCheckingOldEmail = findViewById(R.id.checkingOldEmail);
        mIncorrectEmailError = findViewById(R.id.inCorrectEmailError);

        mOldEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("old email " + mCurrentUser.getEmail());
                mCheckingOldEmail.setVisibility(View.VISIBLE);
//                if(!mCurrentUser.getEmail().equals(s)) {
//                    mIncorrectEmailError.setVisibility(View.VISIBLE);
//                } else {
//                    mIncorrectEmailError.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCheckingOldEmail.setVisibility(View.INVISIBLE);
            }
        });

        mNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}