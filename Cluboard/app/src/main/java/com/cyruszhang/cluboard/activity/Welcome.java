package com.cyruszhang.cluboard.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.parse.ParseInstallation;
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
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;


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

        // Find drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle(toolbar);
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Retrieve current user from Parse.com
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user",currentUser);
        installation.saveInBackground();
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

    private ActionBarDrawerToggle setupDrawerToggle(Toolbar toolbar) {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        swipeRefresh.setRefreshing(true);
        refreshClubList();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
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
        // TODO: but why
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (id) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

//    public void selectDrawerItem(MenuItem menuItem) {
//        // Create a new fragment and specify the planet to show based on
//        // position
//        Fragment fragment = null;
//
//        Class fragmentClass;
//        switch(menuItem.getItemId()) {
//            case R.id.nav_first_fragment:
//                fragmentClass = FirstFragment.class;
//                break;
//            case R.id.nav_second_fragment:
//                fragmentClass = SecondFragment.class;
//                break;
//            case R.id.nav_third_fragment:
//                fragmentClass = ThirdFragment.class;
//                break;
//            default:
//                fragmentClass = FirstFragment.class;
//        }
//
//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//
//        // Highlight the selected item, update the title, and close the drawer
//        menuItem.setChecked(true);
//        setTitle(menuItem.getTitle());
//        mDrawer.closeDrawers();
//    }

}
