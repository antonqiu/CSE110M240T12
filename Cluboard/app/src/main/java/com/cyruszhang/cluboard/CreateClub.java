package com.cyruszhang.cluboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateClub extends AppCompatActivity {
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
        setContentView(R.layout.activity_create_club);
        clubName = (EditText) findViewById(R.id.club_name_input);
        clubDesc = (EditText) findViewById(R.id.club_desc_input);
        clubDetail = (EditText) findViewById(R.id.club_detail_input);

        createClubBtn = (Button) findViewById(R.id.createClubBtn);
        createClubBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(createClub()) {
                    Intent intent = new Intent(CreateClub.this, Welcome.class);
                    startActivity(intent);
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
                    Toast.LENGTH_LONG).show();
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
