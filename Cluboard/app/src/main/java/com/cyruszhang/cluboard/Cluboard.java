package com.cyruszhang.cluboard;

/**
 * Created by zhangxinyuan on 1/27/16.
 */
import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Application;

public class Cluboard extends Application {
    public static final String YOUR_APPLICATION_ID = "VYpP0R73xQjEWBuozA9M51I9wCkeFOBldy7rdAsX";
    public static final String YOUR_CLIENT_KEY = "BSWKCSr3dq4zuZZCKVrNTruGSOwsO3Phz7Kbqhr7";
    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        ParseObject.registerSubclass(Club.class);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
        ParseFacebookUtils.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }



}
