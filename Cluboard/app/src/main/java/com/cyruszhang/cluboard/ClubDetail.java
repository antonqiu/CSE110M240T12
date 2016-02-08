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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;

public class ClubDetail extends AppCompatActivity {
    private static final int MENU_ITEM_LOGOUT = 1001;
    private static final int MENU_ITEM_ADD_BOOKMARK = 1002;
    private static final int MENU_ITEM_REMOVE_BOOKMARK = 1003;
    private CoordinatorLayout coordinatorLayout;
    private Club thisClub;
    private ParseQueryAdapter<Event> eventQueryAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView clubName = (TextView) this.findViewById(R.id.club_detail_name);
        final TextView clubDetail = (TextView) this.findViewById(R.id.club_detail_detail);
        final Button createEventBtn = (Button) this.findViewById(R.id.new_event_btn);

        ParseQuery<Club> query = Club.getQuery();

        query.getInBackground(getIntent().getStringExtra("OBJECT_ID"), new GetCallback<Club>() {
            @Override
            public void done(Club object, ParseException e) {
                if (e == null) {
                    thisClub = (Club) object;
                    Log.d(getClass().getSimpleName(), "got club object" + thisClub.getClubName());
                    clubName.setText(thisClub.getClubName());
                    clubDetail.setText(thisClub.getClubDetail());
                    //User currentUser = (User)ParseUser.getCurrentUser();

                 /*   String roleName = thisClub.getClubName()+" "+"Moderator";
                    ParseQuery<ParseRole> roleQuery = ParseRole.getQuery();
                    roleQuery.whereEqualTo("name", roleName);
                    roleQuery.whereEqualTo("users", currentUser);
                    roleQuery.findInBackground(new FindCallback<ParseRole>() {
                        @Override
                        public void done(List<ParseRole> objects, ParseException e) {
                            if(e==null) {
                                Log.d("permission", "verified");
                                createEventBtn.setVisibility(View.VISIBLE);
                            }
                            else {
                                Log.d("permission", "denied");
                            }
                        }
                    }); */
                    User currentUser = (User)ParseUser.getCurrentUser();
                    ParseACL clubAcl = thisClub.getACL();
                    //if user is owner, show create event button
                    if(clubAcl.getWriteAccess(currentUser)) {
                        createEventBtn.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            //click and go to create club view
            public void onClick(View arg0) {
                Intent intent = new Intent(ClubDetail.this, NewEvent.class);
                intent.putExtra("OBJECT_ID", getIntent().getStringExtra("OBJECT_ID"));
                startActivity(intent);
            }
        });


        //set event listview
        setupEventList();
    }



    @Override
    public synchronized boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_LOGOUT, 102, "Logout");

        // add bookmark or remove bookmark, + actionbar button
//        if (((User)(User.getCurrentUser())).checkBookmarkClub(thisClub)) {
//            MenuItem bookmark = menu.add(0, MENU_ITEM_REMOVE_BOOKMARK, 103, "Remove Bookmark");
//            bookmark.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            bookmark.setIcon(R.drawable.ic_action_remove_bookmark);
//        }
//        else {
            MenuItem bookmark = menu.add(0, MENU_ITEM_ADD_BOOKMARK, 103, "Add Bookmark");
            bookmark.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            bookmark.setIcon(R.drawable.ic_action_add_bookmark);
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            case MENU_ITEM_LOGOUT:
                // Logout current user
                ParseUser.logOut();
                Intent intent = new Intent(ClubDetail.this, Login.class);
                startActivity(intent);
                Snackbar.make(coordinatorLayout,
                        "You are logged out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            case MENU_ITEM_ADD_BOOKMARK:
                // TODO: should toggle it based on
                thisClub.addBookmarkUser(ParseUser.getCurrentUser());
                Snackbar.make(coordinatorLayout,
                        "Bookmark Added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            case MENU_ITEM_REMOVE_BOOKMARK:
                thisClub.removeBookmarkUser(ParseUser.getCurrentUser());
                Snackbar.make(coordinatorLayout,
                        "Bookmark Removed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }
        return true;
    }

    private void setupEventList() {
        // Get List View
        ListView eventList = (ListView) this.findViewById(R.id.event_list_view);

        ParseQueryAdapter.QueryFactory<Event> factory =
                new ParseQueryAdapter.QueryFactory<Event>() {
                    public ParseQuery<Event> create() {
                        ParseQuery<Event> query = Event.getQuery();
                        // only query on two keys to save time
                        query.selectKeys(Arrays.asList("name", "location"));
                        query.orderByDescending("createdAt");
                        Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };

        eventQueryAdapter = new ParseQueryAdapter<Event>(this, factory) {
            @Override
            public View getItemView(Event object, View v, ViewGroup parent) {
                if (v == null) {
                    Log.d(getClass().getSimpleName(), "inflating item view");
                    v = View.inflate(getContext(), R.layout.event_list_item, null);
                    // v = LayoutInflater.from(getContext()).
                    // inflate(R.layout.club_list_item, null, false);
                }
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView eventName = (TextView) v.findViewById(R.id.event_list_item_name);
                TextView eventLocation = (TextView) v.findViewById(R.id.event_list_item_location);
                eventName.setText(object.getEventName());
                eventLocation.setText(object.getEventLocation());
                return v;
            }
        };
        Log.d(getClass().getSimpleName(), "setting up adapter");
        eventList.setAdapter(eventQueryAdapter);

        // item click listener
        /*
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event event = eventQueryAdapter.getItem(position);
                Intent intent = new Intent(Welcome.this, ClubDetail.class);
                intent.putExtra("OBJECT_ID", club.getObjectId());
                startActivity(intent);
            }
        });
        */
    }

}
