package com.cyruszhang.cluboard;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by AntonioQ on 1/30/16.
 */
@ParseClassName("Clubs")
public class Club extends ParseObject{
    protected String clubID;
    protected String clubName;
    protected String clubDesc;
    protected ParseUser owner;

    public String getClubName() {
        return getString("name");
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
        put("name", clubName);
    }

    public String getClubDesc() {
        return getString("desc");
    }

    public void setClubDesc(String clubDesc) {
        this.clubDesc = clubDesc;
        put("desc", clubDesc);
    }

    public String getClubID() {
        return getString("clubID");
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser owner) {
        this.owner = owner;
        put("owner", owner);
    }

    public static ParseQuery<Club> getQuery() {
        return ParseQuery.getQuery(Club.class);
    }
}
