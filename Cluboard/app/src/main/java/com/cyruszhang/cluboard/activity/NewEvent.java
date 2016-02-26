package com.cyruszhang.cluboard.activity;

import android.app.DatePickerDialog;
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
    TextView fromTimeView;
    int eventYear, eventMonth, eventDay;
    int fromHour, fromMinute, fromAM_PM;
    public static TimePickerDialog.OnTimeSetListener fromTimeListener;
    public static TimePickerDialog.OnTimeSetListener toTimeListener;
    public static DatePickerDialog.OnDateSetListener DateListener;

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
        dateView = (TextView) findViewById(R.id.new_date_selected);
        fromTimeView = (TextView) findViewById(R.id.new_from_time_selected);
        calendar = Calendar.getInstance();
        eventYear = calendar.get(Calendar.YEAR);
        eventMonth = calendar.get(Calendar.MONTH);
        eventDay = calendar.get(Calendar.DAY_OF_MONTH);
        dateView.setText(new StringBuilder().append(eventMonth + 1).append("/")
                .append(eventDay).append("/").append(eventYear));
        fromHour = calendar.get(Calendar.HOUR);
        fromMinute = calendar.get(Calendar.MINUTE);
        fromAM_PM = calendar.get(Calendar.AM_PM);
        initTime(fromTimeView, fromHour, fromMinute, fromAM_PM);

        fromTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                fromHour = hourOfDay; fromMinute = minute;
                String format;
                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                }
                else if (hourOfDay == 12) {
                    format = "PM";
                } else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                fromTimeView.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute)
                        .append(" ").append(format));
            }
        };

        DateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                eventYear = year; eventMonth = monthOfYear; eventDay = dayOfMonth;
                dateView.setText(new StringBuilder().append(monthOfYear+1).append("/")
                        .append(dayOfMonth).append("/").append(year));
            }
        };
    }

    private void initTime(TextView timeView, int hourOfDay, int minute, int am_pm) {
        timeView.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute)
                .append(" ").append(
                        am_pm == Calendar.AM ? "AM" : "PM"
                ));
    }

    public void setDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void setFromTime(View view) {
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
        // TODO: this is totally not necessary
        clubQuery.getInBackground(getIntent().getStringExtra("OBJECT_ID"), new GetCallback<Club>() {
            @Override
            public void done(Club object, ParseException e) {
                if (e == null) {
                    Club thisClub = (Club) object;
                    Log.d("NewEvent", "got club object" + thisClub.getClubName());

                    Date fromDate = getEventDate(eventYear, eventMonth, eventDay, fromHour, fromMinute);
                    if (fromDate != null) {
                        newEvent.put("fromTime", fromDate);
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
                                newEvent.setFollowingRelations(newRelation);
                                Log.d("NewEvent", "relation should be added");
                                newEvent.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d("NewEvent", "this event was saved");
                                        //TODO improve performance
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