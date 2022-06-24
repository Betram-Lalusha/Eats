package com.example.eats.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.Point;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;


public class TimelineFragment extends Fragment {

    List<Post> mPosts;
    Double mUserLatitude;
    Double mUserLongitude;
    PriorityQueue<Point> mQu;
    RecyclerView mRecyclerView;
    PostsAdapter mPostsAdapter;
    ProgressBar mPb;
    HashSet<Point> mAlreadyAdded;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get user coordinates passed from Home Activity
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);
        System.out.println("my lats " + mUserLatitude);
        System.out.println("my longs " + mUserLongitude);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mPosts = new ArrayList<>();
        mQu = new PriorityQueue<>();
        mAlreadyAdded = new HashSet<>();
        mPb = (ProgressBar) view.findViewById(R.id.pbLoading);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mPostsAdapter = new PostsAdapter(getContext(), mPosts);

        queryPosts();

        mRecyclerView.setAdapter(mPostsAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextPosts();
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);

    }

    private void loadNextPosts() {
        mPb.setVisibility(ProgressBar.VISIBLE);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereLessThan("createdAt", mPosts.get(mPosts.size() - 1).getDate());
        query.setLimit(4);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }
                for(Post post: posts) mQu.add(new Point(post, mUserLatitude, mUserLongitude));
                addAllPoints();
            }

        });

        //remove progress bar
        mPb.setVisibility(ProgressBar.INVISIBLE);
    }

    private void queryPosts() {
        //show progress bar
        mPb.setVisibility(ProgressBar.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(4);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying posts " + e);
                    return;
                }

                //scaling
                //user location privacy
                for(Post post: posts) mQu.add(new Point(post, 37.4219862, -122.0842771));

                addAllPoints();
            }
        });

        //remove progress bar
        mPb.setVisibility(ProgressBar.INVISIBLE);
    }

    private void addAllPoints() {
        List<Point> points = new LinkedList<>(mQu);
        for(Point point: points) {
            if(mAlreadyAdded.add(point)) mPosts.add(point.mPost);
        }
        mPostsAdapter.notifyDataSetChanged();
    }
}