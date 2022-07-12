package com.example.eats.Fragments;

import static com.example.eats.BuildConfig.MAPS_API_KEY;

import android.content.Intent;
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
import com.example.eats.Activities.RecentSearchesActivity;
import com.example.eats.Adapters.CategoriesAdapter;
import com.example.eats.Adapters.CitiesAdapter;
import com.example.eats.Adapters.SearchResultsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.DistanceCalculator;
import com.example.eats.Helpers.HorizontalSpaceItemDecorator;
import com.example.eats.Interface.OnClickInterface;
import com.example.eats.Models.City;
import com.example.eats.Models.Place;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    Double mUserLatitude;
    Double mUserLongitude;
    ParseUser mCurrentUser;
    RecyclerView mRvCities;
    List<Post> mCachedPosts;
    List<City> mCachedCities;
    ImageView mFeaturedImage;
    ImageButton mSearchButton;
    ImageButton mHistoryButton;
    RecyclerView mRvCategories;
    List<String> mAlreadyAdded;
    RecyclerView mRvSearchItems;
    List<Post> mPostsCategories;
    CitiesAdapter mCitiesAdapter;
    HashSet<String> mCitiesClicked;
    List<Post> mRetrievedCachedPosts;
    List<City> mRetrievedCachedCities;
    HashSet<String> mCategoriesClicked;
    List<Post> mRetrievedRecentSearches;
    CategoriesAdapter mCategoriesAdapter;
    HashSet<String> mCitiesAlreadyQueried;
    SearchResultsAdapter mSearchResultsAdapter;
    private OnClickInterface mOnClickInterface;
    private OnClickInterface mCityClickInterface;
    private DistanceCalculator mDistanceCalculator;
    EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    EndlessRecyclerViewScrollListener mCitiesEndlessRecyclerViewScrollListener;

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
        //get user coordinates passed from Home Activity
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);
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
        mCachedCities = new ArrayList<>();
        mAlreadyAdded = new ArrayList<>();
        mPostsCategories = new LinkedList<>();
        mCitiesAlreadyQueried = new HashSet<>();
        mRetrievedCachedPosts = new ArrayList<>();
        mRetrievedCachedCities = new ArrayList<>();

        mCurrentUser = ParseUser.getCurrentUser();
        mRvCities = view.findViewById(R.id.rvCities);
        mSearchBox = view.findViewById(R.id.searchBox);
        mSearchButton = view.findViewById(R.id.searchButton);
        mRvCategories = view.findViewById(R.id.rvCategories);
        mRvSearchItems = view.findViewById(R.id.rvSearchItems);
        mHistoryButton = view.findViewById(R.id.historyButton);

        mRvCities.setLayoutManager(linearLayoutManager3);
        mRvCategories.setLayoutManager(linearLayoutManager);
        mRvSearchItems.setLayoutManager(linearLayoutManager2);

        mFeaturedImage = view.findViewById(R.id.featuredImage);
        mSearchResultsAdapter = new SearchResultsAdapter(getContext(), mPosts);
        mCitiesAdapter = new CitiesAdapter(getContext(), mCities, mCityClickInterface);
        mDistanceCalculator = new DistanceCalculator("K", mUserLatitude, mUserLatitude);
        mCategoriesAdapter = new CategoriesAdapter(getContext(), mPostsCategories, mOnClickInterface);

        mRvCities.setAdapter(mCitiesAdapter);
        mRvCategories.setAdapter(mCategoriesAdapter);
        mRvSearchItems.setAdapter(mSearchResultsAdapter);

        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                queryPosts();
            }
        };

        mCitiesEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("QUERY", "triggered city scroll!");
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

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecentSearchesActivity.class);
                startActivity(intent);
            }
        });

        mRvCategories.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        mRvSearchItems.addOnScrollListener(mEndlessRecyclerViewScrollListener);
        mRvCities.addOnScrollListener(mCitiesEndlessRecyclerViewScrollListener);

        HorizontalSpaceItemDecorator horizontalSpaceItemDecorator = new HorizontalSpaceItemDecorator(40);
        mRvSearchItems.addItemDecoration(horizontalSpaceItemDecorator);

        //get data
        mRetrievedCachedPosts = getCachedPosts();
        mRetrievedCachedCities = getCachedCities();
        mRetrievedRecentSearches = getRecentSearches();

        if(mRetrievedCachedPosts.isEmpty()) {
            queryPosts();
        } else {
            mCategoriesAdapter.addAll(mRetrievedCachedPosts);
            mSearchResultsAdapter.addAll(mRetrievedCachedPosts);
        }

        if(mRetrievedCachedCities.isEmpty()) {
            queryCities();
        } else {
            mCitiesAdapter.addAll(mRetrievedCachedCities);
        }

    }

    /**
     * queries parse database for posts. The query ignores any posts already queried
     * by keeping track of a list object Ids already queried displayed on the scren
     */
    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(6);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("objectId", mAlreadyAdded);


        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
                    e.printStackTrace();
                    return;
                }
                if(!posts.isEmpty()) {
                    Post featured = randomPost(posts.size(), posts);
                    Glide.with(getContext()).load(featured.getMedia().getUrl()).into(mFeaturedImage);
                }

                for(Post post: posts) post.distanceFromUser = mDistanceCalculator.distance(post.getLatitude(), post.getLongiitude());
                mCachedPosts.addAll(posts);
                mCategoriesAdapter.addAll(posts);
                mSearchResultsAdapter.addAll(posts);



                //cache posts
                ParseObject.pinAllInBackground(mCurrentUser.getObjectId() + "searchedPosts", mCachedPosts);

                for(Post post: posts) mAlreadyAdded.add(post.getObjectId());
            }
        });
    }

    /**
     * Queries the parse databse for cities.All cities already cached or queried for are ignored
     * by the query.
     */
    private void queryCities() {
        ParseQuery<City> query = ParseQuery.getQuery(City.class);
        query.setLimit(4);
        query.addDescendingOrder("createdAt");
        query.whereNotContainedIn("name",mCitiesAlreadyQueried);

        List<City> cities = new ArrayList<>();
        try {
            cities = query.find();
            mCitiesAdapter.addAll(cities);

            //cache posts
            ParseObject.pinAllInBackground(mCurrentUser.getObjectId() + "cachedCities", cities);


            for(City city: cities) mCitiesAlreadyQueried.add(city.getName());
        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying  in search fragment " + e.toString());
            e.printStackTrace();
            return;
        }
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
                cacheResults(posts, null, true, false);
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
            mAlreadyAdded.clear();
            Log.d("HERE", "here " + mRetrievedCachedPosts);
            mSearchResultsAdapter.addAll(mRetrievedCachedPosts);
            for(Post post: mRetrievedCachedPosts)  mAlreadyAdded.add(post.getObjectId());
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
                cacheResults(posts, null,true, false);

            }
        });
    }

    private void filterByCity() {
        if(mCitiesClicked.isEmpty()) {
            mCitiesAdapter.clear();
            mAlreadyAdded.clear();
            mCitiesAlreadyQueried.clear();
            Log.d("TIRED", "retrieved " + mRetrievedCachedCities);
            mCitiesAdapter.addAll(mRetrievedCachedCities);
            for(City city: mRetrievedCachedCities) mCitiesAlreadyQueried.add(city.getName());
            mSearchResultsAdapter.addAll(mRetrievedCachedPosts);
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
                        //cache results
                        cacheResults(null, cities,false, true);
                    }
                });
            }
        });
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

    private List<City> getCachedCities() {
        List<City> retrievedCities = new ArrayList<>();

        ParseQuery<City> parseQuery = new ParseQuery<City>(City.class);
        parseQuery.addDescendingOrder("createdAt");

        try {
            retrievedCities = parseQuery.fromPin(mCurrentUser.getObjectId()  + "cachedCities").find();
            Log.d("cache","results for cities " + retrievedCities);
            for(City city: retrievedCities) {
                mCitiesAlreadyQueried.add(city.getName());
            }
        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying cached posts " + e.toString());
            e.printStackTrace();
            return retrievedCities;
        }
        return retrievedCities;
    }
    /**
     * Caches results obtained by network queries.
     * if 10 posts have already been cached, delete cache before adding more posts to save user space
     * @param posts: the posts to cache
     * @param cities: the cities to cache
     * @param cacheSearchResults: if true, then cache posts saved from results
     * @param cacheCitiesResults: if true then cache cities
     *    Note: only one of the booleans can be true at a time
     */
    private void cacheResults(List<Post> posts, List<City> cities, Boolean cacheSearchResults, Boolean cacheCitiesResults) {
        if(cacheSearchResults) {
            if(mRetrievedCachedPosts.size() >= 10) {
                mRetrievedCachedPosts.clear();
                ParseObject.unpinAllInBackground(mCurrentUser.getObjectId() +  "searchedPosts");
            }

            //cache searched results
            mCachedPosts.addAll(posts);
            ParseObject.pinAllInBackground(mCurrentUser.getObjectId() + "searchedPosts", mCachedPosts);

            //include in recent searches cache
            if(mRetrievedRecentSearches.size() >= 20) {
                ParseObject.unpinAllInBackground(mCurrentUser.getObjectId() + "recentSearches");
            }
            ParseObject.pinAllInBackground(mCurrentUser.getObjectId() + "recentSearches", posts);
        }

        if(cacheCitiesResults) {
            if(mRetrievedCachedCities.size() >= 10) {
                mRetrievedCachedCities.clear();
                ParseObject.unpinAllInBackground(mCurrentUser.getObjectId() + "cachedCities");
            }

            mCachedCities.addAll(cities);
            ParseObject.pinAllInBackground(mCurrentUser.getObjectId() + "cachedCities", cities);
        }
    }

    /**
     * Checks local database for cached posts that were searched for by the user.
     * @return: all cached objects in the user local storage that the user searched for.
     */
    private List<Post> getRecentSearches() {
        List<Post> retrievedPosts = new ArrayList<>();

        ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
        parseQuery.include(Post.USER);
        parseQuery.addDescendingOrder("createdAt");

        try {
            retrievedPosts = parseQuery.fromPin(mCurrentUser.getObjectId() + "recentSearches").find();
            Log.d("cache","results for posts " + retrievedPosts);

        } catch (ParseException e) {
            Log.i("QUERY", "something went wrong querying recent searched posts " + e.toString());
            e.printStackTrace();
            return retrievedPosts;
        }

        return  retrievedPosts;
    }
}