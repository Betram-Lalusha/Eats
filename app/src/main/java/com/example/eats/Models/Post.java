package com.example.eats.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String USER = "user";
    public static final String IMAGE = "image";
    public static final String PRICE = "price";
    public static final String DETAILS = "details";
    public static final String CAPTION = "caption";
    public static final String LOCATION = "location";
    public static final String LIKES_COUNT = "likesCount";
    public static final String COMMENTS_COUNT = "commentsCount";

    public Post() {}

    public String getCaption() {
        return getString(CAPTION);
    }
    public void setCaption(String caption) {
        put(CAPTION, caption);
    }
    public ParseFile getMedia() {
        return getParseFile(IMAGE);
    }

    public void setMedia(ParseFile media) {
        put(IMAGE, media);
    }
    public ParseUser getParseUser() {
        return getParseUser(USER);
    }

    public void setUser(ParseUser user) {
        put(USER, user);
    }

    public int getPrice() {
        return (int) getNumber(PRICE);
    }


    public void setPrice(Number price) {
        put(PRICE, price);
    }

    public Object getLocation() {
        return getParseObject(LOCATION);
    }


    public void setLocation(ParseObject location) {
        put(LOCATION, location);
    }

    public String getDetails() {
        return getString(DETAILS);
    }


    public void setDetails(String details) {
        put(DETAILS, details);
    }

    public int getLikesCount() {
        return (int) getNumber(LIKES_COUNT);
    }


    public void setLikesCount(Number newCount) {
        put(LIKES_COUNT, newCount);
    }

    public int getCommentsCount() {
        return (int) getNumber(COMMENTS_COUNT);
    }


    public void setCommentsCount(Number newCount) {
        put(COMMENTS_COUNT, newCount);
    }

    public Date getDate() {return  getCreatedAt();}
}
