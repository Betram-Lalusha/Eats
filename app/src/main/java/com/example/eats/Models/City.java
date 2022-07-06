package com.example.eats.Models;

import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
@ParseClassName("City")
public class City extends ParseObject {
    public static final String NAME = "name";
    public static final String IMAGE_URL = "imageUrl";

    public City() {}

    public String getName() {
        return getString(NAME);
    }

    public void setName(String cityName) {
        put(NAME, cityName);
    }

    public String getImageUrl() {
        return getString(IMAGE_URL);
    }

    public void setImageUrl(String cityImageUrl) {
        put(IMAGE_URL, cityImageUrl);
    }

    @Override
    public  String toString() {
        return this.getName();
    }

}
