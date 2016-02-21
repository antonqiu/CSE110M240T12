package com.cyruszhang.cluboard.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.Event;
import com.cyruszhang.cluboard.parse.User;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MyEvents extends AppCompatActivity {
    ParseQueryAdapter<ParseObject> eventQueryAdapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinator);
        setupMyEvents();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupMyEvents() {

        // Get List View
        ListView eventList = (ListView) this.findViewById(R.id.event_list_view);

        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery<ParseObject> create() {
                        ParseQuery<ParseObject> relationQuery = ParseQuery.getQuery("FollowingRelations");
                        Log.d(getClass().getSimpleName(),"getting query");
                        User currUser = (User) ParseUser.getCurrentUser();
                        relationQuery.whereEqualTo("followingUsers", currUser);
                        relationQuery.orderByDescending("createdAt");
                        relationQuery.include("eventObject");
                        return relationQuery;
                    }
                };

        eventQueryAdapter = new ParseQueryAdapter<ParseObject>(this, factory) {
            @Override
            public View getItemView(final ParseObject object, View v, ViewGroup parent) {
                final ParseObject followRelation = object;
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.event_list_item, null);
                }
                Log.d(getClass().getSimpleName(), "item retrieved");
                final Event thisEvent = (Event) followRelation.getParseObject("eventObject");
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView eventName = (TextView) v.findViewById(R.id.event_list_item_name);
                TextView eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
                eventName.setText(thisEvent.getEventName());
                eventLocation.setText(thisEvent.getEventLocation());

                // event count
                final TextView eventCount = (TextView) v.findViewById(R.id.event_list_item_count);
                eventCount.setText(String.format("%d", followRelation.getInt("count")));

                // follow button setup
                final ToggleButton followButton = (ToggleButton) v.findViewById(R.id.event_list_item_follow);
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
                        followButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // super counter-intuitive... It's reversed
                                if (!isChecked) {
                                    thisEvent.removeFollowingUser(ParseUser.getCurrentUser());
                                    eventCount.setText(String.format("%d", followRelation.getInt("count") - 1));
                                    Snackbar.make(coordinatorLayout,
                                            "You unfollowed this event", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    thisEvent.addFollowingUser(ParseUser.getCurrentUser());
                                    eventCount.setText(String.format("%d", followRelation.getInt("count") + 1));
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
        };
        Log.d(getClass().getSimpleName(), "setting up adapter");
        eventList.setAdapter(eventQueryAdapter);
    }

}