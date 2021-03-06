package com.cyruszhang.cluboard;

/**
 * Created by Zhiye Zhangon 2016/3/10 0010.
 */

import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;


@RunWith(AndroidJUnit4.class)
public class SignUptest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void signuptest(){



        onView(withId(R.id.parse_signup_button)).perform(click());

      //  intended(toPackage("com.cyruszhang.cluboard.activity.Login"));
        intended(toPackage("com.parse.ui"));

    }


}
