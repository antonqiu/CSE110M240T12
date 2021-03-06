package com.cyruszhang.cluboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cyruszhang.cluboard.MainActivity;
import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.Club;
import com.cyruszhang.cluboard.parse.User;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.Arrays;

public class MyBookmark extends AppCompatActivity {
    private static final int MENU_ITEM_LOGOUT = 1001;
    private static final int MENU_ITEM_REFRESH = 1002;

    ParseQueryAdapter<ParseObject> bookmarkQueryAdapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookmark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        setupClubList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bookmarkQueryAdapter.loadObjects();
        bookmarkQueryAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(MyBookmark.this, Settings.class);
                startActivity(intent);

                return true;
            case R.id.action_about:
                Snackbar.make(coordinatorLayout,
                        "You selected About", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            case MENU_ITEM_LOGOUT:
                // Logout current user
                ParseUser.logOut();
                intent = new Intent(MyBookmark.this, MainActivity.class);
                startActivity(intent);
                Snackbar.make(coordinatorLayout,
                        "You are logged out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            case MENU_ITEM_REFRESH:
                bookmarkQueryAdapter.loadObjects();
                bookmarkQueryAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: WITH BUG: DO NOT SHOW ANY ITEM
    private void setupClubList() {
        // Get List View
        ListView bookmarkList = (ListView) this.findViewById(R.id.bookmark_list_view);
        final User currentUser = (User) ParseUser.getCurrentUser();

        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery<ParseObject> create() {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("BookmarkRelations");
                        query.whereEqualTo("bookmarkUsers", currentUser);
                        query.selectKeys(Arrays.asList("clubObject"));
                        query.include("clubObject").selectKeys(Arrays.asList("name", "desc"));
                        // only query on two keys to save time
                        query.orderByDescending("createdAt");
                        // Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };
        bookmarkQueryAdapter = new ParseQueryAdapter<ParseObject>(this, factory) {
            @Override
            public View getItemView(ParseObject object, View v, ViewGroup parent) {
                // Local DataStore

                if (v == null) {
                    // Log.d(getClass().getSimpleName(), "inflating item view");
                    v = View.inflate(getContext(), R.layout.club_list_item, null);
                    // v = LayoutInflater.from(getContext()).
                    // inflate(R.layout.club_list_item, null, false);
                }
                // Log.d(getClass().getSimpleName(), "setting up item view");
                TextView clubName = (TextView) v.findViewById(R.id.club_list_item_name);
                TextView clubDetail = (TextView) v.findViewById(R.id.club_list_item_desc);
                Club thisClub = (Club) object.getParseObject("clubObject");
                clubName.setText(thisClub.getClubName());
                clubDetail.setText(thisClub.getClubDetail());
                return v;
            }
        };

        Log.d(getClass().getSimpleName(), "setting up adapter");
        bookmarkList.setAdapter(bookmarkQueryAdapter);

        // item click listener
        bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Club club = (Club) bookmarkQueryAdapter.getItem(position).getParseObject("clubObject");
                Intent intent = new Intent(MyBookmark.this, ClubDetail.class);
                intent.putExtra("OBJECT_ID", club.getObjectId());
                startActivity(intent);
            }
        });
    }
}
