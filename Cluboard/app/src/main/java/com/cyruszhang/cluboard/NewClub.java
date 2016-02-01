package com.cyruszhang.cluboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewClub extends AppCompatActivity {

    EditText clubName;
    EditText clubDesc;
    EditText clubDetail;
    String clubNametxt;
    String clubDesctxt;
    String clubDetailtxt;
    Button createClubBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clubName = (EditText) findViewById(R.id.new_club_name);
        clubDesc = (EditText) findViewById(R.id.new_club_desc);
        clubDetail = (EditText) findViewById(R.id.new_club_detail);

        createClubBtn = (Button) findViewById(R.id.new_club_btn);
        createClubBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (createClub()) {
                    // Intent intent = new Intent(NewClub.this, Welcome.class);
                    // startActivity(intent);
                    finish();
                }
            }
        });
    }

    //create a new club object and store in the database
    private boolean createClub () {
        clubNametxt = clubName.getText().toString();
        clubDesctxt = clubDesc.getText().toString();
        clubDetailtxt = clubDetail.getText().toString();
        //if user does not input name and description
        if(clubNametxt.equals("") || clubDesctxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please complete the club name and description",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        Club newClub = new Club();
        newClub.setClubName(clubNametxt);
        newClub.setClubDesc(clubDesctxt);
        newClub.setClubDetail(clubDetailtxt);
        newClub.setOwner(ParseUser.getCurrentUser());

        // 2
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        newClub.setACL(acl);

        // 3
        newClub.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
        return true;
    }

}
