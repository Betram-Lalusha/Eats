package com.example.eats.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.eats.Activities.DetailActivity;
import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.Helpers.Point;
import com.example.eats.Models.Post;
import com.example.eats.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class MapFragment extends Fragment {

    List<Post> mPosts;
    Double mUserLatitude;
    Double mUserLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        mPosts = new LinkedList<Post>();
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);

        //handle exception thrown by getBitMapFromLink
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);


        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //GET POSTS and add markers
                queryPosts(googleMap);

                // Add a marker at current signed in user location,
                // and move the map's camera to the same location.
                LatLng userLoc = new LatLng(mUserLatitude, mUserLongitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(userLoc)
                        .title("me")
                        .zIndex(1.0f));

                //listen to click events on markers
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //if the marker is the current signed in user, do nothing
                        if(marker.getTitle().equals("me")) return false;

                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtra("post", Parcels.wrap(marker.getTag()));
                        startActivity(intent);
                        return false;
                    }
                });
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
                googleMap.setMinZoomPreference(15);

            }
        });
    }

    /***
     * Method converts a resource at a given url path into a bitmap
     * @param remotePath: the url path to the remote image
     * @return the bitmap representing the image\
     * Code Aopted from StackOverflow: https://stackoverflow.com/questions/22139515/set-marker-icon-on-google-maps-v2-android-from-url
     *
     */
    public Bitmap getBitMapFromLink(String remotePath) {
        try {
            URL url = new URL(remotePath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.connect();
            } catch (Exception e) {
                Log.v("MAP-FRAGMENT", e.toString());
            }
            InputStream input = connection.getInputStream();
            Bitmap imageBitmap = BitmapFactory.decodeStream(input);
            return imageBitmap;
        } catch (IOException e) {
            Log.v("MAP-FRAGMENT", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //repeated code....will need a way to avoid this
    //currently bad for scaling because it will load all posts in db
    //method should look for posts closets to where map is centered
    private void queryPosts(GoogleMap googleMap) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.USER);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.i("QUERY", "something went wrong querying posts " + e.toString());
                    System.out.println("here bug");
                    e.printStackTrace();
                    return;
                }
                Log.i("QUERY", "success querying posts " + posts.size());
                mPosts.addAll(posts);
                addMarkers(googleMap, mPosts);
                Log.i("QUERY", "success querying posts2 " + mPosts.size());
            }
        });

    }

    /**
     * Adds markers to a given map positioned at the coordinates of the post object. It does this
     * by looping through the posts list, creating a new marker from each post's coordinates, and then using
     * the marker's setTag method to include the post object in the marker.
     * @param googleMap: The map to add markers to
     * @param posts: the list of posts whose latitude and longitude coordinates will be used to position markers
     */
    private void addMarkers(GoogleMap googleMap, List<Post> posts) {
        for(Post post: posts) {
           LatLng position = new LatLng(post.getLatitude(), post.getLongiitude());
           Marker marker = googleMap.addMarker(new MarkerOptions()
                   .title(post.getParseUser().getUsername())
                   .position(position)
                   .snippet(post.getCaption()));

           //include post object in marker
           marker.setTag(post);
        }

        //[post ->marker]
    }

}

