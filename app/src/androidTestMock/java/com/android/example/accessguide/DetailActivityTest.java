package com.android.example.accessguide;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {
    private static final String EXTRA_PLACE_ITEM = "placeItem";
    private static final String EXTRA_PLACE_DETAIL_ITEM = "placeDetailItem";

    @Rule
    public ActivityTestRule<DetailActivity> DetailActivityTestRule
            = new ActivityTestRule<DetailActivity>(DetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            PlaceItem item = new PlaceItem("", "ChIJaTv4NzuHT4YRwZv7HIPeKUk");
            PlaceDetailItem detailItem = new PlaceDetailItem("First Baptist Church of Hewitt",
                    "301 First St, Hewitt, TX 76643, USA",
                    new LatLng(31.459726200000002, -97.19432839999999));
            intent.putExtra(EXTRA_PLACE_ITEM, item);
            intent.putExtra(EXTRA_PLACE_DETAIL_ITEM, detailItem);
            return intent;
        }
    };

    @Before
    public void before() {
        CountingIdlingResource wheelmapIdlingResource = new CountingIdlingResource("Wheelmap");
        DetailActivityTestRule.getActivity().setIdlingResource(wheelmapIdlingResource);
        wheelmapIdlingResource.increment();
        IdlingRegistry registry = IdlingRegistry.getInstance();
        registry.register(wheelmapIdlingResource);
    }

    @Test
    public void test() {
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_view_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("First Baptist Church of Hewitt")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.text_view_address),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        textView3.check(matches(withText("301 First St, Hewitt, TX 76643, USA")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.text_view_access),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        textView4.check(matches(withText("Wheelchair access status is not available")));

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fab_edit),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text_review),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_text_review),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("This is a review."),
                closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_text_review), withText("This is a review."),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.fab_save),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.text_view_review),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_view_review_list),
                                        0),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("This is a review.")));
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
