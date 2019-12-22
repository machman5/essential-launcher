/*
 * Copyright (C) 2019  Clemens Bartz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.clemensbartz.android.launcher;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import de.clemensbartz.androidx.resources.AbsListViewHasChildrenIdlingResource;
import de.clemensbartz.androidx.resources.WaitingIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.clemensbartz.androidx.matcher.ViewMatchers.childAtPosition;
import static de.clemensbartz.androidx.matcher.ViewMatchers.hasContentDescription;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test case for checking Dock behavior.
 * @author Clemens Bartz
 * @since 2.2
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DockTest extends AbstractTest {

    /** The list of regular items. */
    private final int[] regularDockItems = {
            R.id.ivDock1,
            R.id.ivDock2,
            R.id.ivDock3,
            R.id.ivDock4,
            R.id.ivDock5
    };

    /** The list of extended items. */
    private final int[] extendedDockItems = {
            R.id.ivDock6,
            R.id.ivDock7
    };

    /**
     * Test to check that all five icons are shown when starting.
     */
    @Test
    public void test1() {
        for (int id : regularDockItems) {
            onView(withId(id)).check(matches(isCompletelyDisplayed()));
        }
    }

    /**
     * Check that extended icons are shown when the device is in portrait.
     */
    @Test
    public void test2() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final int orientation = context.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            for (int id : extendedDockItems) {
                onView(withId(id)).check(matches(isCompletelyDisplayed()));
            }
        }
    }

    /**
     * Check that extended icons are shown when the device is xLarge.
     */
    @Test
    public void test3() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final Configuration configuration = context.getResources().getConfiguration();

        final boolean xLarge = (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE;

        if (xLarge) {
            for (int id : extendedDockItems) {
                onView(withId(id)).check(matches(isCompletelyDisplayed()));
            }
        }
    }

    /**
     * Check that extended icons are shown when the device xxxHighDPI (>= 640 dpi).
     */
    @Test
    public void test4() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final Configuration configuration = context.getResources().getConfiguration();

        final boolean xxxHighDPI = configuration.densityDpi >= 640;

        if (xxxHighDPI) {
            for (int id : extendedDockItems) {
                onView(withId(id)).check(matches(isCompletelyDisplayed()));
            }
        }
    }

    /**
     * Check that an app is correctly pinned to all regular positions.
     * <br />
     * This test assumes that there are at least as many apps installed on the emulator
     * running the test as there are regular test icons. It will fail otherwise.
     */
    @Test
    public void test5() {

        // Set timeouts for these waiting operations
        IdlingPolicies.setMasterPolicyTimeout(15, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS);

        // Get activity
        final Launcher launcher = launcherActivityTestRule.getActivity();

        for (int i = 0; i < regularDockItems.length; i++) {
            final int id = regularDockItems[i];

            final GridView gridView = launcher.findViewById(R.id.gvApplications);

            // Go to the drawer
            onView(withText(R.string.up)).perform(swipeUp());

            // Wait until the items have been populated
            final AbsListViewHasChildrenIdlingResource absListViewHasChildrenIdlingResource = new AbsListViewHasChildrenIdlingResource(gridView, regularDockItems.length);
            IdlingRegistry.getInstance().register(absListViewHasChildrenIdlingResource);

            // Grid View should be shown
            onView(withId(R.id.gvApplications)).check(matches(isDisplayed()));
            onView(withId(R.id.gvApplications)).check(matches(hasMinimumChildCount(regularDockItems.length)));

            // Save nth text
            final View view = gridView.getChildAt(i);
            final TextView textView = view.findViewById(R.id.name); // Look for the text view inside any layout
            final CharSequence text = textView.getText();
            assertNotNull("Text is null", text);
            assertNotEquals("Text is empty", 0, text.length());

            // Long click on nth icon
            onView(childAtPosition(withId(R.id.gvApplications), i)).perform(longClick());
            // Select pin app in the menu
            onView(withText(R.string.pinApp)).perform(click());
            // Select the item with i+1
            onView(withText(Integer.toString(i + 1))).perform(click());

            // Check that grid is still visible
            onView(withId(R.id.gvApplications)).check(matches(isDisplayed()));

            // Go back
            Espresso.pressBack();

            // Assert that launcher is not null
            assertNotNull("Launcher has been destroyed", launcher);

            // Wait for 500 second as icon loading is an asynchronous task
            final WaitingIdlingResource waitingIdlingResource = new WaitingIdlingResource(1500);
            IdlingRegistry.getInstance().register(waitingIdlingResource);

            // Check the actual text on the nth item
            onView(withId(id)).check(matches(hasContentDescription(text)));
            // Check drawables: still on the to do list...
        }
    }

    /**
     * Check to see if all dock items are shown when the option to do so is enabled.
     */
    @Test
    public void test6() {
        // Go to the drawer
        onView(withText(R.string.up)).perform(swipeUp());
        // Open the action bar menu
        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());
        // Click "showAllDockIcons"
        onView(withText(R.string.showAllDockIcons)).perform(click());
        // Go back
        Espresso.pressBack(); // Drawer
        Espresso.pressBack(); // Home
        // Check that all regular items are visible
        for (final int id : regularDockItems) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
        // Check that all extended items are visible
        for (final int id : extendedDockItems) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

}
