package com.cyruszhang.cluboard;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
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

    public String getEventName() {
        return getString("name");
    }

    public void setEventName(String eventName) {
        put("name", eventName);
    }

    /* include date and time */
    public Date getEventTime() {
        return getDate("datetime");
    }

    public void setEventTime(Date eventTime) {
        put("datetime", eventTime);
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

    public ParseObject findFollowingRelation(final ParseUser follower){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BookmarkRelations");
        query.whereEqualTo("clubObjectId", this.get("objectId"));
        try {
            return query.getFirst();
        }
        catch(ParseException exception) {
            return null;
        }
    }

    public void addFollowingUser(final ParseUser follower) {
        ParseObject relation = findFollowingRelation(follower);

        if (relation == null) {
            ParseObject newRelation = new ParseObject("BookmarkRelations");
            newRelation.put("clubObjectId", getObjectId());
            ParseRelation<ParseUser> bookmarkRelation = newRelation.getRelation("bookmarkUsers");
            bookmarkRelation.add(follower);
            newRelation.saveInBackground();
        } else {
            ParseRelation<ParseUser> bookmarkRelation = relation.getRelation("bookmarkUsers");
            bookmarkRelation.add(follower);
            relation.saveInBackground();
        }
    }

    public void removeFollowingUser(final ParseUser follower) {
        ParseObject relation = findFollowingRelation(follower);

        if (relation != null) {
            ParseRelation<ParseUser> bookmarkRelation = relation.getRelation("bookmarkUsers");
            bookmarkRelation.remove(follower);
            relation.saveInBackground();
        }
    }
}
