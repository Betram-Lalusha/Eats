package com.example.eats.Models;

import static com.example.eats.BuildConfig.MAPS_API_KEY;

import android.util.Log;

import androidx.annotation.Nullable;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelClass;

import okhttp3.Headers;
import okhttp3.Response;

@Parcel
public class Place {

    public int mWidth;
    public int mHeight;
    public String mName;
    public String mPhotoReference;
    public final String  GOOGLE_PLACES_GET_PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";


    public Place() {}

    public static Place fromJson(JSONObject jsonObject, String name) {
        Place place = new Place();

        try {
            place.mName = name;
            JSONObject photo = jsonObject.getJSONArray("photos").getJSONObject(0);
            place.mWidth = photo.getInt("width");
            place.mHeight = photo.getInt("height");
            place.mPhotoReference = photo.getString("photo_reference");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;

    }


    @Override
    public String toString() {
        return "[" + " place: " + this.mName +"]";
    }

}

