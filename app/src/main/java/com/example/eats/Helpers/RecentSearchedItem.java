package com.example.eats.Helpers;

import com.example.eats.Models.Post;
import com.parse.ParseClassName;

import java.time.LocalTime;

@ParseClassName("Post")
public class RecentSearchedItem implements Comparable<RecentSearchedItem>{
    public Post mPost;
    public Long mTimeSearched;

    public RecentSearchedItem() {}
    
    public RecentSearchedItem(Post post, Long timeSearched) {
        this.mPost = post;
        this.mTimeSearched = timeSearched;
    }

    public int compareTo(RecentSearchedItem other) {
        return (int) (this.mTimeSearched - other.mTimeSearched);
    }

    @Override
    public String toString() {
        return "[ post: " + this.mPost + ", time searched: " + this.mTimeSearched + "]";
    }

}
