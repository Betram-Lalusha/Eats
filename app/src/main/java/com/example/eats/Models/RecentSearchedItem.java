package com.example.eats.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("RecentSearchedItem")
public class RecentSearchedItem extends ParseObject {
    public final String POST = "post";
    public  RecentSearchedItem() {}

    public void setPost(Post post) {
        put(POST, post);
    }

    public Post getPost(){return (Post) get(POST);}
}
