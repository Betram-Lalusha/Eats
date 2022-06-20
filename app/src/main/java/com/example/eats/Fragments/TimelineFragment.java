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

import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class TimelineFragment extends Fragment {

    List<Post> mPosts;
    RecyclerView mRecyclerView;
    PostsAdapter mPostsAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mPosts = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mPostsAdapter = new PostsAdapter(getContext(), mPosts);

        queryPosts();

        mRecyclerView.setAdapter(mPostsAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(4);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "somenthing went wrong querying posts " + e);
                    return;
                }

                mPosts.addAll(posts);
                mPostsAdapter.notifyDataSetChanged();
            }
        });
    }
}