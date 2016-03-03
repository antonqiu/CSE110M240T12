package com.cyruszhang.cluboard.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.cyruszhang.cluboard.adapter.EventQueryAdapter;
import com.cyruszhang.cluboard.adapter.MyEventsQueryAdapter;
import com.cyruszhang.cluboard.parse.Event;
import com.cyruszhang.cluboard.parse.User;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

public class MyEvents extends AppCompatActivity {
    ParseQueryAdapter<ParseObject> eventQueryAdapter;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinator);
        setupMyEvents();
        // swipe refresh
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_events_swiperefresh);
        // start from the very start
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(getClass().getSimpleName(), "refresh triggered");
                refreshEventList();
            }
        });
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
        eventQueryAdapter = new MyEventsQueryAdapter<ParseObject>(this, factory, coordinatorLayout);
        eventQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(List<ParseObject> objects, Exception e) {
                swipeRefresh.setRefreshing(false);
            }
        });
        Log.d(getClass().getSimpleName(), "setting up adapter");
        eventList.setAdapter(eventQueryAdapter);


        Log.d(getClass().getSimpleName(), "setting up adapter");
        eventList.setAdapter(eventQueryAdapter);
    }


    private void refreshEventList() {
        eventQueryAdapter.loadObjects();
        eventQueryAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

}