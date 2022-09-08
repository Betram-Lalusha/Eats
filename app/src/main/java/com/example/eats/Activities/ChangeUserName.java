package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eats.Emails.ValidEmailTester;
import com.example.eats.R;
import com.parse.ParseUser;

public class ChangeUserName extends AppCompatActivity {
    EditText mOldUserName;
    EditText mNewUserName;
    ParseUser mCurrentUser;
    TextView mInvalidEmailError;
    ProgressBar mSavingNewUserName;
    ProgressBar mCheckingOldUserName;
    TextView mIncorrectUserNameError;
    Button mConfirmUserNameChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);

    }
}