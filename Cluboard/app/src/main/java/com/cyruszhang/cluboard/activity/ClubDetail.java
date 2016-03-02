package com.cyruszhang.cluboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.SampleDispatchActivity;
import com.cyruszhang.cluboard.adapter.EventQueryAdapter;
import com.cyruszhang.cluboard.parse.Club;
import com.cyruszhang.cluboard.parse.Event;
import com.cyruszhang.cluboard.parse.User;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class ClubDetail extends AppCompatActivity {
    private static final int MENU_ITEM_LOGOUT = 1001;
    private static final int MENU_ITEM_BOOKMARK = 1002;
    private static final int MENU_ITEM_REFRESH = 1003;
    private static final int IMAGE_VIEW_ID = View.generateViewId();
    private CoordinatorLayout coordinatorLayout;
    private Menu menu;
    private SwipeRefreshLayout swipeRefresh;
    private Club thisClub;
    private ParseObject thisClubBookmarkRelation;
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
        // swipe refresh
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.club_detail_swiperefresh);
        // start from the very start
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(getClass().getSimpleName(), "refresh triggered");
                refreshEventList();
            }
        });

        // normal intent starts
        if (thisClub == null) {
            thisClub = (Club) ParseObject.createWithoutData("Clubs", getIntent().getStringExtra("OBJECT_ID"));
            Log.d(getClass().getSimpleName(), (String) getIntent().getStringExtra("OBJECT_ID"));
            try {
                thisClub.fetchIfNeededInBackground(new GetCallback<Club>() {
                    @Override
                    public void done(Club object, ParseException e) {
                        thisClub = object;
                        thisClubBookmarkRelation = thisClub.getBookmarkRelations();
                        thisClubBookmarkRelation.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                thisClubBookmarkRelation = object;
                                Log.d(getClass().getSimpleName(), "got club object" + thisClub.getClubName());
                                clubName.setText(thisClub.getClubName());
                                clubDetail.setText(thisClub.getClubDetail());

                                User currentUser = (User) ParseUser.getCurrentUser();
                                ParseACL clubAcl = thisClub.getACL();
                                //if user is owner, show create event button
                                if (clubAcl.getWriteAccess(currentUser)) {
                                    createEventBtn.setVisibility(View.VISIBLE);
                                }
                                swipeRefresh.setRefreshing(true);
                                initBookmark();
                                setupEventList();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Log.d(getClass().getSimpleName(), "fetch failed" + thisClub.getClubName());
            }
        }

        //Testing notification
        /*
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 15);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        */
        //ENDING test notification

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            //click and go to create club view
            public void onClick(View arg0) {
                Intent intent = new Intent(ClubDetail.this, NewEvent.class);
                intent.putExtra("OBJECT_ID", getIntent().getStringExtra("OBJECT_ID"));
                startActivity(intent);
            }
        });

    }

//    @Override
//    protected void onRestart() {
//        refreshEventList();
//        super.onRestart();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        // Logout
        menu.add(0, MENU_ITEM_LOGOUT, 102, "Logout");

        // refresh
        MenuItem refresh = menu.add(0, MENU_ITEM_REFRESH, 103, "Refresh");
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refresh.setIcon(R.drawable.ic_action_refresh);

        // bookmark placeholder
        MenuItem bookmark = menu.add(0, MENU_ITEM_BOOKMARK, 104, "Add Bookmark");
        bookmark.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        bookmark.setCheckable(true);
        bookmark.setIcon(R.drawable.ic_action_add_bookmark);

        // in order to support the native back button
        return super.onCreateOptionsMenu(menu);
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
                break;
            case MENU_ITEM_LOGOUT:
                // Logout current user
                ParseUser.logOut();
                Intent intent = new Intent(ClubDetail.this, SampleDispatchActivity.class);
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
                swipeRefresh.setRefreshing(true);
                refreshEventList();
                break;
            // back button behavior customization
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        // in order to support the native back button
        return super.onOptionsItemSelected(item);
    }

    private void setupEventList() {
        // Get List View
        ListView eventList = (ListView) this.findViewById(R.id.event_list_view);

        // get all following relations
        ParseQuery<ParseObject> queryRelation = ParseQuery.getQuery("FollowingRelations");
        queryRelation.whereEqualTo("clubObject", thisClub);

        ParseQueryAdapter.QueryFactory<Event> factory =
                new ParseQueryAdapter.QueryFactory<Event>() {
                    public ParseQuery<Event> create() {
                        ParseQuery<Event> query = Event.getQuery();
                        query.whereEqualTo("club", thisClub);
                        // only query on two keys to save time
                        query.selectKeys(Arrays.asList("objectId", "name", "following", "fromTime", "toTime", "desc", "location"));
                        query.include("following").include("followingUsers");
                        query.include("following").include("count");
                        query.orderByAscending("fromTime");
                        Log.d("factory", "factory created");
                        return query;
                    }
                };
        Log.d(getClass().getSimpleName(), "factory created");
        //set up initial list view
        eventQueryAdapter = new EventQueryAdapter<>(this, factory, coordinatorLayout);
        eventQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Event>() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(List<Event> objects, Exception e) {
                swipeRefresh.setRefreshing(false);
            }
        });
        Log.d(getClass().getSimpleName(), "setting up adapter");
        eventList.setAdapter(eventQueryAdapter);
    }

    private void initBookmark() {
        User thisUser = (User) ParseUser.getCurrentUser();

        // add bookmark or remove bookmark, + actionbar button
        MenuItem bookmark = menu.getItem(4);

        if (thisUser.checkBookmarkClub(thisClub)) {
            Log.d(getClass().getSimpleName(), "You have bookmarked it before.");
            bookmark.setChecked(true);
            bookmark.setIcon(R.drawable.ic_action_remove_bookmark);
        } else {
            Log.d(getClass().getSimpleName(), "You have not bookmarked it before.");
            bookmark.setChecked(false);
            bookmark.setIcon(R.drawable.ic_action_add_bookmark);
        }
    }

    private void refreshEventList() {
        eventQueryAdapter.loadObjects();
        eventQueryAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

}
