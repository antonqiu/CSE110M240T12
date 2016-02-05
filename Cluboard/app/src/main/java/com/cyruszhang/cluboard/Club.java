package com.cyruszhang.cluboard;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

/**
 * Created by AntonioQ on 1/30/16.
 */
@ParseClassName("Clubs")
public class Club extends ParseObject{
    //protected String clubID;
    //protected String clubName;
    //protected String clubDesc;
    //protected String clubDetail;
    //protected ParseUser owner;

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
    public void addEvent(Event event) {
        put("events", Arrays.asList(event));
    }

    /* get all event in a JsonArray from the club */
    public JSONArray getEventList() {
        return (JSONArray)get("events");
    }

    public void setOwner(ParseUser owner) {
        //this.owner = owner;
        put("owner", owner);
    }

    public static ParseQuery<Club> getQuery() {
        return ParseQuery.getQuery(Club.class);
    }
}
