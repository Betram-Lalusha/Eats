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

public class ChangeEmail extends AppCompatActivity {

    EditText mOldEmail;
    EditText mNewEmail;
    ParseUser mCurrentUser;
    TextView mChangingEmail;
    TextView mEmailsDontMatch;
    EditText mConfirmNewEmail;
    TextView mInvalidEmailError;
    ProgressBar mSavingNewEmail;
    ProgressBar mConfirmingEmail;
    ProgressBar mCheckingNewEmail;
    ProgressBar mCheckingOldEmail;
    TextView mIncorrectEmailError;
    Button mConfirmEmailChangeButton;
    ValidEmailTester mValidEmailTester = new ValidEmailTester();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        getSupportActionBar().hide();

        mOldEmail = findViewById(R.id.oldUserName);
        mCurrentUser = ParseUser.getCurrentUser();
        mNewEmail = findViewById(R.id.enterNewUserName);
        mChangingEmail = findViewById(R.id.changingUserName);
        mSavingNewEmail = findViewById(R.id.savingNewUserName);
        mEmailsDontMatch = findViewById(R.id.emailsDontMatch);
        mConfirmingEmail = findViewById(R.id.confirmingEmail);
        mConfirmNewEmail = findViewById(R.id.confirmNewEmail);
        mCheckingNewEmail = findViewById(R.id.checkingNewUserName);
        mCheckingOldEmail = findViewById(R.id.checkingOldUserName);
        mInvalidEmailError = findViewById(R.id.invalidEmailError);
        mIncorrectEmailError = findViewById(R.id.inCorrectUserNameError);
        mConfirmEmailChangeButton = findViewById(R.id.confirmUserNamelChangeButton);

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
                if(!mCurrentUser.getEmail().equals(s.toString())) {
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
                if(s.toString().isEmpty()) {
                    mEmailsDontMatch.setVisibility(View.INVISIBLE);
                    mConfirmingEmail.setVisibility(View.INVISIBLE);
                    mConfirmEmailChangeButton.setVisibility(View.INVISIBLE);
                    return;
                }

                mConfirmingEmail.setVisibility(View.VISIBLE);
                if(!mNewEmail.getText().toString().equals(s.toString())) {
                    mEmailsDontMatch.setVisibility(View.VISIBLE);
                    mConfirmingEmail.setVisibility(View.VISIBLE);
                    mConfirmEmailChangeButton.setVisibility(View.INVISIBLE);
                } else {
                    mEmailsDontMatch.setVisibility(View.INVISIBLE);
                    mConfirmingEmail.setVisibility(View.INVISIBLE);
                    mConfirmEmailChangeButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmEmailChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserEmail();
            }
        });
    }

    private void changeUserEmail() {
        mChangingEmail.setVisibility(View.VISIBLE);
        mSavingNewEmail.setVisibility(View.VISIBLE);

        mCurrentUser.setEmail(mConfirmNewEmail.getText().toString());

        //save info
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    mChangingEmail.setTextColor(Color.RED);
                    mChangingEmail.setText("The following error occured: " + e.getMessage() + ". Please try again");
                    mSavingNewEmail.setVisibility(View.INVISIBLE);
                    return;
                }

                //successful
                mNewEmail.setText("");
                mOldEmail.setText("");
                mConfirmNewEmail.setText("");
                mChangingEmail.setTextColor(Color.GREEN);
                mChangingEmail.setText("new email saved.");
                mNewEmail.setVisibility(View.INVISIBLE);
                mSavingNewEmail.setVisibility(View.INVISIBLE);
                mConfirmNewEmail.setVisibility(View.INVISIBLE);
                mConfirmEmailChangeButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}