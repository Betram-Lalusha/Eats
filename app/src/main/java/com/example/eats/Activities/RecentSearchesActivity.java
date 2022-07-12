package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.eats.Adapters.SearchResultsAdapter;
import com.example.eats.Models.City;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RecentSearchesActivity extends AppCompatActivity {

    ParseUser mCurrentUser;
    RecyclerView mRecyclerView;
    List<Post> mRetrievedSearchedPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_searches);

        mCurrentUser = ParseUser.getCurrentUser();
        mRecyclerView = findViewById(R.id.rvRecentSearches);

        SearchResultsAdapter searchResultsAdapter = new SearchResultsAdapter(this, mRetrievedSearchedPosts);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(searchResultsAdapter);

        mRetrievedSearchedPosts = getCachedPosts();
        searchResultsAdapter.addAll(mRetrievedSearchedPosts);
    }

    /**
     * Checks local database for cached posts
     * @return: all cached objects in the user local storage
     */
    private List<Post> getCachedPosts() {
        List<Post> retrievedPosts = new ArrayList<>();

        ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
        parseQuery.include(Post.USER);
        parseQuery.addDescendingOrder("createdAt");

        try {
            retrievedPosts = parseQuery.fromPin(mCurrentUser.getObjectId() + "searchedPosts").find();
            Log.d("cache","results for posts " + retrievedPosts);

        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        return  retrievedPosts;
    }

}