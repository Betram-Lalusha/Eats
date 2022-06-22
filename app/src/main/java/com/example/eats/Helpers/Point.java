package com.example.eats.Helpers;

import java.util.Date;

//class represents a single coordinate point based on latitude and longitude
public class Point implements Comparable<Point> {

    public double mLatitude;
    public double mLongitude;
    public double mDistance;
    public Date mCreatedAt;
    public double mUserLatitude;
    public double mUserLongitude;

    public Point(double latitude, double longitude, Date createdAt, double userLatitude, double userLongitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mCreatedAt = createdAt;
        this.mUserLatitude = userLatitude;
        this.mUserLongitude = userLongitude;

        this.mDistance = distance(this.mLatitude, this.mLongitude, this.mUserLatitude, this.mUserLongitude);
    }
    

    /*
       Code Adopted from geeksforgeeks: https://www.geeksforgeeks.org/program-distance-two-points-earth/#:~:text=For%20this%20divide%20the%20values,is%20the%20radius%20of%20Earth
       Method calculates the distance between two given coordinates
     */
    public static double distance(double pointLat, double pointLon, double userLat, double userLon) {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        pointLon = Math.toRadians(pointLon);
        userLon = Math.toRadians(userLon);
        pointLat = Math.toRadians(pointLon);
        userLat = Math.toRadians(userLat);

        // Haversine formula
        double dlon = userLon - pointLon;
        double dlat = userLat - pointLon;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(pointLat) * Math.cos(userLat)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    /*
      Used by priority queue to order points
      Points with creation date closest to current date have highest priority. This is to ensure user feed always has newest posts at shown first.
      In the event that two Points have equal creation date, those closest to user are shown first
     */
    public int compareTo(Point other) {
        return 0; //hard coded
    }

}
