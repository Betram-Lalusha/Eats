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
    public  String mImageUrl;
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
            place.getPhotoUrl();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;

    }


        /**
         * Retrives the url of the photo using this object's photo reference.
         * API calls are made using AsyncHTTP class and are made to the google maps api
         * @return: The url of the remote image
         */
    public void getPhotoUrl() {
        System.out.println("ref " + this.mPhotoReference);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("key", MAPS_API_KEY);
        requestParams.put("photo_reference",this.mPhotoReference);
        requestParams.put("maxwidth", 400);
        requestParams.put("maxheight",400);
        asyncHttpClient.get(GOOGLE_PLACES_GET_PHOTO_BASE_URL, requestParams, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, Response response) {
                String url = getUrl(response);
                setUrl(url);
                return;
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
                Log.i("GET-PLACE-URL","error occured no json " + errorResponse);
                return;
            }
        });
    }

    /**
     * returns the url of an image from a given response object
     * @param response: the response object to decode
     * @return: the url contained in the response
     */
    private String getUrl(Response response) {
        String responseStr = response.toString();
        String[] arr = responseStr.split(",");
        String inCorrectUrl =  arr[3];
        String correctUrl = "";
        for(int i = 0; i < inCorrectUrl.length(); i++) {
            if(inCorrectUrl.charAt(i) == 'h') {
                int k = i;
                while(k < inCorrectUrl.length() - 1) {
                    correctUrl += inCorrectUrl.charAt(k);
                    k++;
                }
                i = k;
            }
            if(inCorrectUrl.charAt(i) == '}') break;
        }
        //System.out.println("url " + correctUrl);
        return correctUrl;
    }

    public void setUrl(String url) {
        this.mImageUrl = url;
    }

    @Override
    public String toString() {
        return "[" + " place: " + this.mName + ", imageUrl: " + this.mImageUrl + "]";
    }

}

