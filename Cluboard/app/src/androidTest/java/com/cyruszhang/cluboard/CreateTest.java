package com.cyruszhang.cluboard;

import android.os.Build;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.cyruszhang.cluboard.activity.ClubDetail;
import com.cyruszhang.cluboard.activity.NewClub;
import com.cyruszhang.cluboard.fragment.FromTimePickerFragment;
import com.cyruszhang.cluboard.fragment.ToTimePickerFragment;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Random;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
/**
 * Created by SC on 3/10/16.
 */
@RunWith(AndroidJUnit4.class)
public class CreateTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    private static String mStringToBetyped;

    @Before
    public void initValidString() {
    }


    @Test
    public void a_newClubTest(){
        onView(withId(com.parse.ui.R.id.login_username_input)).perform(typeText("xinyuan"));
        closeSoftKeyboard();
        onView(withId(com.parse.ui.R.id.login_password_input)).perform(typeText("123456"));
        closeSoftKeyboard();
        onView(withId(com.parse.ui.R.id.parse_login_button)).perform(click());
        onView(isRoot()).perform(waitId(R.id.parse_login_button, 3000));
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
        onView(isRoot()).perform(waitId(ClubDetail.MENU_ITEM_REFRESH, 5000));
        onView(withText(mStringToBetyped)).perform(click());

        onView(isRoot()).perform(waitId(R.id.club_detail_new_event_button, 20000));
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 3000));
        onView(withId(R.id.club_detail_new_event_button)).perform(click());
        inputEventInfo();
        onView(withText("CREATE")).perform(click());
        onView(isRoot()).perform(waitId(R.id.club_detail_new_event_button, 20000));
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withId(ClubDetail.MENU_ITEM_REFRESH)).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        Intents.release();
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
        // date
        onView(withId(R.id.new_date_selected)).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())));
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onView(withText("OK")).perform(click());
        } else {
            onView(withText("Set")).perform(click());
        }

        // from time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 32);
        onView(withId(R.id.new_from_time_selected)).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onView(withText("OK")).perform(click());
        } else {
            onView(withText("Set")).perform(click());
        }

        // to time
        cal.add(Calendar.HOUR, 2);
        onView(withId(R.id.new_to_time_selected)).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onView(withText("OK")).perform(click());
        } else {
            onView(withText("Set")).perform(click());
        }
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

    public static ViewAction setTime(int hour, int minute) {
        final int hourF = hour;
        final int minuteF = minute;
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp = (TimePicker) view;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tp.setHour(hourF);
                    tp.setMinute(minuteF);
                } else {
                    tp.setCurrentHour(hourF);
                    tp.setCurrentMinute(minuteF);
                }

            }
            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
    }


}