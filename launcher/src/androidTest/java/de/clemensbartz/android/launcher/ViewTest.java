/*
 * Copyright (C) 2020  Clemens Bartz
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

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

import de.clemensbartz.android.launcher.controllers.ViewController;
import de.clemensbartz.android.launcher.util.SystemServiceUtil;
import de.clemensbartz.androidx.resources.WaitingIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test case for checking Drawer behavior.
 * @author Clemens Bartz
 * @since 2.3
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewTest extends AbstractTest {

    /**
     * Check default view.
     */
    @Test
    public void test1() {
        // Check that the view switcher is shown
        onView(withId(R.id.vsLauncher)).check(matches(isDisplayed()));
        // Check that there are three items in the dock
        onView(withId(R.id.vsLauncher)).check(matches(hasMinimumChildCount(3)));
    }

    /**
     * Check that swiping up reveals the drawer.
     */
    @Test
    public void test2() {
        // Go to the drawer
        onView(withText(R.string.up)).perform(swipeUp());

        // Check that the up icon is hidden
        onView(withText(R.string.up)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    /**
     * Check that swiping down reveals the notification area.
     */
    @Test
    public void test3() {
        // Get activity
        final Launcher launcher = launcherActivityTestRule.getActivity();

        assertTrue("Window is not focused", launcher.hasWindowFocus());

        // Show notifications
        onView(withText(R.string.up)).perform(swipeDown());

        // Very crude hack, but animations are not off for the notification area
        int i = Integer.MIN_VALUE;
        while (i < Integer.MAX_VALUE && launcher.hasWindowFocus()) {
            i++;
            Espresso.onIdle();
        }

        assertFalse("Window is focused", launcher.hasWindowFocus());

        final Object statusBarSystemService = SystemServiceUtil.getSystemServiceOrDefault(launcher, ViewController.SYSTEM_SERVICE_NAME_STATUS_BAR, null);

        assertNotNull("Could not get status bar system service. All other tests will be useless.", statusBarSystemService);

        try {
            final Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            final Method expandNotificationsPanelMethod = statusbarManager.getMethod("collapsePanels");

            expandNotificationsPanelMethod.invoke(statusBarSystemService);
        } catch (final Exception e) {
            // Do nothing here
        }

        // Very crude hack, but animations are not off for the notification area
        int j = Integer.MIN_VALUE;
        while (j < Integer.MAX_VALUE && !launcher.hasWindowFocus()) {
            j++;
            Espresso.onIdle();
        }

        assertTrue("Window is not focused", launcher.hasWindowFocus());
    }

    /**
     * Check that a long press reveals the drawer.
     */
    @Test
    public void test4() {
        // Go to the drawer
        onView(withText(R.string.up)).perform(longClick());

        onView(withId(R.id.gvApplications)).check(matches(isDisplayed()));
    }

    /**
     * Check that "Show notifications" is grayed out for swipe up.
     */
    @Test
    public void test5() {
        // Go to the drawer
        onView(withText(R.string.up)).perform(longClick());

        // Open the action bar menu
        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());

        onView(withText(R.string.gestureSwipeUp)).perform(click());

        onView(withText(R.string.showStatusbar)).check(doesNotExist());
    }

    /**
     * Check that "Show notifications" is grayed out for swipe down after setting it for swipe up.
     */
    @Test
    public void test6() {
        // Go to the drawer
        onView(withText(R.string.up)).perform(longClick());

        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());

        onView(withText(R.string.gestureSwipeDown)).perform(click());
        onView(withText(R.string.showStatusbar)).check(matches(isDisplayed()));
        onView(withText(R.string.showDrawer)).perform(click());

        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());

        onView(withText(R.string.gestureSwipeUp)).perform(click());
        onView(withText(R.string.showStatusbar)).check(matches(isDisplayed()));
        onView(withText(R.string.showStatusbar)).perform(click());

        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());

        onView(withText(R.string.gestureSwipeDown)).perform(click());
        onView(withText(R.string.showStatusbar)).check(doesNotExist());
    }
}
