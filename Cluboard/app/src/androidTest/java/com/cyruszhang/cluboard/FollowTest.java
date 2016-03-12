package com.cyruszhang.cluboard;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.TimePicker;

import com.cyruszhang.cluboard.activity.ClubDetail;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by zhangxinyuan on 3/11/16.
 */
public class FollowTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void followEvent() {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("All Clubs")).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));

        onView(withId(R.id.fragment_club_catalog_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // wait
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(isRoot()).perform(waitId(R.id.club_detail_new_event_button, 10000));
        // follow event
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(isRoot()).perform(waitId(R.id.event_list_item_follow, 20000));
        //onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 2000));
        onView(withId(R.id.event_list_item_follow)).perform(click());
        //bookmark club
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 3000));
        onView(withId(ClubDetail.MENU_ITEM_BOOKMARK)).perform(click());
        onView(isRoot()).perform(waitId(R.id.manage_clubs_edit_button, 3000));
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


}
