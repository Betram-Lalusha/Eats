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
    String mOldUserEmail;
    ParseUser mCurrentUser;
    EditText mConfirmNewEmail;
    TextView mInvalidEmailError;
    ProgressBar mCheckingNewEmail;
    ProgressBar mCheckingOldEmail;
    TextView mIncorrectEmailError;
    ValidEmailTester mValidEmailTester = new ValidEmailTester();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        getSupportActionBar().hide();

        mOldEmail = findViewById(R.id.oldEmail);
        mCurrentUser = ParseUser.getCurrentUser();
        mOldUserEmail = mCurrentUser.getEmail();
        mNewEmail = findViewById(R.id.enterNewEmail);
        mConfirmNewEmail = findViewById(R.id.confirmNewEmail);
        mCheckingNewEmail = findViewById(R.id.checkingNewEmail);
        mCheckingOldEmail = findViewById(R.id.checkingOldEmail);
        mInvalidEmailError = findViewById(R.id.invalidEmailError);
        mIncorrectEmailError = findViewById(R.id.inCorrectEmailError);

        mOldEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    mNewEmail.setVisibility(View.INVISIBLE);
                    mCheckingOldEmail.setVisibility(View.INVISIBLE);
                    mIncorrectEmailError.setVisibility(View.INVISIBLE);
                    return;
                }
                mCheckingOldEmail.setVisibility(View.VISIBLE);
                if(!mOldUserEmail.equals(s.toString())) {
                    mNewEmail.setVisibility(View.INVISIBLE);
                    mIncorrectEmailError.setVisibility(View.VISIBLE);
                } else {
                    mNewEmail.setVisibility(View.VISIBLE);
                    mCheckingOldEmail.setVisibility(View.INVISIBLE);
                    mIncorrectEmailError.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    mConfirmNewEmail.setVisibility(View.INVISIBLE);
                    mCheckingNewEmail.setVisibility(View.INVISIBLE);
                    mInvalidEmailError.setVisibility(View.INVISIBLE);
                    return;
                }

                mCheckingNewEmail.setVisibility(View.VISIBLE);
                if(!mValidEmailTester.isValidEmail(s.toString())) {
                    mCheckingNewEmail.setVisibility(View.VISIBLE);
                    mInvalidEmailError.setVisibility(View.VISIBLE);
                    mConfirmNewEmail.setVisibility(View.INVISIBLE);
                } else {
                    mConfirmNewEmail.setVisibility(View.VISIBLE);
                    mInvalidEmailError.setVisibility(View.INVISIBLE);
                    mCheckingNewEmail.setVisibility(View.INVISIBLE);
                }
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