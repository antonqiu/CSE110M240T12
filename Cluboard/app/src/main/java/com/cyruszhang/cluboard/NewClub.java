package com.cyruszhang.cluboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewClub extends AppCompatActivity {
    private static final int MENU_ITEM_CREATE = 1002;

    private CoordinatorLayout coordinatorLayout;

    EditText clubName;
    EditText clubDesc;
    EditText clubDetail;
    String clubNametxt;
    String clubDesctxt;
    String clubDetailtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clubName = (EditText) findViewById(R.id.new_club_name);
        clubDesc = (EditText) findViewById(R.id.new_club_desc);
        clubDetail = (EditText) findViewById(R.id.new_club_detail);
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
                if (createClub()) {
                    finish();
                }
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
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
        ParseObject newRelation = new ParseObject("BookmarkRelations");
        newRelation.put("clubObject", newClub);
        ParseACL relationACL = new ParseACL();
        relationACL.setPublicReadAccess(true);
        relationACL.setPublicWriteAccess(true);
        newRelation.setACL(relationACL);
        //ParseRelation<ParseUser> bookmarkRelation = newRelation.getRelation("bookmarkUsers");
        newRelation.saveInBackground();

        /* set acl for the club
        write and read access for owner
        read access for public
         */
        setACL(newClub);

        //add club to myClub in user information
        // User currentUser = (User)ParseUser.getCurrentUser();
        // currentUser.setMyclubs(newClub);


        // 3
        newClub.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
        return true;
    }

    /* set acl for the club
        write and read access for owner
        read access for public
         */
    private void setACL(Club newClub) {
        // TODO: role hierarchy discussion
        ParseACL clubAcl = new ParseACL();
        clubAcl.setPublicReadAccess(true);
        String permission = clubNametxt + " " + "Moderator";
        ParseRole moderatorRole = new ParseRole(permission, clubAcl);

        /* allow club creator to change moderatorRole */
        ParseACL roleACL = new ParseACL();
        roleACL.setReadAccess(ParseUser.getCurrentUser(), true);
        roleACL.setWriteAccess(ParseUser.getCurrentUser(), true);
        moderatorRole.setACL(roleACL);

        /* add owner to moderatorRole
            give moderator write access
            set owner as mater of club*/
        moderatorRole.getUsers().add(ParseUser.getCurrentUser());
        moderatorRole.saveInBackground();
        clubAcl.setRoleWriteAccess(permission, true);
        clubAcl.setWriteAccess(ParseUser.getCurrentUser(), true);
        newClub.setACL(clubAcl);

    }


}
