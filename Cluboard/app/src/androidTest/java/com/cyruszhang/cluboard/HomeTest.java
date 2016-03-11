package com.cyruszhang.cluboard;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.cyruszhang.cluboard.activity.Home;
import com.cyruszhang.cluboard.activity.Login;
import com.cyruszhang.cluboard.activity.NewClub;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by SC on 3/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class HomeTest {

    @Rule
    public ActivityTestRule<Home> mActivityRule = new ActivityTestRule<>(Home.class);


    @Test
    public void newClubButtonTest(){
        Intents.init();
        onView(withContentDescription("Open navigation drawer")).perform(click());

        onView(withText("New Club")).perform(click());

        intended(hasComponent(NewClub.class.getName()));

        Intents.release();
    }

    @Test
    public void myBookMarkButtonTest(){
        Intents.init();

        onView(withContentDescription("Open navigation drawer")).perform(click());

        onView(withText("Bookmarked Clubs")).perform(click());

        hasComponent("MyBookmark");

        Intents.release();

    }

    @Test
    public void myEventsButtonTest(){
        Intents.init();

        onView(withContentDescription("Open navigation drawer")).perform(click());

        onView(withText("Followed Events")).perform(click());

        hasComponent("MyEvents");

        Intents.release();

    }

    @Test
    public void settingsButtonTest(){
        Intents.init();

        onView(withContentDescription("Open navigation drawer")).perform(click());

        onView(withText("Setting")).perform(click());

        hasComponent("Settings");

        Intents.release();

    }


}
