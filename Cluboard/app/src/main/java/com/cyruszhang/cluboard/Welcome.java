package com.cyruszhang.cluboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by zhangxinyuan on 1/27/16.
 */
public class Welcome extends Activity {
    Button logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.welcome);
        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();
        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) findViewById(R.id.txtuser);
        // Set the currentUser String into TextView
        txtuser.setText("You are logged in as " + struser);
        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.logout);
        // Logout Button Click Listener
        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                //ParseUser.logOut();
                Intent intent = new Intent (Welcome.this, Login.class);
                startActivity(intent);
            }
        });

        // Get List View
        // Get Single Item Test View

//        ParseQueryAdapter.QueryFactory<Club> factory =
//                new ParseQueryAdapter.QueryFactory<Club>() {
//                    public ParseQueryAdapter.QueryFactory<Club> create() {
//                        // Specific Queries
//                        return;
//                    }
//                };
//        ClubsQueryAdapter = new ParseQueryAdapter<Club>(this, factory);


    }

}
