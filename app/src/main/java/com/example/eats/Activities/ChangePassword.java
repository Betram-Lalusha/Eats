package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eats.Emails.ValidEmailTester;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ChangePassword extends AppCompatActivity {

    EditText mNewPassword;
    ParseUser mCurrentUser;
    TextView mPasswordsDontMatch;
    EditText mConfirmNewPassword;
    ProgressBar mSavingNewPassword;
    ProgressBar mConfirmingPassword;
    Button mConfirmPasswordChangeButton;
    TextView mChangingPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().hide();

        mCurrentUser = ParseUser.getCurrentUser();
        mNewPassword = findViewById(R.id.enterNewPassword);
        mSavingNewPassword = findViewById(R.id.savingNewPassWord);
        mConfirmNewPassword = findViewById(R.id.confirmNewPassword);
        mConfirmingPassword = findViewById(R.id.confirmingPassword);
        mChangingPasswordText = findViewById(R.id.changinPasswordText);
        mPasswordsDontMatch = findViewById(R.id.passwordsDontMatchError);
        mConfirmPasswordChangeButton = findViewById(R.id.changePasswordButton);

        mNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    mConfirmNewPassword.setText("");
                    mSavingNewPassword.setVisibility(View.INVISIBLE);
                    mPasswordsDontMatch.setVisibility(View.INVISIBLE);
                    mConfirmNewPassword.setVisibility(View.INVISIBLE);
                    mConfirmingPassword.setVisibility(View.INVISIBLE);
                    mConfirmPasswordChangeButton.setVisibility(View.INVISIBLE);
                    return;
                } else {
                    mConfirmNewPassword.setVisibility(View.VISIBLE);
                    mChangingPasswordText.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    mPasswordsDontMatch.setVisibility(View.VISIBLE);
                    mConfirmNewPassword.setVisibility(View.VISIBLE);
                    mConfirmingPassword.setVisibility(View.INVISIBLE);
                    mConfirmPasswordChangeButton.setVisibility(View.INVISIBLE);
                    return;
                }

                mConfirmingPassword.setVisibility(View.VISIBLE);
                if(!mConfirmNewPassword.getText().toString().equals(mNewPassword.getText().toString())) {
                    mPasswordsDontMatch.setVisibility(View.VISIBLE);
                    mConfirmingPassword.setVisibility(View.VISIBLE);
                    mSavingNewPassword.setVisibility(View.INVISIBLE);
                    mChangingPasswordText.setVisibility(View.INVISIBLE);
                    mConfirmPasswordChangeButton.setVisibility(View.INVISIBLE);
                } else {
                    mPasswordsDontMatch.setVisibility(View.INVISIBLE);
                    mConfirmingPassword.setVisibility(View.INVISIBLE);
                    mConfirmPasswordChangeButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmPasswordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserPassword();
            }
        });
    }

    private void changeUserPassword() {
        mSavingNewPassword.setVisibility(View.VISIBLE);
        mChangingPasswordText.setVisibility(View.VISIBLE);

        mCurrentUser.setPassword(mConfirmNewPassword.getText().toString());

        //save info
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    mChangingPasswordText.setTextColor(Color.RED);
                    mChangingPasswordText.setText("The following error occured: " + e.getMessage() + ". Please try again");
                    mSavingNewPassword.setVisibility(View.INVISIBLE);
                    return;
                }

                //successful
                mNewPassword.setText("");
                mConfirmNewPassword.setText("");
                mChangingPasswordText.setTextColor(Color.GREEN);
                mChangingPasswordText.setText("new password saved.");

                mSavingNewPassword.setVisibility(View.INVISIBLE);
                mConfirmNewPassword.setVisibility(View.INVISIBLE);
                mPasswordsDontMatch.setVisibility(View.INVISIBLE);
                mConfirmPasswordChangeButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}