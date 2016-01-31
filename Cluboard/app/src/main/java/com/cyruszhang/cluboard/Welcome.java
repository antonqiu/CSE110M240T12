package com.cyruszhang.cluboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
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
                ParseUser.logOut();
                Intent intent = new Intent (Welcome.this, Login.class);
                startActivity(intent);
            }
        });

        // Get List View
        ListView clubList = (ListView) this.findViewById(R.id.club_list_view);

        ParseQueryAdapter.QueryFactory<Club> factory =
                new ParseQueryAdapter.QueryFactory<Club>() {
                    public ParseQuery<Club> create() {
                        ParseQuery<Club> query = Club.getQuery();
                        query.orderByDescending("createdAt");
                        Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };

        ParseQueryAdapter<Club> clubsQueryAdapter = new ParseQueryAdapter<Club>(this, factory) {
            @Override
            public View getItemView(Club object, View v, ViewGroup parent) {
                if (v == null) {
                    Log.d(getClass().getSimpleName(), "inflating item view");
                    v = View.inflate(getContext(), R.layout.club_list_item, null);
                    // v = LayoutInflater.from(getContext()).
                    // inflate(R.layout.club_list_item, null, false);
                }
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView clubName = (TextView) v.findViewById(R.id.club_name);
                TextView clubDetail = (TextView) v.findViewById(R.id.club_detail);
                clubName.setText(object.getClubName());
                clubDetail.setText(object.getClubDesc());
                return v;
            }
        };
        Log.d(getClass().getSimpleName(), "setting up adapter");
        clubList.setAdapter(clubsQueryAdapter);
    }

}
