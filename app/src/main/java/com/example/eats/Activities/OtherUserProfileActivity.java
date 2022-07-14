package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
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
    HashSet<String> mAlreadyAdded;
    UserProfileAdapter mUserProfileAdapter;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);

        Intent intent = getIntent();
        getSupportActionBar().hide();

        mAlreadyAdded = new HashSet<>();
        mUserPosts = new LinkedList<Post>();

        mCurrentUser = new ParseUser();
        mUserBio = findViewById(R.id.bio);
        mUserName = findViewById(R.id.username);
        mRecyclerView = findViewById(R.id.rvUserPosts);
        mRvProgressBar = findViewById(R.id.rvProgressBar);
        mUserProfilePic = findViewById(R.id.userProfilePic);
        mUserProfileAdapter = new UserProfileAdapter(this, mUserPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCurrentUser = Parcels.unwrap(intent.getParcelableExtra("user"));
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
                loadNextData();
                for(Post post: mUserPosts) mAlreadyAdded.add(post.getObjectId());
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);

        getUserPosts();
    }

    protected void getUserPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(5);
        query.include(Post.USER);
        query.whereEqualTo(Post.USER, mCurrentUser);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }

                // save received posts to list and notify adapter of new data
                mUserPosts.addAll(posts);
                mUserProfileAdapter.notifyDataSetChanged();
                mEndlessRecyclerViewScrollListener.resetState();

                for(Post post: posts) mAlreadyAdded.add(post.getObjectId());
                Log.d("INFINTE", "added " + mAlreadyAdded);
            }
        });
    }

    /**
     * Loads more posts from the database when infinte scroll is triggered.
     * Only posts already not in the cache are retrieved
     */
    private void loadNextData() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.whereNotContainedIn("objectId", mAlreadyAdded);
        query.whereEqualTo(Post.USER, mCurrentUser);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }

                mUserPosts.addAll(posts);
                mUserProfileAdapter.notifyDataSetChanged();
                mEndlessRecyclerViewScrollListener.resetState();

            }

        });
    }
}