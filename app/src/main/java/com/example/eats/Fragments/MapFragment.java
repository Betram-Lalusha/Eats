package com.example.eats.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.eats.Adapters.PostsAdapter;
import com.example.eats.EndlessRecyclerViewScrollListener;
import com.example.eats.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class MapFragment extends Fragment {

    Double mUserLatitude;
    Double mUserLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUserLatitude = getArguments().getDouble("userLat", 37.4219862);
        mUserLongitude = getArguments().getDouble("userLong" ,-122.0842771);
        // Initialize view
        View view=inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Add a marker at current signed in user location,
                // and move the map's camera to the same location.
                LatLng userLoc = new LatLng(mUserLatitude, mUserLongitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(userLoc)
                        .title("Marker in Sydney"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
                googleMap.setMinZoomPreference(15);
            }
        });
        // Return view
        return view;
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState) {

    }

}

