package com.cyruszhang.cluboard.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.Club;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;

public class NewClub extends AppCompatActivity {
    private static final int MENU_ITEM_CREATE = 1002;

    private CoordinatorLayout coordinatorLayout;

    EditText clubName;
    EditText clubDesc;
    EditText clubDetail;
    EditText clubEmail;
    EditText clubPhone;
    String clubNametxt;
    String clubDesctxt;
    String clubDetailtxt;
    String clubEmailtxt;
    String clubPhonetxt;

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
        clubEmail = (EditText) findViewById(R.id.new_club_email);
        clubPhone = (EditText) findViewById(R.id.new_club_phone);
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
            case android.R.id.home:
                finish();
                return true;
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
        clubPhonetxt = clubPhone.getText().toString();
        clubEmailtxt = clubEmail.getText().toString();
        //if user does not input name and description
        if(clubNametxt.equals("") || clubDesctxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please complete the club name and description",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        final Club newClub = new Club();
        newClub.setClubName(clubNametxt);
        newClub.setClubDesc(clubDesctxt);
        newClub.setClubDetail(clubDetailtxt);
        newClub.setClubEmail(clubEmailtxt);
        newClub.setClubPhone(clubPhonetxt);
        newClub.setOwner(ParseUser.getCurrentUser());

        // relation
        final ParseObject newRelation = new ParseObject("BookmarkRelations");
        newRelation.put("clubObject", newClub);
        ParseACL relationACL = new ParseACL();
        relationACL.setPublicReadAccess(true);
        relationACL.setPublicWriteAccess(true);
        newRelation.setACL(relationACL);
        //ParseRelation<ParseUser> bookmarkRelation = newRelation.getRelation("bookmarkUsers");
        newRelation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    newClub.setBookmarkRelations(newRelation);
                    Log.d("NewClub", "relation should be added");

                    // clubID: could impact performance a lot
                    ParseQuery<Club> clubQuery = Club.getQuery();
                    clubQuery.selectKeys(Arrays.asList("clubID"));
                    clubQuery.getFirstInBackground(new GetCallback<Club>() {
                        @Override
                        public void done(Club object, ParseException e) {
                            final Club property = object;
                            if (e == null) {
                                property.increment("nextIndex");
                                property.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        int index = property.getNextIndex();
                                        newClub.setClubID(index);
                                        newClub.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Log.d("NewClub", "this event was saved");
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

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
