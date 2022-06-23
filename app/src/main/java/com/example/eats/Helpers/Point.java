package com.example.eats.Helpers;

import com.example.eats.Models.Post;

import java.util.Date;

//class represents a single coordinate point based on latitude and longitude
public class Point implements Comparable<Point> {

    public Post mPost;
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

        this.mDistance = distance(this.mLatitude, this.mLongitude, this.mUserLatitude, this.mUserLongitude, "K");
    }
    

    /*
       Code Adopted from geeksforgeeks: https://www.geeksforgeeks.org/program-distance-two-points-earth/#:~:text=For%20this%20divide%20the%20values,is%20the%20radius%20of%20Earth
       Method calculates the distance between two given coordinates
       //bugged!!!
     */
    public  double distance(double pointLat, double pointLon, double userLat, double userLon,String unit) {
        if ((pointLat == userLat) && (pointLon == userLon)) {
            return 0;
        }
        else {
            double theta = pointLon - userLon;
            double dist = Math.sin(Math.toRadians(pointLat)) * Math.sin(Math.toRadians(userLat)) + Math.cos(Math.toRadians(pointLat)) * Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    /*
      Used by priority queue to order points
      Points with with distance closest to user have higher priority. If Points have the same distance from
      User, Post with earlier creation date have higher priority
     */
    public int compareTo(Point other) {
        int diff = (int)(this.mDistance - other.mDistance);
        return diff;
    }

    @Override
    public String toString() {
        return "[ " + this.mLatitude + ", " + this.mLongitude + " ]";
    }

}
