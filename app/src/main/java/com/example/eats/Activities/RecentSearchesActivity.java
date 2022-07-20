package com.example.eats.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.eats.Adapters.SearchResultsAdapter;
import com.example.eats.Helpers.HorizontalSpaceItemDecorator;
import com.example.eats.Helpers.VerticalSpaceItemDecoration;
import com.example.eats.Models.City;
import com.example.eats.Models.Post;
import com.example.eats.Models.RecentSearchedItem;
import com.example.eats.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecentSearchesActivity extends AppCompatActivity {

    ParseUser mCurrentUser;
    RecyclerView mRecyclerView;
    List<Post> mRetrievedSearchedPosts;
    List<RecentSearchedItem> mRetrievedRecentSearchedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_searches);

        mCurrentUser = ParseUser.getCurrentUser();
        mRetrievedSearchedPosts = new LinkedList<>();
        mRecyclerView = findViewById(R.id.rvRecentSearches);

        getSupportActionBar().setTitle("Recent Searches");
        SearchResultsAdapter searchResultsAdapter = new SearchResultsAdapter(this, mRetrievedSearchedPosts);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(10);
        HorizontalSpaceItemDecorator horizontalSpaceItemDecorator = new HorizontalSpaceItemDecorator(10);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(searchResultsAdapter);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
        mRecyclerView.addItemDecoration(horizontalSpaceItemDecorator);

       // mRetrievedSearchedPosts = getCachedPosts();
        mRetrievedRecentSearchedItems = getCachedPosts();
        for(RecentSearchedItem recentSearchedItem: mRetrievedRecentSearchedItems) {
            mRetrievedSearchedPosts.add(recentSearchedItem.getPost());
            searchResultsAdapter.notifyDataSetChanged();
        }

        Log.d("ACT", "retr " + mRetrievedSearchedPosts);
    }

    /**
     * Checks local database for cached posts that were searched for by the user.
     * @return: all cached objects in the user local storage that the user searched for.
     */
    private List<RecentSearchedItem> getCachedPosts() {
        List<RecentSearchedItem> retrievedPosts = new ArrayList<>();

        ParseQuery<RecentSearchedItem> parseQuery = new ParseQuery<RecentSearchedItem>(RecentSearchedItem.class);
        parseQuery.include(RecentSearchedItem.POST);
        parseQuery.addDescendingOrder("position");

        try {
            retrievedPosts = parseQuery.fromPin(mCurrentUser.getObjectId() + "recentSearches").find();
            Log.d("cache","results for posts " + retrievedPosts);

        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached recent searched posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        return  retrievedPosts;
    }

}