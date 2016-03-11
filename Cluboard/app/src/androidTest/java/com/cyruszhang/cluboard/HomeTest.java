package com.cyruszhang.cluboard;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
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

import java.util.ArrayList;
import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by SC on 3/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class HomeTest {

    @Rule
    public ActivityTestRule<Home> mActivityRule = new ActivityTestRule<>(Home.class);
    private String mStringToBetyped;

    @Before
    public void initValidString() {
        Random ranGen = new Random();
        mStringToBetyped = String.valueOf(ranGen.nextInt(900000) + 100000);
    }


    @Test
    public void newClubTest(){
        Intents.init();
        onView(withContentDescription("Open navigation drawer")).perform(click());

        onView(withText("New Club")).perform(click());

        intended(hasComponent(NewClub.class.getName()));

        // create without input
        onView(withText("CREATE")).perform(click());
        onView(withText(R.string.warning_complete_club_info))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        // create
        inputInfo();
        onView(withText("CREATE")).perform(click());

        // All Clubs -> Demo
        for (double i = 0.000001; i < 100000000; i *= 3.14) { i /= 3.13;}
        onView(withText("All Clubs")).perform(click());
        onView(withText(mStringToBetyped)).perform(click());

        Intents.release();
    }

//    @Test
//    public void myBookMarkButtonTest(){
//        Intents.init();
//
//        onView(withContentDescription("Open navigation drawer")).perform(click());
//
//        onView(withText("Bookmarked Clubs")).perform(click());
//
//        hasComponent("MyBookmark");
//
//        Intents.release();
//
//    }
//
//    @Test
//    public void myEventsButtonTest(){
//        Intents.init();
//
//        onView(withContentDescription("Open navigation drawer")).perform(click());
//
//        onView(withText("Followed Events")).perform(click());
//
//        hasComponent("MyEvents");
//
//        Intents.release();
//
//    }
//
//    @Test
//    public void settingsButtonTest(){
//        Intents.init();
//
//        onView(withContentDescription("Open navigation drawer")).perform(click());
//
//        onView(withText("Setting")).perform(click());
//
//        hasComponent("Settings");
//
//        Intents.release();
//
//    }

    public void inputInfo() {
        onView(withId(R.id.new_club_name)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_desc)).perform(typeText("Demo"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_email)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_phone)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_detail)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
    }


}
