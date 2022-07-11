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
import com.parse.ParseObject;
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
    List<Post> mCachedPosts;
    RecyclerView mRecyclerView;
    PostsAdapter mPostsAdapter;
    StringBuilder mUserGeoHash;
    HashSet<String> mAlreadyAdded;
    List<Post> mRetrievedCachedPosts;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("lifecycle","onCreateView invoked");
        //get user coordinates passed from Home Activity
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        Log.d("lifecycle","onViewCreated invoked");

        mPosts = new ArrayList<>();
        mAlreadyAdded = new HashSet<>();
        mCachedPosts = new ArrayList<>();
        mRetrievedCachedPosts = new ArrayList<>();
        mPb = (ProgressBar) view.findViewById(R.id.pbLoading);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mPostsAdapter = new PostsAdapter(getContext(), mPosts);
        mGeohasher = new Geohasher(mUserLatitude, mUserLongitude);
        mUserGeoHash = new StringBuilder(mGeohasher.geoHash(12));
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(40);



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
        mRetrievedCachedPosts = getCachedPosts();

        //only query for posts if cache is empty
        if(mRetrievedCachedPosts.isEmpty()) {
            getPosts(4);
        } else mPostsAdapter.addAll(mRetrievedCachedPosts);
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


    /**
     * Gets posts from the database that start with the current hash valu of the user's geo hash until either the number of required posts are found
     * or the user's has becomes empty
     * @param minNumber: the minimum number of posts to return from the databse
     */
    private void getPosts(int minNumber) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.setLimit(minNumber);
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("objectId", mAlreadyAdded);

        List<Post> posts = new LinkedList<>();
        while (mUserGeoHash.length() > 0 && posts.size() < minNumber) {
            try {
                query.whereStartsWith("geohash", mUserGeoHash.toString());
                posts = query.find();
            } catch (ParseException e) {
                Log.i("QUERY", "something went wrong querying posts " + e.toString());
                e.printStackTrace();
                return;
            }

            //if no posts with user geohash are found,remove last character and try again
            if (posts.isEmpty()) {
                mUserGeoHash.deleteCharAt(mUserGeoHash.length() - 1);
            } else {
                for(int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    if(!mAlreadyAdded.add(post.getObjectId())) {
                        posts.remove(post);
                        continue;
                    }
                    post.distanceFromUser = distance(post.getLatitude(), post.getLongiitude(), mUserLatitude, mUserLongitude,"K");
                }

                mCachedPosts.addAll(posts);
                mPostsAdapter.addAll(posts);

                //cache posts
                ParseObject.pinAllInBackground("cachedPosts", mCachedPosts);
            }
        }

    }

    public List<Post> getCachedPosts() {
        List<Post> retrievedPosts = new ArrayList<>();

        ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
        parseQuery.include(Post.USER);
        parseQuery.addDescendingOrder("createdAt");

        try {
            retrievedPosts = parseQuery.fromLocalDatastore().find();
            System.out.println("cached posts " + retrievedPosts);
            for(Post post: retrievedPosts) {
                mAlreadyAdded.add(post.getObjectId());
            }
        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        return  retrievedPosts;
    }

}