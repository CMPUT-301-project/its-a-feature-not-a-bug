
package com.example.its_a_feature_not_a_bug;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
    public void testDeleteEvent() {
        // Start Activity
        ActivityScenario.launch(MainActivity.class);
        // Click on Organizer button
        onView(withId(R.id.button_organizer_login)).perform(click());
        // Click on Add Event button
        onView(withId(R.id.button_new_event)).perform(click());
        // Type "CRYING" in the editText
        onView(withId(R.id.edit_text_event_title)).perform(ViewActions.typeText("CRYING"));
        // Close soft keyboard
        Espresso.closeSoftKeyboard();
        onView(withText("OK")).perform(click());
        // Sleep for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the event CRYING
        onView(withText("CRYING")).perform(click());
        // Click on Delete Event
        onView(withId(R.id.deleteEventButton)).perform(click());
        onView(withText("CRYING")).check(doesNotExist());
    }
    @Test
    public void testUpdateProfile(){
        // Start Activity
        ActivityScenario.launch(MainActivity.class);
        // Click on Organizer button
        onView(withId(R.id.button_organizer_login)).perform(click());
        // Click on Profile button
        onView(withId(R.id.button_profile)).perform(click());
        // Type "Jingyang" in the editTest
        onView(withId(R.id.editTextFullName)).perform(ViewActions.typeText("Jingyang"));
        Espresso.closeSoftKeyboard();
        // Press submit button buttonSubmit
        onView(withId(R.id.buttonSubmit)).perform(click());
        onView(withText("Jingyang")).check(matches(isDisplayed()));
    }

}



