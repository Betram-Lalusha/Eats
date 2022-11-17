package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.eats.Activities.Vendors.ReadTsAndCsWindowActivity;
import com.example.eats.R;

public class BecomeAVendor extends AppCompatActivity {

    Button mViewPrivacyPolicyBtn;
    Button mActivateVendorAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_avendor);

        getSupportActionBar().hide();

        mViewPrivacyPolicyBtn = findViewById(R.id.viewTandCsButton);
        mActivateVendorAccountBtn = findViewById(R.id.activateAccountButton);

        mViewPrivacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BecomeAVendor.this, ReadTsAndCsWindowActivity.class));
                mActivateVendorAccountBtn.setVisibility(View.VISIBLE);
            }
        });

        mActivateVendorAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}