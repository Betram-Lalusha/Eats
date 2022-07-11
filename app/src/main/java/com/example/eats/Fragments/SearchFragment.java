package com.example.eats.Fragments;

import static com.example.eats.BuildConfig.MAPS_API_KEY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.eats.Adapters.CategoriesAdapter;
import com.example.eats.Adapters.CitiesAdapter;
import com.example.eats.Adapters.SearchResultsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Interface.OnClickInterface;
import com.example.eats.Models.City;
import com.example.eats.Models.Place;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import okhttp3.Headers;

public class SearchFragment extends Fragment {


    List<Post> mPosts;
    List<City> mCities;
    TextView mSearchBox;
    RecyclerView mRvCities;
    List<Post> mCachedPosts;
    ImageView mFeaturedImage;
    ImageButton mSearchButton;
    RecyclerView mRvCategories;
    List<String> mAlreadyAdded;
    RecyclerView mRvSearchItems;
    List<Post> mPostsCategories;
    CitiesAdapter mCitiesAdapter;
    HashSet<String> mCitiesClicked;
    List<Post> mRetrievedCachedPosts;
    HashSet<String> mCategoriesClicked;
    CategoriesAdapter mCategoriesAdapter;
    HashSet<String> mCitiesAlreadyQueried;
    SearchResultsAdapter mSearchResultsAdapter;
    private OnClickInterface mOnClickInterface;
    private OnClickInterface mCityClickInterface;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    EndlessRecyclerViewScrollListener mCitiesEndlessRecyclerViewScrollListener;
    private final String GOOGLE_PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategoriesClicked = new HashSet<>();

        //set on click interface
        mOnClickInterface = new OnClickInterface() {
            @Override
            public void setClick(String category) {
                if(!mCategoriesClicked.add(category)) {
                    mCategoriesClicked.remove(category);
                }

                filterByCategory();
            }
        };

        mCityClickInterface = new OnClickInterface() {
            @Override
            public void setClick(String item) {
                if(!mCitiesClicked.add(item)) {
                    mCitiesClicked.remove(item);
                }

                filterByCity();
            }
        };
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
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);

        mPosts = new LinkedList<>();
        mCities = new LinkedList<>();
        mCitiesClicked = new HashSet<>();
        mCachedPosts = new ArrayList<>();
        mAlreadyAdded = new ArrayList<>();
        mPostsCategories = new LinkedList<>();
        mCitiesAlreadyQueried = new HashSet<>();
        mRetrievedCachedPosts = new ArrayList<>();

        mRvCities = view.findViewById(R.id.rvCities);
        mSearchBox = view.findViewById(R.id.searchBox);
        mSearchButton = view.findViewById(R.id.searchButton);
        mRvCategories = view.findViewById(R.id.rvCategories);
        mRvSearchItems = view.findViewById(R.id.rvSearchItems);

        mRvCities.setLayoutManager(linearLayoutManager3);
        mRvCategories.setLayoutManager(linearLayoutManager);
        mRvSearchItems.setLayoutManager(linearLayoutManager2);

        mFeaturedImage = view.findViewById(R.id.featuredImage);
        mCitiesAdapter = new CitiesAdapter(getContext(), mCities, mCityClickInterface);
        mSearchResultsAdapter = new SearchResultsAdapter(getContext(), mPosts);
        mCategoriesAdapter = new CategoriesAdapter(getContext(), mPostsCategories, mOnClickInterface);

        mRvCities.setAdapter(mCitiesAdapter);
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

        mCitiesEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryCities();
            }
        };


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing if there is no entered text
                if(mSearchBox.getText().toString().isEmpty()) return;

                searchDb(mSearchBox.getText().toString());
                mSearchBox.setText("");
            }
        });

        mRvCategories.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        mRvSearchItems.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        mRvCities.addOnScrollListener(mCitiesEndlessRecyclerViewScrollListener);

        //get data
        mRetrievedCachedPosts = getCachedPosts();
        if(mRetrievedCachedPosts.isEmpty()) {
            queryPosts();
        } else {
            mCategoriesAdapter.addAll(mRetrievedCachedPosts);
            mSearchResultsAdapter.addAll(mRetrievedCachedPosts);
        }

        queryCities();
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

                mCategoriesAdapter.addAll(posts);
                mSearchResultsAdapter.addAll(posts);
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

                mCachedPosts.addAll(posts);
                mCategoriesAdapter.addAll(posts);
                mSearchResultsAdapter.addAll(posts);

                Glide.with(getContext()).load(featured.getMedia().getUrl()).into(mFeaturedImage);

                //cache posts
                ParseObject.pinAllInBackground("searchedPosts", mCachedPosts);
            }
        });
    }

    private void queryCities() {
        ParseQuery<City> query = ParseQuery.getQuery(City.class);
        query.setLimit(4);
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("name",mCitiesAlreadyQueried);

        query.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> cities, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
                    e.printStackTrace();
                    return;
                }
                mCitiesAdapter.addAll(cities);

                for(City city: cities) mCitiesAlreadyQueried.add(city.getName());
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


    /**
     * Searches parse db for given user query. It does this by finding captions that contain
     * the input query
     * @param userQuery: the string to look for in the db
     */
    private void searchDb(String userQuery) {
        //Query for captions that starts with user query
        ParseQuery<Post> captionQuery = new ParseQuery<Post>(Post.class);
        captionQuery.whereStartsWith(Post.CAPTION, userQuery);

        //Query for descriptions that starts with user query
        ParseQuery<Post> detailsQuery = new ParseQuery<Post>(Post.class);;
        detailsQuery.whereStartsWith(Post.DETAILS, userQuery);

        //Query for category whose name that starts with user query
        ParseQuery<Post> categoryQuery = new ParseQuery<Post>(Post.class);
        categoryQuery.whereStartsWith(Post.CATEGORY, userQuery);


        List<ParseQuery<Post>> queries = new ArrayList<ParseQuery<Post>>();
        queries.add(captionQuery);
        queries.add(detailsQuery);
        queries.add(categoryQuery);

        ParseQuery<Post> mainQuery = ParseQuery.or(queries);
        mainQuery.setLimit(12);
        mainQuery.include(Post.USER);
        mainQuery.addDescendingOrder("createdAt");

        //caching searched queries
        mainQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("SEARCH-DB", e.toString());
                    e.printStackTrace();
                    return;
                }

                if(posts.isEmpty()) {
                    Toast.makeText(getContext(), "no results for " + userQuery, Toast.LENGTH_LONG).show();
                    return;
                }
                //remove current posts
                mSearchResultsAdapter.clear();

                //add new ones
                mSearchResultsAdapter.addAll(posts);
                //cache results
                cacheResults(posts, true, false);
            }
        });

    }

    /**
     * Function queries db for posts that have categories equal to that selected by user.
     * If user has not selected any categories, queryPosts is called instead to populate the screen with featured items
     */
    private void filterByCategory() {
        if(mCategoriesClicked.isEmpty()) {
            mSearchResultsAdapter.clear();
            queryPosts();
            return;
        }

        //query for posts that have categories selected
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(16);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");
        query.whereContainedIn(Post.CATEGORY, mCategoriesClicked);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
                    e.printStackTrace();
                    return;
                }

                mSearchResultsAdapter.clear();
                mSearchResultsAdapter.addAll(posts);
                //cache results
                cacheResults(posts, true, false);

            }
        });
    }

    private void filterByCity() {
        if(mCitiesClicked.isEmpty()) {
            mCitiesAdapter.clear();
            queryCities();
            queryPosts();
            return;
        }

        ParseQuery<City> query = ParseQuery.getQuery(City.class);
        query.setLimit(6);
        query.addDescendingOrder("createdAt");
        query.whereContainedIn("name", mCitiesClicked);

        query.findInBackground(new FindCallback<City>() {
            @Override
            public void done(List<City> cities, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
                    e.printStackTrace();
                    return;
                }

                mCitiesAdapter.clear();
                mCitiesAdapter.addAll(cities);

                ParseQuery<Post> postQuery = ParseQuery.getQuery(Post.class);
                postQuery.setLimit(6);
                postQuery.include(Post.USER);
                postQuery.addDescendingOrder("createdAt");
                postQuery.whereContainedIn("city", cities);

                postQuery.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if(e != null) {
                            Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
                            e.printStackTrace();
                            return;
                        }

                        mSearchResultsAdapter.clear();
                        mSearchResultsAdapter.addAll(posts);
                    }
                });
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
            retrievedPosts = parseQuery.fromPin("searchedPosts").find();
            System.out.println("cached search results " + retrievedPosts);
            for(Post post: retrievedPosts) {
                mAlreadyAdded.add(post.getObjectId());
            }
        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        if(!retrievedPosts.isEmpty()) {
            Post featured = randomPost(retrievedPosts.size(),retrievedPosts);
            Glide.with(getContext()).load(featured.getMedia().getUrl()).into(mFeaturedImage);
        }
        return  retrievedPosts;
    }

    /**
     * Caches results obtained by network queries
     * @param posts: the posts to cache
     * @param cacheSearchResults: if true, then cache posts saved from results
     * @param cacheCitiesResults: if true then cache cities
     */
    private void cacheResults(List<Post> posts, Boolean cacheSearchResults, Boolean cacheCitiesResults) {
        //if 10 posts are cached, delete cache before adding more posts to save user space
        if(cacheSearchResults) {
            if(mCachedPosts.size() >= 10) {
                mCachedPosts.clear();
                ParseObject.unpinAllInBackground("searchedPosts");
            }

            //cache searched results
            mCachedPosts.addAll(posts);
            ParseObject.pinAllInBackground("searchedPosts", mCachedPosts);
        }
    }
}