package com.example.eats.Fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Geohashing.Geohasher;
import com.example.eats.Helpers.DistanceCalculator;
import com.example.eats.Helpers.VerticalSpaceItemDecoration;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class TimelineFragment extends Fragment {

    List<Post> mPosts;
    Geohasher mGeohasher;
    Double mUserLatitude;
    Double mUserLongitude;
    ParseUser mCurrentUser;
    List<Post> mCachedPosts;
    RecyclerView mRecyclerView;
    PostsAdapter mPostsAdapter;
    StringBuilder mUserGeoHash;
    HashSet<String> mAlreadyAdded;
    List<Post> mRetrievedCachedPosts;
    LottieAnimationView mLottieAnimationView;
    private DistanceCalculator mDistanceCalculator;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;


    private Handler mMainHandler = new Handler();
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
        mPosts = new ArrayList<>();
        mAlreadyAdded = new HashSet<>();
        mCachedPosts = new ArrayList<>();
        mRetrievedCachedPosts = new ArrayList<>();
        mCurrentUser = ParseUser.getCurrentUser();
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mPostsAdapter = new PostsAdapter(getContext(), mPosts);
        mLottieAnimationView = view.findViewById(R.id.animation);
        mGeohasher = new Geohasher(mUserLatitude, mUserLongitude);
        mUserGeoHash = new StringBuilder(mGeohasher.geoHash(12));
        mDistanceCalculator = new DistanceCalculator("K",mUserLatitude, mUserLongitude);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(40);



        Log.d("TIMELINE GEO", "user geohash " + mUserGeoHash);
        Log.d("TIMELINE GEO", "user coordinates " + mUserLatitude + ", " + mUserLongitude);
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
                getPosts(4);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        //setRandomAnimation();
        //only query for posts if cache is empty
        BackgroundThread backgroundThread = new BackgroundThread();
        new Thread(backgroundThread).start();
    }

    /**
     * Gets posts from the database that start with the current hash valu of the user's geo hash until either the number of required posts are found
     * or the user's hash becomes empty
     * @param minNumber: the minimum number of posts to return from the databse
     */
    private void getPosts(int minNumber) {
        mLottieAnimationView.setVisibility(View.VISIBLE);
        mLottieAnimationView.playAnimation();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.setLimit(minNumber);
        query.addDescendingOrder("createdAt");
        query.whereNotEqualTo("user", mCurrentUser);
        query.whereNotContainedIn("objectId", mAlreadyAdded);

        List<Post> posts = new LinkedList<>();
        while (mUserGeoHash.length() > 0 && posts.size() < minNumber) {
            try {
                query.whereStartsWith("geohash", mUserGeoHash.toString());
                posts = query.find();
            } catch (ParseException e) {
                Log.i("QUERY", "something went wrong querying posts " + e.toString());
                e.printStackTrace();
                //update UI thread
                mLottieAnimationView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLottieAnimationView.cancelAnimation();
                        mLottieAnimationView.setVisibility(View.INVISIBLE);
                    }
                });
                return;
            }

            //if no posts with user geohash are found,remove last character and try again
            if (posts.isEmpty() && mUserGeoHash.length() > 0) {
                mUserGeoHash.deleteCharAt(mUserGeoHash.length() - 1);
            } else {
                for(int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    if(!mAlreadyAdded.add(post.getObjectId())) {
                        posts.remove(post);
                        continue;
                    }
                    post.distanceFromUser = mDistanceCalculator.distance(post.getLatitude(), post.getLongiitude());
                }

                //update UI thread
                List<Post> finalPosts = posts;
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCachedPosts.addAll(finalPosts);
                        mPostsAdapter.addAll(finalPosts);

                        //cache posts
                        //if cached posts size is 10, remove them and update with new ones
                        //this prevents user seen the exact same posts every time they login
                        if(mRetrievedCachedPosts.size() >= 10) {
                            ParseObject.unpinAllInBackground(mCurrentUser.getObjectId() + "cachedPosts");
                        }
                        ParseObject.pinAllInBackground(mCurrentUser.getObjectId() + "cachedPosts", mCachedPosts);
                    }
                });
            }
        }
        //update UI thread
        mLottieAnimationView.post(new Runnable() {
            @Override
            public void run() {
                mLottieAnimationView.cancelAnimation();
                mLottieAnimationView.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Checks local database for cached posts
     * @return: all cached objects in the user local storage
     */
    public List<Post> getCachedPosts() {
        List<Post> retrievedPosts = new ArrayList<>();

        ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
        parseQuery.include(Post.USER);
        parseQuery.addDescendingOrder("createdAt");

        try {
            retrievedPosts = parseQuery.fromPin(mCurrentUser.getObjectId() + "cachedPosts").find();
            Log.d("cache","results " + retrievedPosts);
            for(Post post: retrievedPosts) {
                mAlreadyAdded.add(post.getObjectId());
                post.distanceFromUser = mDistanceCalculator.distance(post.getLatitude(), post.getLongiitude());
            }


        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        return  retrievedPosts;
    }

    /**
     * sets a random lottie animation to use as data loads in the background thread
     */
    private void setRandomAnimation() {
        int min = 1;
        int max = 2;
        Random random = new Random();
        int index =  random.nextInt((max - min) + 1) + min;
        System.out.println("index " + index);
        if(index <= 1) {
            mLottieAnimationView.setAnimation(R.raw.mixing_egg_into_flour);
        }
    }

    private class BackgroundThread implements Runnable {
        @Override
        public void run() {
            if(mRetrievedCachedPosts.isEmpty()) {
                getPosts(4);
            } else {
                mRetrievedCachedPosts = getCachedPosts();
                mPostsAdapter.addAll(mRetrievedCachedPosts);
            }

        }
    }

}