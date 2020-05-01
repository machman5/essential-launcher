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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test case for checking Drawer behavior.
 * @author Clemens Bartz
 * @since 2.3
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DrawerTest extends AbstractTest {

    /**
     * Check that the default layout is a GridLayout.
     */
    @Test
    public void test1() {
        // Get activity
        final Launcher launcher = launcherActivityTestRule.getActivity();

        // Go to the drawer
        onView(withText(R.string.up)).perform(swipeUp());

        onView(withId(R.id.gvApplications)).check(matches(isDisplayed()));
        onView(withId(R.id.gvApplications)).check(matches(isFocusable()));
        onView(withId(R.id.lvApplications)).check(matches(withEffectiveVisibility(GONE)));
    }

    /**
     * Check that switching to ListView works.
     */
    @Test
    public void test2() {
        // Get activity
        final Launcher launcher = launcherActivityTestRule.getActivity();

        // Go to the drawer
        onView(withText(R.string.up)).perform(swipeUp());

        // Open the action bar menu
        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());
        // Click "showAllDockIcons"
        onView(withText(R.string.showAsGrid)).perform(click());

        onView(withId(R.id.lvApplications)).check(matches(isDisplayed()));
        onView(withId(R.id.lvApplications)).check(matches(isFocusable()));
        onView(withId(R.id.gvApplications)).check(matches(withEffectiveVisibility(GONE)));
    }
}
