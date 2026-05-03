package org.tudublin.bonsaiapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SpeciesBrowseTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void homeScreenIsDisplayed() {
        onView(withId(R.id.btnViewSpecies)).check(matches(isDisplayed()));
    }

    @Test
    public void clickBrowseSpeciesOpensSpeciesList() {
        onView(withId(R.id.btnViewSpecies)).perform(click());
        onView(withId(R.id.recyclerSpecies)).check(matches(isDisplayed()));
    }
}
