package com.cyruszhang.cluboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewEvent extends AppCompatActivity {
    EditText eventName;
    EditText eventDesc;
    EditText eventLocation;
    String eventNametxt;
    String eventDesctxt;
    String eventLocationtxt;
    Button createEventBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventName = (EditText) findViewById(R.id.new_event_name);
        eventDesc = (EditText) findViewById(R.id.new_event_desc);
        eventLocation = (EditText) findViewById(R.id.new_event_location);

        createEventBtn = (Button) findViewById(R.id.new_event_btn);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (createEvent()) {
                    // Intent intent = new Intent(NewClub.this, Welcome.class);
                    // startActivity(intent);
                    finish();
                }
            }
        });
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
                    thisClub.addEvent(newEvent);
                    ParseACL clubAcl = thisClub.getACL();
                    newEvent.setACL(clubAcl);
                    newEvent.put("club", thisClub);

                } else {
                    Toast.makeText(getApplicationContext(), "Something is wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        newEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
        return true;
    }

}
