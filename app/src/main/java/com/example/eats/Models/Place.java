package com.example.eats.Models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelClass;

@Parcel
public class Place {

    public int mWidth;
    public int mHeight;
    public String mName;
    public String mPhotoReference;
    public Place() {}

    public static Place fromJson(JSONObject jsonObject) {
        Place place = new Place();

        try {
            place.mName = jsonObject.getString("name");
            JSONObject photo = jsonObject.getJSONArray("photos").getJSONObject(0);
            place.mWidth = photo.getInt("width");
            place.mHeight = photo.getInt("height");
            place.mPhotoReference = photo.getString("photo_reference");
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
    public String getPhotoUrl() {
        return "";
    }

    @Override
    public String toString() {
        return "[" + " place: " + this.mName + "]";
    }

}

//        : response {
//            I/System.out:    "candidates" : [
//            I/System.out:       {
//                I/System.out:          "photos" : [
//                I/System.out:             {
//                    I/System.out:                "height" : 746,
//                            I/System.out:                "html_attributions" : [
//                    I/System.out:                   "\u003ca href=\"https://maps.google.com/maps/contrib/108862076113400282635\"\u003eA Google User\u003c/a\u003e"
//                    I/System.out:                ],
//                    I/System.out:                "photo_reference" : "Aap_uEBADZ7cBAoEHSnljKUQ2UZow9kq53TbIdb1YoRBHXp6fg92ffiCl-OcI5FO-JQcQ0QFRZXomMy0fRntMHcIBrDa_yaIg_xmsh6J3WoXMV5U-xO95-PiN4SOlsCd70h2VGE9JBO_lZpOga9khRQ0K2rnRSGI5IAT_OzjUDZm507hn1wS",
//                            I/System.out:                "width" : 1079
//                    I/System.out:             }
//                I/System.out:          ]
//                I/System.out:       }
//            I/System.out:    ],
//            I/System.out:    "status" : "OK"
//            I/System.out: }
