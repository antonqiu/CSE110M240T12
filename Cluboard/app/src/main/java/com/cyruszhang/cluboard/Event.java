package com.cyruszhang.cluboard;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by AntonioQ on 1/30/16.
 */
@ParseClassName("Events")
public class Event extends ParseObject{
    /*protected int eventID;
    private String eventName;
    // TODO: date time type?
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String eventDesc;
*/
   /* public Event(String eventName, String eventDate, String eventTime, String eventLocation, String eventDesc) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventDesc = eventDesc;

        // TODO: event ID and database interaction
    }
*/
    public String getEventName() {
        return getString("name");
    }

    public void setEventName(String eventName) {
        put("name", eventName);
    }

    /* include date and time */
    public Date getEventTime() {
        return getDate("time");
    }

    public void setEventDate(Date eventTime) {
        put("time", eventTime);
    }

  /*  public String getEventTime() {
        return getString("time");
    }

    public void setEventTime(String eventTime) {
        put("time", )
    } */

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
}
