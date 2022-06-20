package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eats.Models.Post;
import com.example.eats.R;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    Post mPost;
    TextView mPrice;
    TextView mCaption;
    TextView mUserName;
    TextView mDescription;
    ImageView mPostImage;
    ImageView mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        mPrice = findViewById(R.id.price);
        mPostImage = findViewById(R.id.detailImage);
        mCaption = findViewById(R.id.detailCaption);
        mDescription= findViewById(R.id.description);
        mUserName = findViewById(R.id.detailUserName);
        mUserImage = findViewById(R.id.detailUserImage);
        mPost = (Post) Parcels.unwrap(intent.getParcelableExtra("post"));

        mPrice.setText(String.valueOf(mPost.getPrice()));
        mCaption.setText(mPost.getCaption());
        mDescription.setText(mPost.getDetails());
        mUserName.setText(mPost.getParseUser().getUsername());
        Glide.with(DetailActivity.this).load(mPost.getMedia().getUrl()).into(mPostImage);
        Glide.with(DetailActivity.this).load(mPost.getParseUser().getParseFile("userProfilePic").getUrl()).into(mUserImage);

    }
}