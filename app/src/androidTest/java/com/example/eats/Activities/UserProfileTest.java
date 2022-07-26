package com.example.eats.Activities;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.eats.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserProfileTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void loginActivityTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.userName),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("betram"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.signInBtn), withText("sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.password), withText("123"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("1234"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.password), withText("1234"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.userName), withText("betram"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("betra"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.userName), withText("betra"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.signInBtn), withText("sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.userName), withText("betra"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.userName), withText("betra"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText("betram"));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.userName), withText("betram"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.signInBtn), withText("sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.doneButton), withText("done"),
                        childAtPosition(
                                allOf(withId(R.id.frame_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.followButton),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                9),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.followButton),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                9),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.imageOfPost),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.carView2),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        pressBack();

        ViewInteraction circleImageView = onView(
                allOf(withId(R.id.imageOfOwner),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                7),
                        isDisplayed()));
        circleImageView.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rvUserPosts),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                5)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        pressBack();

        pressBack();

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.food_nearyby), withContentDescription("Map"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        pressBack();

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.postButton), withContentDescription("Post"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction circleImageView2 = onView(
                allOf(withId(R.id.addedImage),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                0),
                        isDisplayed()));
        circleImageView2.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.selectImage), withText("select"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                7),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.captureImage), withText("capture"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                6),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.setPrice),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText10.perform(replaceText("22"), closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.setCaption),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText11.perform(replaceText("testing"), closeSoftKeyboard());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.enterCategory),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText12.perform(replaceText("testing"), closeSoftKeyboard());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.setDescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.textInputLayout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(replaceText("testing 123"), closeSoftKeyboard());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.submitButton), withText("Post"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                4),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.exploreButton), withContentDescription("explore"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.imageOfPost),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.carView2),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        pressBack();

        ViewInteraction circleImageView3 = onView(
                allOf(withId(R.id.cityImage),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        circleImageView3.perform(click());

        ViewInteraction circleImageView4 = onView(
                allOf(withId(R.id.cityImage),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        circleImageView4.perform(click());

        ViewInteraction circleImageView5 = onView(
                allOf(withId(R.id.cityImage),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        circleImageView5.perform(click());

        ViewInteraction circleImageView6 = onView(
                allOf(withId(R.id.cityImage),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        circleImageView6.perform(click());

        ViewInteraction circleImageView7 = onView(
                allOf(withId(R.id.cityImage),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        circleImageView7.perform(click());

        ViewInteraction circleImageView8 = onView(
                allOf(withId(R.id.cityImage),
                        childAtPosition(
                                allOf(withId(R.id.frameLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        circleImageView8.perform(click());

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.categoryItem), withText("mediterranean"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvCategories),
                                        1),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.categoryItem), withText("pastries"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvCategories),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction materialTextView3 = onView(
                allOf(withId(R.id.categoryItem), withText("healthy"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvCategories),
                                        2),
                                0),
                        isDisplayed()));
        materialTextView3.perform(click());

        ViewInteraction materialTextView4 = onView(
                allOf(withId(R.id.categoryItem), withText("mediterranean"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvCategories),
                                        1),
                                0),
                        isDisplayed()));
        materialTextView4.perform(click());

        ViewInteraction materialTextView5 = onView(
                allOf(withId(R.id.categoryItem), withText("healthy"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvCategories),
                                        2),
                                0),
                        isDisplayed()));
        materialTextView5.perform(click());

        ViewInteraction materialTextView6 = onView(
                allOf(withId(R.id.categoryItem), withText("pastries"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvCategories),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView6.perform(click());

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.searchBox),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText14.perform(replaceText("chicken"), closeSoftKeyboard());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.searchButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                1),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.historyButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragmentContainer),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        pressBack();

        ViewInteraction bottomNavigationItemView4 = onView(
                allOf(withId(R.id.userProfile), withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                4),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rvUserPosts),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                5)));
        recyclerView2.perform(actionOnItemAtPosition(0, longClick()));

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.rvUserPosts),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                5)));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        pressBack();

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.deletePostButton), withText("delete"),
                        childAtPosition(
                                allOf(withId(R.id.alertBox),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                6)),
                                1),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction imageView = onView(
                allOf(withId(R.id.postImage),
                        withParent(allOf(withId(R.id.userPost),
                                withParent(withId(R.id.rvUserPosts)))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.postImage),
                        withParent(allOf(withId(R.id.userPost),
                                withParent(withId(R.id.rvUserPosts)))),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.userProfilePic),
                        withParent(withParent(withId(R.id.fragmentContainer))),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

//        ViewInteraction imageView4 = onView(
//                allOf(withId(androidx.appcompat.R.id.navigation_bar_item_icon_view),
//                        withParent(allOf(withId(androidx.appcompat.R.id.navigation_bar_item_icon_container),
//                                withParent(allOf(withId(R.id.exploreButton), withContentDescription("explore"))))),
//                        isDisplayed()));
//        imageView4.check(matches(isDisplayed()));
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
