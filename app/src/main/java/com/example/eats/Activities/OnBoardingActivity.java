package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.eats.R;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

public class OnBoardingActivity extends AppCompatActivity {

    Button mDoneButton;
    private FragmentManager mFragmentManager;
    private final String COMPLETED_ONBOARDING_PREF_NAME = "userSawTutorial";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        mDoneButton = findViewById(R.id.doneButton);
        mFragmentManager = getSupportFragmentManager();

        getSupportActionBar().hide();

        final PaperOnboardingFragment paperOnboardingFragment = PaperOnboardingFragment.newInstance(getDataForOnBoarding());
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame_layout, paperOnboardingFragment);

        fragmentTransaction.commit();

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnBoardingActivity.this, RequestLocation.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private ArrayList<PaperOnboardingPage> getDataForOnBoarding() {
        // the first string is to show the main title ,
        // second is to show the message below the
        // title, then color of background is passed ,
        // then the image to show on the screen is passed
        // and at last icon to navigate from one screen to other
        PaperOnboardingPage source = new PaperOnboardingPage("Eats", "We're so glad you're here", Color.parseColor("#ffb174"),R.drawable.apple, R.drawable.eats_logo);
        PaperOnboardingPage source1 = new PaperOnboardingPage("TimeLine", "Following Accounts", Color.parseColor("#22eaaa"),R.drawable.timeline_tut_1, R.drawable.default_image);
        PaperOnboardingPage source2 = new PaperOnboardingPage("TimeLine", "Followed Accounts", Color.parseColor("#22eaaa"),R.drawable.timeline_tut_2, R.drawable.default_image);
        PaperOnboardingPage source3 = new PaperOnboardingPage("Nearby Spots", "See eats spots near you", Color.parseColor("#ee5a5a"),R.drawable.map_frag_tut, R.drawable.icons8_distance_64);
        PaperOnboardingPage source4 = new PaperOnboardingPage("Create", "How to add an Image", Color.parseColor("#fce803"), R.drawable.post_frag_tut, R.drawable.chef_311369);
        PaperOnboardingPage source5 = new PaperOnboardingPage("Options", "Take new picture or select from gallery", Color.parseColor("#fce803"), R.drawable.smaller, R.drawable.chef_311369);
        PaperOnboardingPage source6 = new PaperOnboardingPage("Search", "Filter items by category", Color.parseColor("#03fcfc"), R.drawable.search_frag_3, R.drawable.ic_baseline_explore_24);
        PaperOnboardingPage source7 = new PaperOnboardingPage("Search", "Filter items by city", Color.parseColor("#03fcfc"), R.drawable.filter_by_city, R.drawable.ic_baseline_explore_24);

        // array list is used to store
        // data of onbaording screen
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();

        // all the sources(data to show on screens)
        // are added to array list
        elements.add(source);
        elements.add(source1);
        elements.add(source2);
        elements.add(source3);
        elements.add(source4);
        elements.add(source5);
        elements.add(source6);
        return elements;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // User has seen OnboardingSupportFragment, so mark our SharedPreferences
        // flag as completed so that we don't show our OnboardingSupportFragment
        // the next time the user launches the app.
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        sharedPreferencesEditor.putBoolean(
                COMPLETED_ONBOARDING_PREF_NAME, true);
        sharedPreferencesEditor.apply();
    }
}