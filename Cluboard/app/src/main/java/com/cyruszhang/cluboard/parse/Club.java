package com.cyruszhang.cluboard.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;

/**
 * Created by AntonioQ on 1/30/16.
 * Fields:
 * name: String
 * desc: String
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

    public void deleteClub() {
        ParseObject.createWithoutData("Clubs", getObjectId()).deleteEventually();
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

    public String getClubPhone() {return getString("phone"); }

    public void setClubPhone(String clubPhone) {
        put("phone", clubPhone);
    }

    public String getClubWeb() {return getString("web"); }

    public void setClubWeb(String clubWeb) {
        put("web", clubWeb);
    }
    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser owner) {
        //this.owner = owner;
        put("owner", owner);
    }

    public void setBookmarkRelations(ParseObject relations) {
        put("bookmark", relations);
    }
    public ParseObject getBookmarkRelations() {
        return getParseObject("bookmark");
    }

    public ParseObject findBookmarkRelation(){
        try {
            ParseObject relation = getBookmarkRelations();
            // A safety feature that prevents unfilled relation; may slow things down
            relation.fetchIfNeeded();
            return relation;
        } catch (Exception e) {
            return null;
        }
    }

    public void addBookmarkUser(final ParseUser bookmarker) {
        ParseObject relation = findBookmarkRelation();

        if (relation == null) {
            ParseObject newRelation = new ParseObject("BookmarkRelations");
            newRelation.put("clubObject", this);
            ParseRelation<ParseUser> bookmarkRelation = newRelation.getRelation("bookmarkUsers");
            bookmarkRelation.add(bookmarker);
            newRelation.saveInBackground();
        } else {
            ParseRelation<ParseUser> bookmarkRelation = relation.getRelation("bookmarkUsers");
            bookmarkRelation.add(bookmarker);
            relation.saveEventually();
        }
    }

    public void removeBookmarkUser(final ParseUser bookmarker) {
        ParseObject relation = findBookmarkRelation();

        if (relation != null) {
            ParseRelation<ParseUser> bookmarkRelation = relation.getRelation("bookmarkUsers");
            bookmarkRelation.remove(bookmarker);
            relation.saveEventually();
        }
    }

    public static ParseQuery<Club> getQuery() {
        return ParseQuery.getQuery(Club.class);
    }
}
