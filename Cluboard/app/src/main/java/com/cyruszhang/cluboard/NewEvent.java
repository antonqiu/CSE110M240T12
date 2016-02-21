package com.cyruszhang.cluboard;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

        eventName = (EditText) findViewById(R.id.new_event_name);
        eventDesc = (EditText) findViewById(R.id.new_event_desc);
        eventLocation = (EditText) findViewById(R.id.new_event_location);
        dateView = (TextView) findViewById(R.id.new_date_selected);
        timeView = (TextView) findViewById(R.id.new_time_selected);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        showTime(hour, minute);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(998);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        else if (id == 998) {
            Log.d("New Event", "998 called");
            return new TimePickerDialog(this, myTimeListener, hour, minute, false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showTime(hour, minute);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(month).append("/")
                        .append(day).append("/").append(year));
    }

    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        timeView.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
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

    private Date getEventDate(int year, int month, int day, int hour, int minute) {
        Log.d(getClass().getSimpleName(), Integer.toString(year));

        String dateString = "";
        if (month < 9)
            dateString = "0";
        dateString += (month+1) + "/";
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
        }
        catch (Exception e) {
            Log.d("NewEvent", "data parse error");
            return null;
        }
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
                    Date eventDate = getEventDate(year, month, day, hour, minute);


                    // thisClub.addEvent(newEvent);
                    ParseACL clubAcl = thisClub.getACL();
                    newEvent.setACL(clubAcl);
                    newEvent.put("club", thisClub);
                    ParseObject newRelation = new ParseObject("FollowingRelations");
                    newRelation.put("eventObject", newEvent);
                    newRelation.put("count", 0);
                    if (eventDate != null) {
                        newEvent.put("eventTime", eventDate.getTime());
                    }
                    else
                        Log.d(getClass().getSimpleName(), "eventDate null");
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
