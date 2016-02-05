package com.cyruszhang.cluboard;

import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhangxinyuan on 2/4/16.
 */
public class User extends ParseUser {
    /* store user created club */
    public void setMyclub(Club myClub) {
        this.addAllUnique("myClub", Arrays.asList(myClub));
    }
    /* store bookmarked club */
    public void setBookmarkedClub(Club bookmarkedClub) {
        this.addAllUnique("bookmarkedClub", Arrays.asList(bookmarkedClub));
    }
    /* store followed event */
    public void setFollowedEvent(Event followedEvent) {
        this.addAllUnique("followedEvent", Arrays.asList(followedEvent));
    }


}
