package com.cyruszhang.cluboard;

import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhangxinyuan on 2/4/16.
 */
@ParseClassName("User")
public class User extends ParseUser {
    public User() {}
    
    /* store user created club */
    public void setMyclubs(Club myClub) {
        this.addUnique("myClubs", myClub);
    }
    /* store bookmarked club */
    public void setBookmarkedClubs(Club bookmarkedClub) {
        this.addUnique("bookmarkedClubs", bookmarkedClub);
    }
    /* store followed event */
    public void setFollowedEvents(Event followedEvent) {
        this.addUnique("followedEvents", followedEvent);
    }
    public void checkPermission(Club club) {
        // TODO: check permissions
    }

    public boolean checkBookmarkClub(Club club) {
        ParseRelation<ParseUser> bookmarkRelation = club.findBookmarkRelation().getRelation("bookmarkUsers");
        ParseQuery<ParseUser> userQuery = bookmarkRelation.getQuery();
        userQuery.whereEqualTo("objectId", this.getObjectId());
        try {
            userQuery.getFirst();
            return true;
        }
        catch (com.parse.ParseException e) {
            return false;
        }
    }

    public boolean checkFollowingClub(Event event) {
        ParseRelation<ParseUser> followingRelation = event.findFollowingRelation().getRelation("followingUsers");
        ParseQuery<ParseUser> userQuery = followingRelation.getQuery();
        userQuery.whereEqualTo("objectId", this.getObjectId());
        try {
            userQuery.getFirst();
            return true;
        }
        catch (com.parse.ParseException e) {
            return false;
        }
    }
}
