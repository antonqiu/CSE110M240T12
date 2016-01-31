package com.cyruszhang.cluboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClubDetail extends AppCompatActivity {

    private Club thisClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);
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
}
