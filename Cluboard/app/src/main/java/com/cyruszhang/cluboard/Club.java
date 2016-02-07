package com.cyruszhang.cluboard;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

/**
 * Created by AntonioQ on 1/30/16.
 * Fields:
 * name: String
 * desc: String
 * clubID: String // TODO: Do we need it & How to implement
 * detail: String
 * email: String
 * web: String
 * owner: Relation of ParseUser
 * events: Relation of Events
 */
@ParseClassName("Clubs")
public class Club extends ParseObject{

    public Club() {}

    public String getClubName() {
        return getString("name");
    }

    public void setClubName(String clubName) {
        //this.clubName = clubName;
        put("name", clubName);
    }

    public String getClubDesc() {
        return getString("desc");
    }

    public void setClubDesc(String clubDesc) {
        //this.clubDesc = clubDesc;
        put("desc", clubDesc);
    }

    public String getClubID() {
        return getString("clubID");
    }

    public void setClubDetail(String clubDetail) {
        put("detail", clubDetail);
    }

    public String getClubDetail() {
        return getString("detail");
    }

    public String getClubEmail() {return getString("email"); }

    public void setClubEmail(String clubEmail) {
        put("email", clubEmail);
    }

    public String getClubWeb() {return getString("web"); }

    public void setClubWeb(String clubWeb) {
        put("web", clubWeb);
    }
    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    /* add event to the events col in club */
    // TODO: add vs. put?
    public void addEvent(Event event) {
        add("events", event);
    }

    /* get all event in a JsonArray from the club */
    // TODO: we should change it to Relation, which is more standardized
    public JSONArray getEventList() {
        return (JSONArray)get("events");
    }

    public void setOwner(ParseUser owner) {
        //this.owner = owner;
        put("owner", owner);
    }

    public void addBookmarkUser(final ParseUser bookmarker) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BookmarkRelations");
        query.whereEqualTo("clubObjectId", this.get("objectId"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null) {
                    ParseObject newRelation = new ParseObject("BookmarkRelations");
                    newRelation.put("clubObjectId",this.get("objectId"));
                    ParseRelation<ParseObject> bookmarkRelation = newRelation.getRelation("bookmarkUsers");
                    bookmarkRelation.add(bookmarker);
                    newRelation.saveInBackground();
                }
                else {
                    ParseObject relation = objects.get(0);
                    ParseRelation<ParseObject> bookmarkRelation = relation.getRelation("bookmarkUsers");
                    bookmarkRelation.add(bookmarker);
                    relation.saveInBackground();
                }
            }
        });
    }

    public void removeBookmarkUser(ParseUser bookmarker) {
        ParseRelation<ParseUser> bookmarkRelation = getRelation("bookmarkUsers");
        bookmarkRelation.remove(bookmarker);
        saveInBackground();
    }

    public static ParseQuery<Club> getQuery() {
        return ParseQuery.getQuery(Club.class);
    }
}
