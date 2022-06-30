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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.eats.Adapters.CategoriesAdapter;
import com.example.eats.Adapters.SearchResultsAdapter;
import com.example.eats.Helpers.Point;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {


    List<Post> mPosts;
    ImageView mFeaturedImage;
    RecyclerView mRvCategories;
    RecyclerView mRvSearchItems;
    CategoriesAdapter mCategoriesAdapter;
    SearchResultsAdapter mSearchResultsAdapter;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);


    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);

        mPosts = new LinkedList<>();
        mRvCategories = view.findViewById(R.id.rvCategories);
        mRvSearchItems = view.findViewById(R.id.rvSearchItems);

        mRvCategories.setLayoutManager(linearLayoutManager);
        mRvSearchItems.setLayoutManager(linearLayoutManager2);

        mFeaturedImage = view.findViewById(R.id.featuredImage);
        mCategoriesAdapter = new CategoriesAdapter(getContext(), mPosts);
        mSearchResultsAdapter = new SearchResultsAdapter(getContext(), mPosts);

        mRvCategories.setAdapter(mCategoriesAdapter);
        mRvSearchItems.setAdapter(mSearchResultsAdapter);

        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(5);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
                    e.printStackTrace();
                    return;
                }
                mPosts.addAll(posts);
                mCategoriesAdapter.notifyDataSetChanged();
                Glide.with(getContext()).load(posts.get(0).getMedia().getUrl()).into(mFeaturedImage);
            }
        });
    }
}