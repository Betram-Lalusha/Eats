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

public class ChangeUserName extends AppCompatActivity {
    EditText mOldUserName;
    EditText mNewUserName;
    ParseUser mCurrentUser;
    ProgressBar mSavingNewUserName;
    TextView mSavingNewUserNameText;
    ProgressBar mCheckingOldUserName;
    TextView mIncorrectUserNameError;
    Button mConfirmUserNameChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);

        getSupportActionBar().hide();

        mCurrentUser = ParseUser.getCurrentUser();
        mOldUserName = findViewById(R.id.oldUsername);
        mNewUserName = findViewById(R.id.enterNewUsername);
        mSavingNewUserName = findViewById(R.id.savingNewUserName);
        mCheckingOldUserName = findViewById(R.id.checkingOldUserName);
        mIncorrectUserNameError = findViewById(R.id.inCorrectUserName);
        mConfirmUserNameChangeButton = findViewById(R.id.saveNewUserName);
        mSavingNewUserNameText = findViewById(R.id.savingNewUserNameText);

        mOldUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    mNewUserName.setText("");
                    mNewUserName.setVisibility(View.INVISIBLE);
                    mCheckingOldUserName.setVisibility(View.INVISIBLE);
                    mIncorrectUserNameError.setVisibility(View.INVISIBLE);
                    mConfirmUserNameChangeButton.setVisibility(View.INVISIBLE);
                    return;
                }
                mCheckingOldUserName.setVisibility(View.VISIBLE);
                if(!mCurrentUser.getUsername().equals(s.toString())) {
                    mNewUserName.setText("");
                    mNewUserName.setVisibility(View.INVISIBLE);
                    mIncorrectUserNameError.setVisibility(View.VISIBLE);
                    mConfirmUserNameChangeButton.setVisibility(View.INVISIBLE);
                } else {
                    mNewUserName.setVisibility(View.VISIBLE);
                    mCheckingOldUserName.setVisibility(View.INVISIBLE);
                    mIncorrectUserNameError.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNewUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    mConfirmUserNameChangeButton.setVisibility(View.INVISIBLE);
                    return;
                }

                mConfirmUserNameChangeButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmUserNameChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserName();
            }
        });

    }

    private void changeUserName() {
        mSavingNewUserName.setVisibility(View.VISIBLE);
        mCurrentUser.setUsername(mNewUserName.getText().toString());

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    System.out.println("error changing user name " + e.getMessage());
                    mSavingNewUserNameText.setVisibility(View.VISIBLE);
                    mSavingNewUserNameText.setText(e.getMessage());
                    mSavingNewUserNameText.setTextColor(Color.RED);
                    mSavingNewUserName.setVisibility(View.INVISIBLE);
                    return;
                }
                mNewUserName.setText("");
                mOldUserName.setText("");
                mNewUserName.setVisibility(View.INVISIBLE);
                mSavingNewUserNameText.setVisibility(View.VISIBLE);
                mSavingNewUserNameText.setTextColor(Color.GREEN);
                mSavingNewUserName.setVisibility(View.INVISIBLE);
                mSavingNewUserNameText.setText("username changed");
            }
        });
    }
}