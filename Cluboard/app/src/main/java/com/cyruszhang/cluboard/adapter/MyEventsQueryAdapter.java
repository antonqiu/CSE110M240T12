package com.cyruszhang.cluboard.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xinyuan Zhang on 2/27/16.
 */
public class MyEventsQueryAdapter<T extends ParseObject> extends ParseQueryAdapter<T>{

    private static final int WITHIN_THREE_DAYS = 0;
    private static final int MORE = 1;
    private CoordinatorLayout coordinatorLayout;
    Map<Integer, Event> headerSwitch = new HashMap<>(2);

    public MyEventsQueryAdapter(Context context, QueryFactory<T> queryFactory, CoordinatorLayout c) {
        super(context, queryFactory);
        this.coordinatorLayout = c;
//        headerSwitch.put(WITHIN_THREE_DAYS, null);
//        headerSwitch.put(MORE, null);
    }

    public MyEventsQueryAdapter(Context context, QueryFactory<T> queryFactory, int itemViewResource) {
        super(context, queryFactory, itemViewResource);
    }

    public MyEventsQueryAdapter(Context context, Class<? extends ParseObject> clazz) {
        super(context, clazz);
    }

    public MyEventsQueryAdapter(Context context, String className) {
        super(context, className);
    }

    public MyEventsQueryAdapter(Context context, Class<? extends ParseObject> clazz, int itemViewResource) {
        super(context, clazz, itemViewResource);
    }

    public MyEventsQueryAdapter(Context context, String className, int itemViewResource) {
        super(context, className, itemViewResource);
    }

    public MyEventsQueryAdapter(Context context, QueryFactory<T> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(T object, View v, ViewGroup parent) {
        TextView eventName; TextView eventLocation;
        TextView fromTime; TextView toTime; TextView eventDate;
        TextView timeHeader;
        final ToggleButton followButton; final TextView eventCount;
        final ParseObject followRelation = object;
        final Event thisEvent = (Event) followRelation.getParseObject("eventObject");

        if (v == null) {
            Log.d(getClass().getSimpleName(), "inflating item view");
            v = View.inflate(getContext(), R.layout.event_list_item, null);
        }
        Log.d(getClass().getSimpleName(), "setting up item view");
        eventName = (TextView) v.findViewById(R.id.event_list_item_name);
        eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
        eventName.setText(thisEvent.getEventName());
        eventLocation.setText(thisEvent.getEventLocation());

        // date time setup
        eventDate = (TextView) v.findViewById(R.id.event_list_item_time_date);
        fromTime = (TextView) v.findViewById(R.id.event_list_item_time_from);
        toTime = (TextView) v.findViewById(R.id.event_list_item_time_to);

        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        fromTime.setText(formatter.format(thisEvent.getFromTime()));
        toTime.setText(formatter.format(thisEvent.getToTime()));
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        eventDate.setText(formatter.format(thisEvent.getFromTime()));

        // header
        timeHeader = (TextView) v.findViewById(R.id.event_list_item_header);
        Calendar startTime = Calendar.getInstance(); startTime.setTime(thisEvent.getFromTime());
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

        // date


        // following count & following button init
        followButton = (ToggleButton) v.findViewById(R.id.event_list_item_follow);
        eventCount = (TextView) v.findViewById(R.id.event_list_item_count);
        //followRelation = thisEvent.getFollowingRelations();
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
                            Snackbar.make(coordinatorLayout,
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
                            Snackbar.make(coordinatorLayout,
                                    "You followed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        // reset header
        headerSwitch.clear();;
        super.notifyDataSetChanged();
    }

    private void scheduleNotification(Date startTime, Date id) {
        //notify 30 mins before event start time
        final long TIME_AHEAD = TimeUnit.MINUTES.toMillis(30);

        //if event start time is in the past or less then 30mins in future, do not schedule an alarm
        //if(startTime.getTime()-TIME_AHEAD <= System.currentTimeMillis()) return;
        Log.e("notification", "scheduleNotification: enter function");
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        int eventID = (int)(id.getTime()/1000);
        PendingIntent broadcast = PendingIntent.getBroadcast(getContext(), eventID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        /*
        long eventTime = date.getTime();
        long alarmTime = eventTime-TIME_AHEAD;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, broadcast);
        */
        Log.e("notification", "scheduleNotification: scheduled a notification");
    }

    private void cancelNotification(Date startTime, Date id) {
        //notify 30 mins before event start time
        final long TIME_AHEAD = TimeUnit.MINUTES.toMillis(30);

        //if event start time is in the past or less then 30mins in future, no need to cancel an alarm
        //if(startTime.getTime()-TIME_AHEAD <= System.currentTimeMillis()) return;
        Log.e("notification", "cancelNotification: enter cancel function");
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        int eventID = (int)(id.getTime()/1000);
        PendingIntent broadcast = PendingIntent.getBroadcast(getContext(), eventID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // If the alarm has been set, cancel it.
        if(alarmManager != null) {
            alarmManager.cancel(broadcast);
            Log.e("notification", "cancelNotification: canceled a notification");
        }
        /*
        long eventTime = date.getTime();
        long alarmTime = eventTime-TIME_AHEAD;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, broadcast);
        */

    }

}
