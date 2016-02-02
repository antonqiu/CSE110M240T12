package com.cyruszhang.cluboard;

import com.parse.ParseObject;

/**
 * Created by AntonioQ on 1/30/16.
 */
public class Event {
    protected int eventID;
    private String eventName;
    // TODO: date time type?
    private String eventDate;
    private String eventTime;
    private String eventLocation;
    private String eventDesc;

    public Event(String eventName, String eventDate, String eventTime, String eventLocation, String eventDesc) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventDesc = eventDesc;

        // TODO: event ID and database interaction
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
}
