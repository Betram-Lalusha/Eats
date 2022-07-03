package com.example.eats.Fragments;

import static com.example.eats.BuildConfig.MAPS_API_KEY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.eats.Adapters.CategoriesAdapter;
import com.example.eats.Adapters.SearchResultsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.Point;
import com.example.eats.Models.Place;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import okhttp3.Headers;

public class SearchFragment extends Fragment {


    List<Post> mPosts;
    ImageView mFeaturedImage;
    RecyclerView mRvCategories;
    RecyclerView mRvSearchItems;
    CategoriesAdapter mCategoriesAdapter;
    SearchResultsAdapter mSearchResultsAdapter;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private final String GOOGLE_PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";

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

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextPosts();
            }
        };

        mRvCategories.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        mRvSearchItems.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        queryPosts();
        getPlaces("LA");
    }

    private void loadNextPosts() {
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

                mPosts.addAll(posts);
            }

        });

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(6);
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

                Post featured = randomPost(posts.size(), posts);
                mPosts.addAll(posts);
                mCategoriesAdapter.notifyDataSetChanged();
                Glide.with(getContext()).load(featured.getMedia().getUrl()).into(mFeaturedImage);
            }
        });
    }

    /**
     * Returns a random element in the list to display as the featured item.
     * The item is removed from the list so that it is not repeated on the screen
     * @param size: The size of the list to consider
     * @param posts: the list of items to be used to retrieve a random post
     * @return a random post from the list
     */
    private Post randomPost(int size, List<Post> posts) {
        int upperBound = size;
        Random random = new Random();
        int index = random.nextInt(upperBound);
        return posts.remove(index);
    }

    /**
     * returns a place from the google maps api from a given search query
     * @param query: the name of the place to look for
     *
     */
    private void getPlaces(String query) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("key", MAPS_API_KEY);
        params.put("input", query);
        params.put("inputtype", "textquery");
        params.put("fields", "name");
        params.put("fields", "photos");

        //get data
        asyncHttpClient.get(GOOGLE_PLACES_API_BASE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                JSONArray possibleCandidates = null;
                try {
                    possibleCandidates = jsonObject.getJSONArray("candidates");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<JSONObject> candidates = getCandidates(possibleCandidates);
                List<Place> places = getAllPlaces(candidates,query); //will be fixed later, name of place should be set by response from google api not user query
                return;
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("GET-PLACES","failed because " + response);
                return;
            }
        });
    }

    /**
     * Function returns a list of all candidates from a given jsonArray
     * @param candidates: The json array of candidates
     * @return: a list of all candiates from the given jsonArray
     */
    private List<JSONObject> getCandidates(JSONArray candidates) {
        List<JSONObject> result = new LinkedList<>();
        for(int i = 0; i < candidates.length(); i++) {
            JSONObject candidate = null;
            try {
                candidate = candidates.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("GET-CANDIDATES", e.toString());
                e.printStackTrace();
            }
            result.add(candidate);
        }

        return result;
    }

    /**
     * Function returns a list of a places from a given list of candidates
     * @param candidates: a list of json objects representing candidates returned from google places API
     * @return:  a list of a places from a given list of candidates
     */
    private List<Place> getAllPlaces(List<JSONObject> candidates, String name) {
        List<Place> places = new LinkedList<>();
        for(JSONObject candidate: candidates) {
            Place place = Place.fromJson(candidate,name);
            places.add(place);
        }

        return places;
    }
}