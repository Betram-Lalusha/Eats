package com.example.eats.Models;

import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.time.LocalTime;

@ParseClassName("RecentSearchedItem")
public class RecentSearchedItem extends ParseObject {
    public static final String POST = "post";
    public static final String POSITION = "position";
    public  RecentSearchedItem() {}

    public void setPost(Post post) {
        put(POST, post);
    }

    public Post getPost(){return (Post) get(POST);}

    public void setPosition(int position) {
        put(POSITION, position);
    }

    public int getPosition() {return getInt(POSITION);}
    @Override
    public String toString() {
        return "{post: " + getPost() + ", timeSearched: " +  getPosition() + "}";
    }
}
