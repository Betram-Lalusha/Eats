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
import com.example.eats.Geohashing.Geohasher;
import com.example.eats.Helpers.VerticalSpaceItemDecoration;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class TimelineFragment extends Fragment {

    ProgressBar mPb;
    List<Post> mPosts;
    Geohasher mGeohasher;
    Double mUserLatitude;
    Double mUserLongitude;
    RecyclerView mRecyclerView;
    PostsAdapter mPostsAdapter;
    StringBuilder mUserGeoHash;
    HashSet<String> mAlreadyAdded;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mPosts = new ArrayList<>();
        mAlreadyAdded = new HashSet<>();
        mPb = (ProgressBar) view.findViewById(R.id.pbLoading);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mPostsAdapter = new PostsAdapter(getContext(), mPosts);
        mGeohasher = new Geohasher(mUserLatitude, mUserLongitude);
        mUserGeoHash = new StringBuilder(mGeohasher.geoHash(12));
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(40);

        queryPosts();

        mRecyclerView.setAdapter(mPostsAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //to add space between elements in recycler view
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
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
        query.setLimit(4);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");


        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("HOME", "something went wrong obtaining posts " + e);
                }
                for(Post post: posts) post.distanceFromUser = distance(post.getLatitude(), post.getLongiitude(), mUserLatitude, mUserLongitude, "K");
                mPostsAdapter.addAll(posts);
                Log.i("QUERY", "success querying posts23 " + mPosts.size());
            }

        });

        //remove progress bar
        mPb.setVisibility(ProgressBar.INVISIBLE);
    }

    private void queryPosts() {
        //get user hash
        //show progress bar
        mPb.setVisibility(ProgressBar.VISIBLE);
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(4);
        query.include(Post.USER);
        System.out.println("long " + mUserLongitude);
        System.out.println("lat " + mUserLatitude);
        System.out.println("long " + mUserLongitude);
        System.out.println(mUserGeoHash);
        query.addDescendingOrder("createdAt");

        //check for posts with user geo hash
        //if none found, keep removing last character until matching  geohash is found
        while(mUserGeoHash.length() > 0) {
            List<Post> posts = new LinkedList<>();
            try {
                query.whereStartsWith("geohash",mUserGeoHash.toString());
                posts = query.find();
                System.out.println("posts " + posts);
            } catch(ParseException e) {
                Log.i("QUERY", "something went wrong querying posts " + e.toString());
                e.printStackTrace();
                return;
           }
            if(posts.isEmpty()) {
                mUserGeoHash.deleteCharAt(mUserGeoHash.length() - 1);
            } else {
                for(Post post: posts) post.distanceFromUser = distance(post.getLatitude(), post.getLongiitude(), mUserLatitude, mUserLongitude, "K");
                mPostsAdapter.addAll(posts);
                break;
            }
        }

        //remove progress bar
        mPb.setVisibility(ProgressBar.INVISIBLE);
    }


    public  double distance(double pointLat, double pointLon, double userLat, double userLon,String unit) {
        if ((pointLat == userLat) && (pointLon == userLon)) {
            return 0;
        }
        else {
            double theta = pointLon - userLon;
            double dist = Math.sin(Math.toRadians(pointLat)) * Math.sin(Math.toRadians(userLat)) + Math.cos(Math.toRadians(pointLat)) * Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

}