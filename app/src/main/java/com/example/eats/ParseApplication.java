package com.example.eats;

import android.app.Application;

import com.example.eats.Models.City;
import com.example.eats.Models.Post;
import com.example.eats.Models.RecentSearchedItem;
import com.example.eats.Models.User;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(City.class);
        ParseObject.registerSubclass(RecentSearchedItem.class);
        //enable local storage for caching
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .enableLocalDataStore()
                .build());

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "1071974939904");
        installation.saveInBackground();
    }
}
