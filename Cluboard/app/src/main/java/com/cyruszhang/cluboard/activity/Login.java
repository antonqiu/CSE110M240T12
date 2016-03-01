package com.cyruszhang.cluboard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cyruszhang.cluboard.MainActivity;
import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {
    // Declare Variables
    Button loginbutton;
    Button signup;
    Button fblogin;
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;

    String fbName;
    String fbEmail;
    String fbPicUrl;
    Bitmap fbPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseLoginBuilder builder = new ParseLoginBuilder(Login.this);
        startActivityForResult(builder.build(), 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = (EditText) findViewById(R.id.Users_username);
        password = (EditText) findViewById(R.id.Users_password);
        // Locate Buttons in main.xml
        loginbutton = (Button) findViewById(R.id.login_button);

        signup = (Button) findViewById(R.id.register_button);
        fblogin = (Button) findViewById(R.id.fb_login_button);

        loginbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // Send data to Parse.com for verification
                ParseUser.logInInBackground(usernametxt, passwordtxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    // If user exist and authenticated, send user to Welcome.class
                                    Intent intent = new Intent(
                                            Login.this,
                                            Welcome.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully Logged in",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No such user exist, please signup",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissions = Arrays.asList("public_profile", "email");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(Login.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {

                            getUserDetailsFromFB();
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            Intent intent = new Intent(
                                    Login.this,
                                    Welcome.class);
                            startActivity(intent);
                            finish();

                        } else {

                            Log.d("MyApp", "User logged in through Facebook!");
                            Intent intent = new Intent(
                                    Login.this,
                                    Welcome.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),
                                    "Successfully Logged in through Facebook!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            // getUserDetailsFromParse();
                        }
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // Force user to fill up the form
                if (usernametxt.equals("") && passwordtxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage
                    User user = new User();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, please log in.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserDetailsFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    fbName = object.getString("name");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fbEmail = object.getString("email");
                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fbPicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    /*Log.e("IMAGE", "URL is: "+fbPicUrl);
                    fbPic = getFacebookProfilePicture(fbPicUrl);
                    if(fbPic == null) {

                        Log.e("IMAGE", "bitMap is null");
                    }*/

                }
                catch(JSONException e) {
                    e.printStackTrace();
                }
                saveNewFBUser();
            }
        });
        Bundle parameter = new Bundle();
        parameter.putString("fields", "name, email, picture");
        request.setParameters(parameter);
        request.executeAsync();
    }

    private void saveNewFBUser() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setUsername(fbName);
        currentUser.setEmail(fbEmail);
        currentUser.put("imageUrl", fbPicUrl);
        // Convert it to byte

        //currentUser.put("profilepic", file);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "New User " + fbName + " Signed Up", Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
 private void saveNewFbUser() {
     final ParseUser parseUser = ParseUser.getCurrentUser();
     parseUser.setUsername(fbName);
     parseUser.setEmail(fbEmail);
     ByteArrayOutputStream stream = new ByteArrayOutputStream();
     // Compress image to lower quality scale 1 - 100
     fbPic.compress(Bitmap.CompressFormat.PNG, 100, stream);
     byte[] data = stream.toByteArray();
     String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
     final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);
     parseFile.saveInBackground(new SaveCallback() {
         @Override
         public void done(ParseException e) {
             parseUser.put("profileThumb", parseFile);
             //Finally save all the user details
             parseUser.saveInBackground(new SaveCallback() {
                 @Override
                 public void done(ParseException e) {
                     Toast.makeText(getApplicationContext(), "New user:" + fbName + " Signed up", Toast.LENGTH_SHORT).show();
                 }
             });
         }
     });
 }

    private Bitmap getFacebookProfilePicture(String url){
        Bitmap bm = null;
        try {
            URL aUrl = new URL(url);
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection connection = (HttpURLConnection) aUrl.openConnection();
            connection.setDoInput(true);

            connection.connect();
            bm = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }
*/

}
