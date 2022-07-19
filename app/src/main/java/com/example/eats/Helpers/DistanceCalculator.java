package com.example.eats.Helpers;

import android.util.Log;

public class DistanceCalculator {
    String mUnits;
    Double mUserLatitude;
    Double mUserLongitude;
    private final String TAG = "DistanceCalculator";
    public DistanceCalculator(String units, Double userLatitude, Double userLongitude) {
        this.mUnits = units;
        this.mUserLatitude = userLatitude;
        this.mUserLongitude = userLongitude;

    }

    /**
     * Calculates distance between two coordinates using the Haversine formula
     * @param pointLat: the latitude coordinate of the post
     * @param pointLon: the longitude coordinate of the post
     * @return: the distance of the given post from the current user's location in either kilometers or Newtons
     */
    public  double distance(double pointLat, double pointLon) {
        if ((pointLat == this.mUserLatitude) && (pointLon == this.mUserLongitude)) {
            //Log.d(TAG, "Returning 0");
            return 0;
        }
        else {
            double theta = pointLon - this.mUserLongitude;
            double dist = Math.sin(Math.toRadians(pointLat)) * Math.sin(Math.toRadians(this.mUserLatitude)) + Math.cos(Math.toRadians(pointLat)) * Math.cos(Math.toRadians(this.mUserLatitude)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if ( this.mUnits.equals("K")) {
                dist = dist * 1.609344;
            } else if ( this.mUnits.equals("N")) {
                dist = dist * 0.8684;
            }

            //Log.d(TAG, "Returning " + dist);
            return (dist);
        }
    }
}
