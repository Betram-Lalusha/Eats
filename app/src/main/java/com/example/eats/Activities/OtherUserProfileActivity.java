package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eats.Adapters.UserProfileAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.VerticalSpaceItemDecoration;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
    ImageView mUserProfilePic;
    RecyclerView mRecyclerView;
    ProgressBar mRvProgressBar;
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
        mRvProgressBar = findViewById(R.id.rvProgressBar);
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

    private void getUserPosts() {
        mRvProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(5);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");
        query.whereEqualTo(Post.USER, ParseUser.getCurrentUser());

        List<Post> posts = new LinkedList<>();
        try {
            posts = query.find();
            // save received posts to list and notify adapter of new data
            mUserPosts.addAll(posts);
            mUserProfileAdapter.notifyDataSetChanged();
            mUserProfileAdapter.addAll(posts);
            mRvProgressBar.setVisibility(View.INVISIBLE);

            //for(Post post: posts) mAlreadyAdded.add(post.getObjectId());

        } catch (ParseException e) {
            Log.i("HOME", "something went wrong obtaining posts " + e);
            mRvProgressBar.setVisibility(View.INVISIBLE);
            return;
        }


    }
}