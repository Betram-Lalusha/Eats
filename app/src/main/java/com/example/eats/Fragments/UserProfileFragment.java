package com.example.eats.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.LoginActivity;
import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.Adapters.UserProfileAdapter;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class UserProfileFragment extends Fragment {

    TextView mUserBio;
    TextView mUserName;
    GridView mGridView;
    Button mLogOutButton;
    List<Post> mUserPosts;
    ParseUser mCurrentUser;
    ImageView mUserProfilePic;
    UserProfileAdapter mUserProfileAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        mUserPosts = new LinkedList<Post>();
        mUserBio = view.findViewById(R.id.bio);
        mCurrentUser = ParseUser.getCurrentUser();
        mUserName = view.findViewById(R.id.username);
        mGridView = view.findViewById(R.id.gridView);
        mLogOutButton = view.findViewById(R.id.logOutButton);
        mUserProfilePic = view.findViewById(R.id.userProfilePic);
        mUserProfileAdapter = new UserProfileAdapter(getContext(),0, mUserPosts);

        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mUserName.setText(mCurrentUser.getUsername());
        mUserBio.setText(mCurrentUser.getString("bio"));
        Glide.with(getContext()).load(mCurrentUser.getParseFile("userProfilePic").getUrl()).into(mUserProfilePic);

        mGridView.setAdapter(mUserProfileAdapter);

        //query for user posts
        getUserPosts();

    }

    private void getUserPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.whereEqualTo(Post.USER, ParseUser.getCurrentUser());
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
               // mScrollListener.resetState();
            }
        });
    }

}