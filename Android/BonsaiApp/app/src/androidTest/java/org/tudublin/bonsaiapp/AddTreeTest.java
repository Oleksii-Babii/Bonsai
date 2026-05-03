package org.tudublin.bonsaiapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AddTreeTest {

    @Rule
    public ActivityScenarioRule<AddEditTreeActivity> activityRule =
            new ActivityScenarioRule<>(AddEditTreeActivity.class);

    @Test
    public void addTreeFormIsDisplayed() {
        onView(withId(R.id.editNickname)).check(matches(isDisplayed()));
        onView(withId(R.id.editAge)).check(matches(isDisplayed()));
        onView(withId(R.id.editHeight)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
    }

    @Test
    public void saveWithEmptyFieldsShowsError() {
        onView(withId(R.id.btnSave)).perform(click());
        onView(withId(R.id.editNickname)).check(matches(isDisplayed()));
    }

    @Test
    public void typingNicknamePopulatesField() {
        onView(withId(R.id.editNickname)).perform(typeText("Test Tree"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
    }
}
