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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewEvent extends AppCompatActivity {
    private static final int MENU_ITEM_CREATE = 1001;

    EditText eventName;
    EditText eventDesc;
    EditText eventLocation;
    String eventNametxt;
    String eventDesctxt;
    String eventLocationtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventName = (EditText) findViewById(R.id.new_event_name);
        eventDesc = (EditText) findViewById(R.id.new_event_desc);
        eventLocation = (EditText) findViewById(R.id.new_event_location);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem create = menu.add(0, MENU_ITEM_CREATE, 103, "Create");
        create.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        create.setTitle("CREATE");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_CREATE:
                if (createEvent()) {
                    finish();
                }
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private boolean createEvent() {
        eventNametxt = eventName.getText().toString();
        eventDesctxt = eventDesc.getText().toString();
        eventLocationtxt = eventLocation.getText().toString();
        //if user does not input name and description
        if(eventNametxt.equals("") || eventDesctxt.equals("") || eventLocationtxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please complete the club name and description",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        final Event newEvent = new Event();
        newEvent.setEventName(eventNametxt);
        newEvent.setEventDesc(eventDesctxt);
        newEvent.setEventLocation(eventLocationtxt);


        ParseQuery<Club> query = Club.getQuery();
        query.getInBackground(getIntent().getStringExtra("OBJECT_ID"), new GetCallback<Club>() {
            @Override
            public void done(Club object, ParseException e) {
                if (e == null) {
                    Club thisClub = (Club) object;
                    Log.d(getClass().getSimpleName(), "got club object" + thisClub.getClubName());
                    // thisClub.addEvent(newEvent);
                    ParseACL clubAcl = thisClub.getACL();
                    newEvent.setACL(clubAcl);
                    newEvent.put("club", thisClub);
                    ParseObject newRelation = new ParseObject("FollowingRelations");
                    newRelation.put("eventObject", newEvent);
                    ParseACL relationACL = new ParseACL();
                    relationACL.setPublicReadAccess(true);
                    relationACL.setPublicWriteAccess(true);
                    newRelation.setACL(relationACL);
                    newRelation.saveInBackground();
                    newEvent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            finish();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        return true;
    }

}
