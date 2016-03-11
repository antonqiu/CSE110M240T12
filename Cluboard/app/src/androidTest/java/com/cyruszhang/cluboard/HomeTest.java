package com.cyruszhang.cluboard;

import android.content.Intent;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MenuItem;
import android.view.View;

import com.cyruszhang.cluboard.activity.Home;
import com.cyruszhang.cluboard.activity.Login;
import com.cyruszhang.cluboard.activity.NewClub;
import com.cyruszhang.cluboard.activity.NewEvent;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
/**
 * Created by SC on 3/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class HomeTest {

    @Rule
    public ActivityTestRule<Home> mActivityRule = new ActivityTestRule<>(Home.class);
    private static String mStringToBetyped;

    @Before
    public void initValidString() {
    }


    @Test
    public void a_newClubTest(){
        Random ranGen = new Random();
        mStringToBetyped = String.valueOf(ranGen.nextInt(900000) + 100000);

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
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withText("All Clubs")).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withText(mStringToBetyped)).perform(click());

        onView(isRoot()).perform(waitId(R.id.club_detail_new_event_button, 20000));
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 3000));
        onView(withId(R.id.club_detail_new_event_button)).perform(click());
        inputEventInfo();
        onView(withText("CREATE")).perform(click());

        Intents.release();
    }

    @Test
    public void b_bookmarkClub() {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("All Clubs")).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withText(mStringToBetyped)).perform(click());

        // wait
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(isRoot()).perform(waitId(R.id.club_detail_new_event_button, 10000));

        // bookmark it
        onView(isRoot()).perform(waitId(R.id.event_list_item_follow, 20000));
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withId(R.id.event_list_item_follow)).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
    }

    public void inputInfo() {
        onView(withId(R.id.new_club_name)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_desc)).perform(typeText("Demo"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_email)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_phone)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_club_detail)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
    }

    void inputEventInfo() {
        onView(isRoot()).perform(waitId(R.id.new_event_name, 20000));
        onView(withId(R.id.new_event_name)).perform(typeText(mStringToBetyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_event_location)).perform(typeText("location"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.new_event_desc)).perform(typeText("desc"), ViewActions.closeSoftKeyboard());
    }

    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
//                throw new PerformException.Builder()
//                        .withActionDescription(this.getDescription())
//                        .withViewDescription(HumanReadables.describe(view))
//                        .withCause(new TimeoutException())
//                        .build();
            }
        };
    }

    static MenuItemTitleMatcher withTitle(String title) {
        return new MenuItemTitleMatcher(title);
    }

    static class MenuItemTitleMatcher extends BaseMatcher<Object> {
        private final String title;
        public MenuItemTitleMatcher(String title) { this.title = title; }

        @Override public boolean matches(Object o) {
            if (o instanceof MenuItem) {
                return ((MenuItem) o).getTitle().equals(title);
            }
            return false;
        }
        @Override public void describeTo(Description description) { }
    }
}