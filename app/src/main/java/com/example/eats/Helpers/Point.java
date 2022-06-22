package com.example.eats.Helpers;

import com.example.eats.Models.Post;

import java.util.Date;

//class represents a single coordinate point based on latitude and longitude
public class Point implements Comparable<Point> {

    Post mPost;
    public double mLatitude;
    public double mLongitude;
    public double mDistance;
    public double mUserLatitude;
    public double mUserLongitude;

    public Point(Post post, double userLatitude, double userLongitude) {
        this.mPost = post;
        this.mUserLatitude = userLatitude;
        this.mUserLongitude = userLongitude;
        this.mLatitude = post.getLatitude();
        this.mLongitude = post.getLongiitude();

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
      Points with with distance closest to user have higher priority. If Points have the same distance from
      User, Post with earlier creation date have higher priority
     */
    public int compareTo(Point other) {
        int diff = (int)(this.mDistance - other.mDistance);
        int thisTimeDiff  = (int)(System.currentTimeMillis() - this.mPost.getDate().getTime());
        int otherTimeDiff = (int)(System.currentTimeMillis() - other.mPost.getDate().getTime());
        int timeDiff = thisTimeDiff - otherTimeDiff;
        return diff == 0 ? timeDiff : diff;
    }

}
