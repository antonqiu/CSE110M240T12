package com.cyruszhang.cluboard;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.cyruszhang.cluboard.activity.Home;
import com.cyruszhang.cluboard.activity.Login;
import com.parse.ui.ParseLoginFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.util.Log;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;


@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void login() {
        Intents.init();
        onView(withId(com.parse.ui.R.id.login_username_input)).perform(typeText("xinyuan"));
        closeSoftKeyboard();
        onView(withId(com.parse.ui.R.id.login_password_input)).perform(typeText("123456"));
        closeSoftKeyboard();
        onView(withId(com.parse.ui.R.id.parse_login_button)).perform(click());
    }
        /*
        intended(hasComponent(Home.class.getName()));
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Logout")).perform(click());
        intended(hasComponent("MainActivity"));
        Intents.release();






    @Test
    public void testLogout() {
        intended(hasComponent(Home.class.getName()));
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Logout")).perform(click());
        intended(hasComponent("MainActivity"));
        Intents.release();

    }*/

}



