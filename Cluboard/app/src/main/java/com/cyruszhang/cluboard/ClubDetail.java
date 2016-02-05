package com.cyruszhang.cluboard;

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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ClubDetail extends AppCompatActivity {
    private static final int MENU_ITEM_LOGOUT = 1001;
    private CoordinatorLayout coordinatorLayout;
    private Club thisClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final TextView clubName = (TextView) this.findViewById(R.id.club_detail_name);
        final TextView clubDetail = (TextView) this.findViewById(R.id.club_detail_detail);

        ParseQuery<Club> query = Club.getQuery();
        query.getInBackground(getIntent().getStringExtra("OBJECT_ID"), new GetCallback<Club>() {
            @Override
            public void done(Club object, ParseException e) {
                if (e == null) {
                    thisClub = (Club) object;
                    Log.d(getClass().getSimpleName(), "got club object" + thisClub.getClubName());
                    clubName.setText(thisClub.getClubName());
                    clubDetail.setText(thisClub.getClubDetail());
                } else {
                    Toast.makeText(getApplicationContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_LOGOUT, 102, "Logout");

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
        }
        return true;
    }

}
