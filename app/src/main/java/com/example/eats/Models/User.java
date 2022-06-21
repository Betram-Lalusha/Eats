package com.example.eats.Models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_PROFILE_PIC = "userProfilePic";
    public static final String EMAIL = "email";

    public User() {}

    public String getUserName() {
        return getString(USER_NAME);
    }
    public void setUserName(String userName) {
        put(USER_NAME, userName);
    }

    public String getUserEmail() {
        return getString(EMAIL);
    }
    public void setUserEmail(String userEmail) {
        put(EMAIL, userEmail);
    }

    public String getUserPassword() {
        return getString(PASSWORD);
    }
    public void setUserPassword(String userPassword) {
        put(PASSWORD, userPassword);
    }

    public ParseFile getUserPfp() {
        return getParseFile(USER_PROFILE_PIC);
    }

    public void setUserProfilePic(ParseFile media) {
        put(USER_PROFILE_PIC, media);
    }
    public Date getJoinedDate() {return  getCreatedAt();}

    public void signUp(String userName, String password) {
        signUp(userName, password);
    }
}
