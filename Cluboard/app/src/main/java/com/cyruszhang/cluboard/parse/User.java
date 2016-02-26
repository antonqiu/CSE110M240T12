package com.cyruszhang.cluboard.parse;

import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by zhangxinyuan on 2/4/16.
 */
@ParseClassName("_User")
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

    // use it only when you have to
    public boolean checkBookmarkClub(Club club) {
        // TODO: do the same thing, as to the following
        ParseRelation<ParseUser> bookmarkRelation = club.findBookmarkRelation().getRelation("bookmarkUsers");
        ParseQuery<ParseUser> userQuery = bookmarkRelation.getQuery();
        userQuery.whereEqualTo("objectId", this.getObjectId());
        try {
            // may slow things down!!!
            return userQuery.count() > 0;
        }
        catch (com.parse.ParseException e) {
            return false;
        }
    }

    // use it only when you have to
    public boolean checkFollowingEvent(Event event) {
        try {
            ParseObject followingRelation = event.getFollowingRelations();
            // may slow things down
            followingRelation.fetchIfNeeded();
            ParseRelation<ParseUser> following = followingRelation.getRelation("followingUsers");
            ParseQuery<ParseUser> userQuery = following.getQuery();
            userQuery.whereEqualTo("objectId", getObjectId());
            // may slow things down
            return userQuery.count() > 0;

        } catch (Exception e) {
            return false;
        }
    }
}
