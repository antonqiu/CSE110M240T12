package com.cyruszhang.cluboard.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.Event;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by AntonioQ on 3/3/16.
 *
 */
public abstract class EventQueryRecyclerAdapter<T extends ParseObject, U extends RecyclerView.ViewHolder>
        extends ParseRecyclerQueryAdapter<T, U> {

    public Event myEvent;
    public ParseObject myFollowingRelation;
    public Context context;
    private static final int WITHIN_THREE_DAYS = 0;
    private static final int MORE = 1;
    private CoordinatorLayout coordinatorLayout;
    Map<Integer, Event> headerSwitch = new HashMap<>(2);

    public EventQueryRecyclerAdapter(ParseQueryAdapter.QueryFactory factory, boolean hasStableIds) {
        super(factory, hasStableIds);
    }

    public EventQueryRecyclerAdapter(String className, boolean hasStableIds) {
        super(className, hasStableIds);
    }

    public EventQueryRecyclerAdapter(Class clazz, boolean hasStableIds) {
        super(clazz, hasStableIds);
    }

//    @Override
//    public U onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        // Inflate the custom layout
//        View contactView = inflater.inflate(R.layout.event_list_item, parent, false);
//
//        // Return a new holder instance
//        return new ListViewHolder(contactView);
//    }

    public void getParseObject(int position) {
        myEvent = (Event) getItem(position);
        myFollowingRelation = myEvent.getFollowingRelations();
    }

    @Override
    public void onBindViewHolder(U holder, int position) {
        if (holder instanceof ListViewHolder) {
            onBindListViewHolder((ListViewHolder)holder, position);
        } else if (holder instanceof CardViewHolder) {
            onBindCardViewHolder((CardViewHolder)holder, position);
        }
    }

    public void onBindCardViewHolder(CardViewHolder holder, int position) {
        getParseObject(position);
        final Event thisEvent = myEvent;
        final TextView eventName = holder.eventName,
                eventLocation = holder.eventLocation,
                eventDesc = holder.eventDesc,
                fromTime = holder.fromTime,
                toTime = holder.toTime,
                eventDate = holder.eventDate,
                timeHeader = holder.timeHeader,
                eventCount = holder.eventCount;
        final ToggleButton followButton = holder.followButton;

        // set event basic info
        eventName.setText(thisEvent.getEventName());
        eventLocation.setText(thisEvent.getEventLocation());
        eventDesc.setText(thisEvent.getEventDesc());

        // date time setup
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        fromTime.setText(formatter.format(thisEvent.getFromTime()));
        toTime.setText(formatter.format(thisEvent.getToTime()));
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        eventDate.setText(formatter.format(thisEvent.getFromTime()));

        // header
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(thisEvent.getFromTime());
        Calendar threeDaysCal = Calendar.getInstance();
        threeDaysCal.set(Calendar.DAY_OF_MONTH, threeDaysCal.get(Calendar.DAY_OF_MONTH) + 2);
        if (startTime.compareTo(threeDaysCal) < 0 && (!headerSwitch.containsKey(WITHIN_THREE_DAYS)
                || headerSwitch.get(WITHIN_THREE_DAYS) == thisEvent)) {
            timeHeader.setVisibility(View.VISIBLE);
            timeHeader.setText("In 3 Days");
            headerSwitch.put(WITHIN_THREE_DAYS, thisEvent);
        } else if (startTime.compareTo(threeDaysCal) >= 0 && (!headerSwitch.containsKey(MORE)
                || headerSwitch.get(MORE) == thisEvent)) {
            timeHeader.setVisibility(View.VISIBLE);
            timeHeader.setText("More");
            headerSwitch.put(MORE, thisEvent);
        } else timeHeader.setVisibility(View.GONE);

        // following count & following button init
        final ParseObject followRelation = myFollowingRelation;
        eventCount.setText(String.format("%d", followRelation.getInt("count")));
        ParseQuery<ParseObject> userQuery = followRelation.getRelation("followingUsers").getQuery();
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        userQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (count == 1) {
                    followButton.setChecked(true);
                } else {
                    followButton.setChecked(false);
                }
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // super counter-intuitive... It's reversed
                        if (!followButton.isChecked()) {
                            int currentCount = followRelation.getInt("count");
                            thisEvent.removeFollowingUser(ParseUser.getCurrentUser());
                            Log.e("notification", "cancelNotification: call function");
                            cancelNotification(thisEvent.getFromTime(), thisEvent.getCreatedAt());
                            Log.e("notification", "cancelNotification: after call function");
                            eventCount.setText(String.format("%d", currentCount - 1));
                            Snackbar.make(eventName,
                                    "You unfollowed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            int currentCount = followRelation.getInt("count");
                            thisEvent.addFollowingUser(ParseUser.getCurrentUser());
                            //schedule notification if user follow event
                            Log.e("notification", "scheduleNotification: call function");
                            scheduleNotification(thisEvent.getFromTime(), thisEvent.getCreatedAt());
                            Log.e("notification", "scheduleNotification: after call function");
                            eventCount.setText(String.format("%d", currentCount + 1));
                            Snackbar.make(eventName,
                                    "You followed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });
    }

    public void onBindListViewHolder(ListViewHolder holder, int position) {
        getParseObject(position);
        final Event thisEvent = myEvent;
        final TextView eventName = holder.eventName,
                eventLocation = holder.eventLocation,
                fromTime = holder.fromTime,
                toTime = holder.toTime,
                eventDate = holder.eventDate,
                timeHeader = holder.timeHeader,
                eventCount = holder.eventCount;
        final ToggleButton followButton = holder.followButton;

        // set event basic info
        eventName.setText(thisEvent.getEventName());
        eventLocation.setText(thisEvent.getEventLocation());

        // date time setup
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        fromTime.setText(formatter.format(thisEvent.getFromTime()));
        toTime.setText(formatter.format(thisEvent.getToTime()));
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        eventDate.setText(formatter.format(thisEvent.getFromTime()));

        // header
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(thisEvent.getFromTime());
        Calendar threeDaysCal = Calendar.getInstance();
        threeDaysCal.set(Calendar.DAY_OF_MONTH, threeDaysCal.get(Calendar.DAY_OF_MONTH) + 2);
        if (startTime.compareTo(threeDaysCal) < 0 && (!headerSwitch.containsKey(WITHIN_THREE_DAYS)
                || headerSwitch.get(WITHIN_THREE_DAYS) == thisEvent)) {
            timeHeader.setVisibility(View.VISIBLE);
            timeHeader.setText("In 3 Days");
            headerSwitch.put(WITHIN_THREE_DAYS, thisEvent);
        } else if (startTime.compareTo(threeDaysCal) >= 0 && (!headerSwitch.containsKey(MORE)
                || headerSwitch.get(MORE) == thisEvent)) {
            timeHeader.setVisibility(View.VISIBLE);
            timeHeader.setText("More");
            headerSwitch.put(MORE, thisEvent);
        } else timeHeader.setVisibility(View.GONE);

        // following count & following button init
        final ParseObject followRelation = myFollowingRelation;
        eventCount.setText(String.format("%d", followRelation.getInt("count")));
        ParseQuery<ParseObject> userQuery = followRelation.getRelation("followingUsers").getQuery();
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        userQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (count == 1) {
                    followButton.setChecked(true);
                } else {
                    followButton.setChecked(false);
                }
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // super counter-intuitive... It's reversed
                        if (!followButton.isChecked()) {
                            int currentCount = followRelation.getInt("count");
                            thisEvent.removeFollowingUser(ParseUser.getCurrentUser());
                            Log.e("notification", "cancelNotification: call function");
                            cancelNotification(thisEvent.getFromTime(), thisEvent.getCreatedAt());
                            Log.e("notification", "cancelNotification: after call function");
                            eventCount.setText(String.format("%d", currentCount - 1));
                            Snackbar.make(eventName,
                                    "You unfollowed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            int currentCount = followRelation.getInt("count");
                            thisEvent.addFollowingUser(ParseUser.getCurrentUser());
                            //schedule notification if user follow event
                            Log.e("notification", "scheduleNotification: call function");
                            scheduleNotification(thisEvent.getFromTime(), thisEvent.getCreatedAt());
                            Log.e("notification", "scheduleNotification: after call function");
                            eventCount.setText(String.format("%d", currentCount + 1));
                            Snackbar.make(eventName,
                                    "You followed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });
    }
    private void scheduleNotification(Date startTime, Date id) {
        //TODO: notify 30 mins before event start time
        final long TIME_AHEAD = TimeUnit.MINUTES.toMillis(30);

        //if event start time is in the past or less then 30mins in future, do not schedule an alarm
        //if(startTime.getTime()-TIME_AHEAD <= System.currentTimeMillis()) return;
        Log.e("notification", "scheduleNotification: enter function");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        int eventID = (int) (id.getTime() / 1000);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, eventID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.SECOND, 5);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

        long eventTime = startTime.getTime();
        long alarmTime = eventTime-TIME_AHEAD;
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, broadcast);
        Log.e("notification", "scheduleNotification: scheduled a notification");
    }

    private void cancelNotification(Date startTime, Date id) {
        //notify 30 mins before event start time
        final long TIME_AHEAD = TimeUnit.MINUTES.toMillis(30);

        //if event start time is in the past or less then 30mins in future, no need to cancel an alarm
        //if(startTime.getTime()-TIME_AHEAD <= System.currentTimeMillis()) return;
        Log.e("notification", "cancelNotification: enter cancel function");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        int eventID = (int) (id.getTime() / 1000);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, eventID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // If the alarm has been set, cancel it.
        if (alarmManager != null) {
            alarmManager.cancel(broadcast);
            Log.e("notification", "cancelNotification: canceled a notification");
        }
        /*
        long eventTime = date.getTime();
        long alarmTime = eventTime-TIME_AHEAD;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, broadcast);
        */

    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ListViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView eventName, eventLocation, fromTime,
                toTime, eventDate, timeHeader, eventCount;
        public ToggleButton followButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ListViewHolder(View v) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ListViewHolder instance.
            super(v);

            eventName = (TextView) v.findViewById(R.id.event_list_item_name);
            eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
            eventDate = (TextView) v.findViewById(R.id.event_list_item_time_date);
            fromTime = (TextView) v.findViewById(R.id.event_list_item_time_from);
            toTime = (TextView) v.findViewById(R.id.event_list_item_time_to);
            timeHeader = (TextView) v.findViewById(R.id.event_list_item_header);
            followButton = (ToggleButton) v.findViewById(R.id.event_list_item_follow);
            eventCount = (TextView) v.findViewById(R.id.event_list_item_count);
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView eventName, eventLocation, eventDesc, fromTime,
                toTime, eventDate, timeHeader, eventCount;
        public ToggleButton followButton;

        public CardViewHolder(View v) {
            super(v);

            eventName = (TextView) v.findViewById(R.id.event_list_item_name);
            eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
            eventDesc = (TextView) v.findViewById(R.id.event_list_item_desc);
            eventDate = (TextView) v.findViewById(R.id.event_list_item_time_date);
            fromTime = (TextView) v.findViewById(R.id.event_list_item_time_from);
            toTime = (TextView) v.findViewById(R.id.event_list_item_time_to);
            timeHeader = (TextView) v.findViewById(R.id.event_list_item_header);
            followButton = (ToggleButton) v.findViewById(R.id.event_list_item_follow);
            eventCount = (TextView) v.findViewById(R.id.event_list_item_count);
        }
    }
}
