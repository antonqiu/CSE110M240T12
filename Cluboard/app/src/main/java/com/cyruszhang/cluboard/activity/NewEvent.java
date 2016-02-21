package com.cyruszhang.cluboard.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.fragment.DatePickerFragment;
import com.cyruszhang.cluboard.fragment.TimePickerFragment;
import com.cyruszhang.cluboard.parse.Club;
import com.cyruszhang.cluboard.parse.Event;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NewEvent extends AppCompatActivity {
    private static final int MENU_ITEM_CREATE = 1001;

    EditText eventName;
    EditText eventDesc;
    EditText eventLocation;
    String eventNametxt;
    String eventDesctxt;
    String eventLocationtxt;
    DatePicker datePicker;
    Calendar calendar;
    TextView dateView;
    TextView timeView;
    int year, month, day;
    int hour, minute;
    String format = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TimePickerFragment initTime = new TimePickerFragment();
        eventName = (EditText) findViewById(R.id.new_event_name);
        eventDesc = (EditText) findViewById(R.id.new_event_desc);
        eventLocation = (EditText) findViewById(R.id.new_event_location);
        dateView = (TextView) findViewById(R.id.new_date_selected);
        timeView = (TextView) findViewById(R.id.new_time_selected);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateView.setText(new StringBuilder().append(month + 1).append("/")
                .append(day).append("/").append(year));
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        initTime.initTime(timeView, hour, minute);
    }

    public void setDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void setTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
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
                Log.d(getClass().getSimpleName(), "create button clicked");
                if (createEvent()) {
                    finish();
                }
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private Date getEventDate(int year, int month, int day, int hour, int minute) {
        Log.d(getClass().getSimpleName(), Integer.toString(year));

        String dateString = "";
        if (month < 9)
            dateString = "0";
        dateString += (month + 1) + "/";
        if (day < 10)
            dateString += "0";
        dateString += day + "/" + year + "/";
        if (hour < 10)
            dateString += 0;
        dateString += hour + ":";
        if (minute < 10)
            dateString += 0;
        dateString += minute;
        Log.d(getClass().getSimpleName(), dateString);
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy/hh:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(dateString);
        } catch (Exception e) {
            Log.d("NewEvent", "data parse error");
            return null;
        }
    }

    private boolean createEvent() {
        eventNametxt = eventName.getText().toString();
        eventDesctxt = eventDesc.getText().toString();
        eventLocationtxt = eventLocation.getText().toString();
        //if user does not input name and description
        if (eventNametxt.equals("") || eventDesctxt.equals("") || eventLocationtxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please complete the club name and description",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        final Event newEvent = new Event();
        newEvent.setEventName(eventNametxt);
        newEvent.setEventDesc(eventDesctxt);
        newEvent.setEventLocation(eventLocationtxt);

        ParseQuery<Club> clubQuery = Club.getQuery();
        clubQuery.getInBackground(getIntent().getStringExtra("OBJECT_ID"), new GetCallback<Club>() {
            @Override
            public void done(Club object, ParseException e) {
                if (e == null) {
                    Club thisClub = (Club) object;
                    Log.d("NewEvent", "got club object" + thisClub.getClubName());

                    Date eventDate = getEventDate(year, month, day, hour, minute);
                    if (eventDate != null) {
                        newEvent.put("eventTime", eventDate.getTime());
                    } else
                        Log.d("NewEvent", "eventDate null");
                    // ACL
                    ParseACL clubAcl = thisClub.getACL();
                    newEvent.setACL(clubAcl);
                    // corresponding club
                    newEvent.setClub(thisClub);

                    // setup following relation
                    final ParseObject newRelation = new ParseObject("FollowingRelations");
                    newRelation.put("eventObject", newEvent);
                    newRelation.put("count", 0);
                    ParseACL relationACL = new ParseACL();
                    relationACL.setPublicReadAccess(true);
                    relationACL.setPublicWriteAccess(true);
                    newRelation.setACL(relationACL);
                    newRelation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                newEvent.put("following", newRelation);
                                Log.d("NewEvent", "relation should be added");
                                newEvent.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d("NewEvent", "this event was saved");
                                        finish();
                                    }
                                });
                            }
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