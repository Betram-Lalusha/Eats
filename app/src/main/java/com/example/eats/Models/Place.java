package com.example.eats.Models;

import org.parceler.Parcel;
import org.parceler.ParcelClass;

@Parcel
public class Place {

    public int mWidth;
    public int mHeight;
    public String mName;
    public String mPhotoReference;
    public Place() {}

    public Place(int width, int height, String name, String photoReference) {
        this.mName = name;
        this.mWidth = width;
        this.mHeight = height;
        this.mPhotoReference = photoReference;
    }

    /**
     * Retrives the url of the photo using this object's photo reference.
     * API calls are made using AsyncHTTP class and are made to the google maps api
     * @return: The url of the remote image
     */
    public String getPhotoUrl() {

    }

}
