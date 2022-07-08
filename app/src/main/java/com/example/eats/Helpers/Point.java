package com.example.eats.Helpers;

import android.content.Intent;

import com.example.eats.Models.Post;

import java.util.Date;
import java.util.Objects;

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
        this.mPost.distanceFromUser= this.mDistance;
    }
    

    /*
       Method calculates the distance between two given coordinates
       //more details
       //link
       //failing cases
       //scaling....will have to repeat
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.mLatitude, mLatitude) == 0 && Double.compare(point.mLongitude, mLongitude) == 0 && Double.compare(point.mDistance, mDistance) == 0 && Double.compare(point.mUserLatitude, mUserLatitude) == 0 && Double.compare(point.mUserLongitude, mUserLongitude) == 0 && mPost.equals(point.mPost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPost, mLatitude, mLongitude, mDistance, mUserLatitude, mUserLongitude);
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
