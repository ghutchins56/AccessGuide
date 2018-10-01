package com.android.example.accessguide;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Instrumentation.*;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> MainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before() {
        CountingIdlingResource signInIdlingResource = new CountingIdlingResource("SignIn");
        MainActivityTestRule.getActivity().setIdlingResource(signInIdlingResource);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) signInIdlingResource.increment();
        IdlingRegistry registry = IdlingRegistry.getInstance();
        registry.register(signInIdlingResource);
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void test() {
        Intents.intending(IntentMatchers
                .hasAction("com.google.android.gms.location.places.ui.PICK_PLACE"))
                .respondWith(new ActivityResult(Activity.RESULT_OK, new Intent()));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_add),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.item_text_view_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_view_place_list),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("First Baptist Church of Hewitt")));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view_place_list),
                        childAtPosition(
                                withClassName(
                                is("android.support.constraint.ConstraintLayout")), 3)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_view_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("First Baptist Church of Hewitt")));

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.fab_delete),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        ViewInteraction imageView = onView(
                allOf(withId(R.id.image_view_wheelchair_ramp),
                        withContentDescription("Wheelchair ramp"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
