package com.cyruszhang.cluboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.Club;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangxinyuan on 1/27/16.
 */
public class Welcome extends AppCompatActivity {
    private static final int MENU_ITEM_LOGOUT = 1001;
    private static final int MENU_ITEM_REFRESH = 1002;

    Button myEvents;
    SwipeRefreshLayout swipeRefresh;
    ParseQueryAdapter<Club> clubsQueryAdapter;

    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.welcome_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        // Retrieve current user from Parse.com
        final ParseUser currentUser = ParseUser.getCurrentUser();
        // Convert currentUser into String
        String struser = currentUser.getUsername();

        // add new club floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.welcome_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, NewClub.class);
                startActivity(intent);
            }
        });

        // swipe refresh
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.welcome_swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(getClass().getSimpleName(), "refresh triggered");
                refreshClubList();
            }
        });

        myEvents = (Button) findViewById(R.id.my_events);
        myEvents.setOnClickListener(new View.OnClickListener() {
            //click and go to create club view
            public void onClick(View arg0) {
                Intent intent = new Intent(Welcome.this, MyEvents.class);
                startActivity(intent);
            }
        });
        Button bookmark = (Button) findViewById(R.id.my_bookmark);
        bookmark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Welcome.this, MyBookmark.class);
                startActivity(intent);
            }
        });
        setupClubList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        swipeRefresh.setRefreshing(true);
        refreshClubList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_LOGOUT, 102, "Logout");
        MenuItem refresh = menu.add(0, MENU_ITEM_REFRESH, 103, "Refresh");
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refresh.setIcon(R.drawable.ic_action_refresh);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                //go to setting page
                Snackbar.make(coordinatorLayout,
                        "You selected settings", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(Welcome.this, Setting.class);
                startActivity(intent);

                return true;
            case R.id.action_about:
                Snackbar.make(coordinatorLayout,
                        "You selected About", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case MENU_ITEM_LOGOUT:
                // Logout current user
                ParseUser.logOut();
                intent = new Intent(Welcome.this, Login.class);
                startActivity(intent);
                Snackbar.make(coordinatorLayout,
                        "You are logged out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case MENU_ITEM_REFRESH:
                swipeRefresh.setRefreshing(true);
                refreshClubList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupClubList() {
        // Get List View
        ListView clubList = (ListView) this.findViewById(R.id.club_list_view);

        ParseQueryAdapter.QueryFactory<Club> factory =
                new ParseQueryAdapter.QueryFactory<Club>() {
                    public ParseQuery<Club> create() {
                        ParseQuery<Club> query = Club.getQuery();
                        // only query on two keys to save time
                        query.selectKeys(Arrays.asList("name", "desc"));
                        query.orderByDescending("createdAt");
                        Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };

        clubsQueryAdapter = new ParseQueryAdapter<Club>(this, factory) {
            @Override
            public View getItemView(Club object, View v, ViewGroup parent) {
                // Local DataStore

                if (v == null) {
                    v = View.inflate(getContext(), R.layout.club_list_item, null);
                }
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView clubName = (TextView) v.findViewById(R.id.club_list_item_name);
                TextView clubDetail = (TextView) v.findViewById(R.id.club_list_item_desc);
                clubName.setText(object.getClubName());
                clubDetail.setText(object.getClubDesc());
                return v;
            }
        };
        clubsQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Club>() {
            @Override
            public void onLoading() {
                swipeRefresh.setRefreshing(true);
            }

            @Override
            public void onLoaded(List<Club> objects, Exception e) {
                swipeRefresh.setRefreshing(false);
            }
        });
        Log.d(getClass().getSimpleName(), "setting up adapter");
        clubList.setAdapter(clubsQueryAdapter);

        // item click listener
        clubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Club club = clubsQueryAdapter.getItem(position);
                Intent intent = new Intent(Welcome.this, ClubDetail.class);
                intent.putExtra("OBJECT_ID", club.getObjectId());
                startActivity(intent);
            }
        });
    }

    private void refreshClubList() {
        clubsQueryAdapter.loadObjects();
        clubsQueryAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

}