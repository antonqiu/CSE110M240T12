package com.cyruszhang.cluboard;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cyruszhang.cluboard.parse.Event;
import com.cyruszhang.cluboard.parse.User;
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
                        return relationQuery;
                    }
                };

        eventQueryAdapter = new ParseQueryAdapter<ParseObject>(this, factory) {
            @Override
            public View getItemView(final ParseObject object, View v, ViewGroup parent) {
                if (v == null) {
                    //Log.d(getClass().getSimpleName(), "inflating item view");
                    v = View.inflate(getContext(), R.layout.event_list_item, null);
                    // v = LayoutInflater.from(getContext()).
                    // inflate(R.layout.club_list_item, null, false);
                }
                Log.d(getClass().getSimpleName(), "item retrieved");
                final Event thisEvent = (Event) object.getParseObject("eventObject");
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView eventName = (TextView) v.findViewById(R.id.event_list_item_name);
                TextView eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
                try {
                    thisEvent.fetch();
                } catch (Exception e) {
                    Log.d(getClass().getSimpleName(), "Something is wrong");
                }
                eventName.setText(thisEvent.getEventName());
                eventLocation.setText(thisEvent.getEventLocation());
                final TextView eventCount = (TextView) v.findViewById(R.id.event_list_item_count);
                eventCount.setText(String.format("%d", (int) thisEvent.findFollowingRelation().get("count")));
                // follow button setup
                final ToggleButton followButton = (ToggleButton) v.findViewById(R.id.event_list_item_follow);
                /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) followButton.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                followButton.setLayoutParams(params);*/
                User currentUser = (User) ParseUser.getCurrentUser();
                if (currentUser.checkFollowingEvent(thisEvent)) {
                    followButton.setChecked(false);
                }
                else
                    followButton.setChecked(true);
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (followButton.isChecked()) {
                            thisEvent.removeFollowingUser(ParseUser.getCurrentUser());
                            Snackbar.make(v,
                                    "You unfollowed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                        else {
                            thisEvent.addFollowingUser(ParseUser.getCurrentUser());
                            Snackbar.make(v,
                                    "You followed this event", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();


                        }
                        eventCount.setText(String.format("%d", (int) thisEvent.findFollowingRelation().get("count")));
                    }
                });


                return v;
            }
        };
        Log.d(getClass().getSimpleName(), "setting up adapter");
        eventList.setAdapter(eventQueryAdapter);

        // item click listener

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event event =(Event) eventQueryAdapter.getItem(position).getParseObject("eventObject");
                event.addFollowingUser((User) ParseUser.getCurrentUser());
                Toast.makeText(getApplicationContext(), "You followed this event", Toast.LENGTH_SHORT).show();
               /* Intent intent = new Intent(Welcome.this, ClubDetail.class);
                intent.putExtra("OBJECT_ID", club.getObjectId());
                startActivity(intent); */
            }
        });

    }

}