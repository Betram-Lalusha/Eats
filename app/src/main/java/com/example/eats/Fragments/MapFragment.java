package com.example.eats.Fragments;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eats.Activities.DetailActivity;
import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
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
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

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
    View mCustomMarkerView;
    IconGenerator mIconGenerator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {
        mPosts = new LinkedList<Post>();
        mIconGenerator = new IconGenerator(getContext());
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

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

                //user's  location.
                LatLng userLoc = new LatLng(mUserLatitude, mUserLongitude);
                //GET POSTS and add markers
                queryPosts(googleMap, userLoc);
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

                //move the map's camera to the user's  location.
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
                googleMap.setMinZoomPreference(5);


            }
        });
    }

    /***
     * Method converts a resource at a given url path into a bitmap
     * @param remotePath: the url path to the remote image
     * @return the bitmap representing the image
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
    private void queryPosts(GoogleMap googleMap, LatLng userLoc) {
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
                addMarkers(googleMap, mPosts, userLoc);
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
    private void addMarkers(GoogleMap googleMap, List<Post> posts, LatLng userLoc) {
        // Add a marker at current signed in user location,
        Bitmap markerIcon = getMarkerBitmapFromView(posts.get(0), true);
        Marker userMarker = googleMap.addMarker(new MarkerOptions()
                .position(userLoc)
                .title("me")
                .zIndex(1.0f)
                .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));

        userMarker.showInfoWindow();

        for(Post post: posts) {
            markerIcon = getMarkerBitmapFromView(post, false);
            LatLng position = new LatLng(post.getLatitude(), post.getLongiitude());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .title("")
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));

            //include post object in marker
            marker.setTag(post);
        }
    }

    /**
     * Method turns a given xml layout file into a bitmap
     * @param post: The post whose image will be loaded into the imageview of the layout_xml file
     * @return: A bit map representation of the given xml file
     *  * Code adopted from: https://stackoverflow.com/questions/14811579/how-to-create-a-custom-shaped-bitmap-marker-with-android-map-api-v2
     */
    private Bitmap getMarkerBitmapFromView(Post post, Boolean isUserMarker) {
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        TextView markerSnippet = (TextView)  mCustomMarkerView.findViewById(R.id.markerSnippet);
        ImageView markerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.markerImage);

        if(isUserMarker) {
            //load user pfp into marker
            Bitmap image = getBitMapFromLink(ParseUser.getCurrentUser().getParseFile("userProfilePic").getUrl());
            markerImageView.setImageBitmap(image);
            //set marker snippet
            markerSnippet.setVisibility(View.GONE);
        } else {
            //load user pfp into marker
            Bitmap image = getBitMapFromLink(post.getMedia().getUrl());
            markerImageView.setImageBitmap(image);
            //set marker snippet
            markerSnippet.setText(post.getCaption());
        }

        mCustomMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mCustomMarkerView.layout(0, 0, mCustomMarkerView.getMeasuredWidth(), mCustomMarkerView.getMeasuredHeight());
        mCustomMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mCustomMarkerView.getMeasuredWidth(), mCustomMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mCustomMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        mCustomMarkerView.draw(canvas);
        return returnedBitmap;
    }

}

