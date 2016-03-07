package com.cyruszhang.cluboard.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.adapter.EventQueryRecyclerAdapter;
import com.cyruszhang.cluboard.parse.Event;
import com.cyruszhang.cluboard.parse.User;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MyEvents extends AppCompatActivity {
    ParseQueryAdapter<ParseObject> eventQueryAdapter;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefresh;
    private EventQueryRecyclerAdapter eventQueryRecyclerAdapter;
    private RecyclerView myEventsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.coordinator);
        setupMyEvents();
        // swipe refresh
        //swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_events_swiperefresh);
        // start from the very start
        /*
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(getClass().getSimpleName(), "refresh triggered");
                refreshEventList();
            }
        });*/
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
       // ListView eventList = (ListView) this.findViewById(R.id.event_list_view);

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

        //Use the following after change MyEvents page to recyclerApatper
        myEventsRecyclerView = (RecyclerView) findViewById(R.id.my_events_recycler);
        eventQueryRecyclerAdapter = new EventQueryRecyclerAdapter<ParseObject, EventQueryRecyclerAdapter.CardViewHolder>(factory, true) {
            @Override
            public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                // Inflate the custom layout
                View contactView = inflater.inflate(R.layout.event_card_item, parent, false);
                // Return a new holder instance
                return new CardViewHolder(contactView);
            }

            @Override
            public void getParseObject(int position) {

                myFollowingRelation = getItem(position);
                myEvent = (Event)myFollowingRelation.getParseObject("eventObject");

            }
        };
        myEventsRecyclerView.setAdapter(eventQueryRecyclerAdapter);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    private void refreshEventList() {
        eventQueryAdapter.loadObjects();
        eventQueryAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

}