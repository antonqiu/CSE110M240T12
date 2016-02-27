package com.cyruszhang.cluboard.parse;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by AntonioQ on 1/30/16.
 * ParseObject Fields:
 * name String; datetime, Date; coord, GeoPoint; location, String; desc, String
 */
@ParseClassName("Events")
public class Event extends ParseObject {

    // from docs, ParseObject has to have an empty constructor
    public Event() {
    }

    public void setClub(Club club) {
        put("club", club);
    }

    public Club getClub() {
        return (Club) getParseObject("club");
    }

    public void setFollowingRelations(ParseObject relation) {
        put("following", relation);
    }

    // TODO: sometimes it just doesn't have it !!!
    public ParseObject getFollowingRelations() {
        ParseObject result = getParseObject("following");
        try {
            result.fetchIfNeeded();
        } catch (Exception e) {}
        return result;
    }

    public String getEventName() {
        return getString("name");
    }

    public void setEventName(String eventName) {
        put("name", eventName);
    }

    /* include date and time */
    public Date getFromTime() {
        return getDate("fromTime");
    }

    public void setFromTime(Date eventTime) {
        put("fromTime", eventTime);
    }

    public Date getToTime() {
        return getDate("toTime");
    }

    public void setToTime(Date eventTime) {
        put("toTime", eventTime);
    }

    // let's use a GeoPoint type to store a 2nd location
    public ParseGeoPoint getEventCoordinate() {
        return getParseGeoPoint("coord");
    }

    public void setEventCoordinate(ParseGeoPoint coord) {
        put("coord", coord);
    }

    public String getEventLocation() {
        return getString("location");
    }

    public void setEventLocation(String eventLocation) {
        put("location", eventLocation);
    }

    public String getEventDesc() {
        return getString("desc");
    }

    public void setEventDesc(String eventDesc) {
        put("desc", eventDesc);
    }


    public static ParseQuery<Event> getQuery() {
        return ParseQuery.getQuery(Event.class);
    }

    // TODO: no repeat following
    public void addFollowingUser(final ParseUser follower) {
        ParseObject relation = getFollowingRelations();
        ParseRelation<ParseUser> bookmarkRelation = relation.getRelation("followingUsers");
        bookmarkRelation.add(follower);
        relation.increment("count", 1);
        relation.saveEventually();
    }

    public void removeFollowingUser(final ParseUser follower) {
        ParseObject relation = getFollowingRelations();
        ParseRelation<ParseUser> bookmarkRelation = relation.getRelation("followingUsers");
        bookmarkRelation.remove(follower);
        relation.increment("count", -1);
        relation.saveEventually();
    }


}
