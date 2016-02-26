package com.cyruszhang.cluboard.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.parse.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                List<String> permissions = Arrays.asList("public_profile", "user_about_me", "user_relationships", "user_birthday", "user_location", "email");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(Login.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {

                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            Intent intent = new Intent(
                                    Login.this,
                                    Welcome.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),
                                    "Successfully signed up and logged in through Facebook!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            // getUserDetailsFromFB();
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


}
