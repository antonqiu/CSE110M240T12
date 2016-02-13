package com.cyruszhang.cluboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.Arrays;

public class ClubDetail extends AppCompatActivity {
    private static final int MENU_ITEM_LOGOUT = 1001;
    private static final int MENU_ITEM_BOOKMARK = 1002;
    private static final int MENU_ITEM_REFRESH = 1003;
    private static final int IMAGE_VIEW_ID = View.generateViewId();
    private CoordinatorLayout coordinatorLayout;
    private Club thisClub;
    private ParseQueryAdapter<Event> eventQueryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        final TextView clubName = (TextView) this.findViewById(R.id.club_detail_name);
        final TextView clubDetail = (TextView) this.findViewById(R.id.club_detail_detail);
        final Button createEventBtn = (Button) this.findViewById(R.id.new_event_btn);

        thisClub = (Club) ParseObject.createWithoutData("Clubs", getIntent().getStringExtra("OBJECT_ID"));
        if (thisClub == null) {
            Toast.makeText(getApplicationContext(), "thisClub is null", Toast.LENGTH_SHORT).show();
        } else {
            try {
                thisClub.fetch();
            } catch (Exception e) {
                Log.d(getClass().getSimpleName(), "fetch failed" + thisClub.getClubName());
            }
            Log.d(getClass().getSimpleName(), "got club object" + thisClub.getClubName());
            clubName.setText(thisClub.getClubName());
            clubDetail.setText(thisClub.getClubDetail());

            // listview setup
            setupEventList();

            User currentUser = (User) ParseUser.getCurrentUser();
            ParseACL clubAcl = thisClub.getACL();
            //if user is owner, show create event button
            if (clubAcl.getWriteAccess(currentUser)) {
                createEventBtn.setVisibility(View.VISIBLE);
            }

        }

//        query.getInBackground(getIntent().getStringExtra("OBJECT_ID"), new GetCallback<Club>() {
//            @Override
//            public void done(Club object, ParseException e) {
//                if (e == null) {
//                    thisClub = (Club) object;
//                    Log.d(getClass().getSimpleName(), "got club object" + thisClub.getClubName());
//                    clubName.setText(thisClub.getClubName());
//                    clubDetail.setText(thisClub.getClubDetail());
//
//                    setupEventList();
//                    //User currentUser = (User)ParseUser.getCurrentUser();
//
//                 /*   String roleName = thisClub.getClubName()+" "+"Moderator";
//                    ParseQuery<ParseRole> roleQuery = ParseRole.getQuery();
//                    roleQuery.whereEqualTo("name", roleName);
//                    roleQuery.whereEqualTo("users", currentUser);
//                    roleQuery.findInBackground(new FindCallback<ParseRole>() {
//                        @Override
//                        public void done(List<ParseRole> objects, ParseException e) {
//                            if(e==null) {
//                                Log.d("permission", "verified");
//                                createEventBtn.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                Log.d("permission", "denied");
//                            }
//                        }
//                    }); */
//                    User currentUser = (User) ParseUser.getCurrentUser();
//                    ParseACL clubAcl = thisClub.getACL();
//                    //if user is owner, show create event button
//                    if (clubAcl.getWriteAccess(currentUser)) {
//                        createEventBtn.setVisibility(View.VISIBLE);
//                    }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        });

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            //click and go to create club view
            public void onClick(View arg0) {
                Intent intent = new Intent(ClubDetail.this, NewEvent.class);
                intent.putExtra("OBJECT_ID", getIntent().getStringExtra("OBJECT_ID"));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        eventQueryAdapter.loadObjects();
        eventQueryAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Logout
        menu.add(0, MENU_ITEM_LOGOUT, 102, "Logout");

        // refresh
        MenuItem refresh = menu.add(0, MENU_ITEM_REFRESH, 103, "Refresh");
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refresh.setIcon(R.drawable.ic_action_refresh);

        // add bookmark or remove bookmark, + actionbar button
        MenuItem bookmark = menu.add(0, MENU_ITEM_BOOKMARK, 104, "Add Bookmark");
        bookmark.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        bookmark.setCheckable(true);

        User thisUser = (User) ParseUser.getCurrentUser();
        if (thisUser.checkBookmarkClub(thisClub)) {
            Log.d(getClass().getSimpleName(), "You have bookmarked it before.");
            bookmark.setChecked(true);
            bookmark.setIcon(R.drawable.ic_action_remove_bookmark);
        } else {
            Log.d(getClass().getSimpleName(), "You have not bookmarked it before.");
            bookmark.setChecked(false);
            bookmark.setIcon(R.drawable.ic_action_add_bookmark);
        }
        // in order to support the native back button
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(getClass().getSimpleName(), "Item with ID" + item.getItemId());
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Snackbar.make(coordinatorLayout,
                        "You selected settings", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            case R.id.action_about:
                Snackbar.make(coordinatorLayout,
                        "You selected About", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case MENU_ITEM_LOGOUT:
                // Logout current user
                ParseUser.logOut();
                Intent intent = new Intent(ClubDetail.this, Login.class);
                startActivity(intent);
                Snackbar.make(coordinatorLayout,
                        "You are logged out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case MENU_ITEM_BOOKMARK:
                if (item.isChecked()) {
                    thisClub.removeBookmarkUser(ParseUser.getCurrentUser());
                    item.setIcon(R.drawable.ic_action_add_bookmark);
                    item.setChecked(false);
                    Snackbar.make(coordinatorLayout,
                            "Bookmark Removed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    thisClub.addBookmarkUser(ParseUser.getCurrentUser());
                    item.setIcon(R.drawable.ic_action_remove_bookmark);
                    item.setChecked(true);
                    Snackbar.make(coordinatorLayout,
                            "Bookmarked", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            case MENU_ITEM_REFRESH:
                eventQueryAdapter.loadObjects();
                eventQueryAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        // in order to support the native back button
        return super.onOptionsItemSelected(item);
    }

    private void setupEventList() {
        // Get List View
        ListView eventList = (ListView) this.findViewById(R.id.event_list_view);

        ParseQueryAdapter.QueryFactory<Event> factory =
                new ParseQueryAdapter.QueryFactory<Event>() {
                    public ParseQuery<Event> create() {
                        ParseQuery<Event> query = Event.getQuery();
                        if (thisClub != null) {
                            query.whereEqualTo("club", thisClub);
                        } else {
                            Toast.makeText(getApplicationContext(), "thisclub is null", Toast.LENGTH_SHORT).show();
                        }
                        // only query on two keys to save time
                        query.selectKeys(Arrays.asList("name", "location", "count"));
                        query.orderByDescending("createdAt");
                        Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };

        eventQueryAdapter = new ParseQueryAdapter<Event>(this, factory) {
            @Override
            public View getItemView(final Event object, View v, ViewGroup parent) {
                if (v == null) {
                    Log.d(getClass().getSimpleName(), "inflating item view");
                    v = View.inflate(getContext(), R.layout.event_list_item, null);
                    // v = LayoutInflater.from(getContext()).
                    // inflate(R.layout.club_list_item, null, false);
                }
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView eventName = (TextView) v.findViewById(R.id.event_list_item_name);
                TextView eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
                final TextView eventCount = (TextView) v.findViewById(R.id.event_list_item_count);
                eventName.setText(object.getEventName());
                eventLocation.setText(object.getEventLocation());
                eventCount.setText(Integer.toString(object.getCount()));
                // follow button setup
                final ToggleButton followButton = (ToggleButton) v.findViewById(R.id.event_list_item_follow);
                /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) followButton.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                followButton.setLayoutParams(params);*/
                User currentUser = (User) ParseUser.getCurrentUser();
                if (currentUser.checkFollowingEvent(object)) {
                    followButton.setChecked(false);
                }
                else
                    followButton.setChecked(true);
                followButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (followButton.isChecked()) {
                                object.removeFollowingUser(ParseUser.getCurrentUser());
                                Snackbar.make(coordinatorLayout,
                                        "You unfollowed this event", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                            else {
                                object.addFollowingUser(ParseUser.getCurrentUser());
                                Snackbar.make(coordinatorLayout,
                                        "You followed this event", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            eventCount.setText(Integer.toString(object.getCount()));

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
                final Event event = eventQueryAdapter.getItem(position);
                event.addFollowingUser((User) ParseUser.getCurrentUser());
                Toast.makeText(getApplicationContext(), "You followed this event", Toast.LENGTH_SHORT).show();
               /* Intent intent = new Intent(Welcome.this, ClubDetail.class);
                intent.putExtra("OBJECT_ID", club.getObjectId());
                startActivity(intent); */
            }
        });

    }

}
