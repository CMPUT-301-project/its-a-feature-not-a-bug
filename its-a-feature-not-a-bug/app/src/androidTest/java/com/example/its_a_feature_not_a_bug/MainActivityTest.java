
package com.example.its_a_feature_not_a_bug;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.doesNotHaveFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;

import androidx.core.widget.ListViewAutoScrollHelper;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testAddEvent() {
        // Start Activity
        ActivityScenario.launch(MainActivity.class);
        // Click on Organizer button
        onView(withId(R.id.button_organizer_login)).perform(click());
        // Click on Add Event button
        onView(withId(R.id.button_new_event)).perform(click());
        // Type "STUDYING" in the editText
        onView(withId(R.id.edit_text_event_title)).perform(ViewActions.typeText("STUDYING"));
        // Close soft keyboard
        Espresso.closeSoftKeyboard();
        onView(withText("OK")).perform(click());
        onView(withText("STUDYING")).check(matches(isDisplayed()));
    }

    @Test
    public void testRemoveEvent() {
        // add an event
        // Start Activity
        ActivityScenario.launch(MainActivity.class);
        // Click on Organizer button
        onView(withId(R.id.button_organizer_login)).perform(click());
        // Click on Add Event button
        onView(withId(R.id.button_new_event)).perform(click());
        // Type "STUDYING" in the editText
        onView(withId(R.id.edit_text_event_title)).perform(ViewActions.typeText("STUDYING"));
        // Close soft keyboard
        Espresso.closeSoftKeyboard();
        onView(withText("OK")).perform(click());
        onView(withText("STUDYING")).check(matches(isDisplayed()));

        // delete the event
        onData(anything())
                .inAdapterView(withId(R.id.list_view_events_list))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.deleteEventButton)).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withText("STUDYING"))
                .check(matches(not(isDisplayed())));
    }
}



