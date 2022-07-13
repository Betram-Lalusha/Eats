package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eats.Adapters.UserProfileAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.VerticalSpaceItemDecoration;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity {

    TextView mUserBio;
    TextView mUserName;
    List<Post> mUserPosts;
    ParseUser mCurrentUser;
    ProgressBar mProgressBar;
    ImageView mUserProfilePic;
    RecyclerView mRecyclerView;
    UserProfileAdapter mUserProfileAdapter;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);

        mUserPosts = new LinkedList<Post>();
        mUserBio = findViewById(R.id.bio);
        mCurrentUser = ParseUser.getCurrentUser();
        mUserName = findViewById(R.id.username);
        mRecyclerView = findViewById(R.id.rvUserPosts);
        mProgressBar = findViewById(R.id.progressBar);
        mUserProfilePic = findViewById(R.id.userProfilePic);
        mUserProfileAdapter = new UserProfileAdapter(this, mUserPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(40);

        mUserName.setText(mCurrentUser.getUsername());
        mUserBio.setText(mCurrentUser.getString("bio"));
        Glide.with(this).load(mCurrentUser.getParseFile("userProfilePic").getUrl()).into(mUserProfilePic);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mUserProfileAdapter);
        //to add space between elements in recycler view
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                getUserPosts();
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }
}