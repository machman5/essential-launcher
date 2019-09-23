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

import android.app.ActionBar;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test case for checking {@link ActionBar} behavior.
 * @author Clemens Bartz
 * @since 2.2
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActionBarTest extends AbstractTest {

    /**
     * On start up, check that the action bar is hidden.
     */
    @Test
    public void test1() {
        // Check that action bar is hidden
        final ActionBar actionBar = launcherActivityTestRule.getActivity().getActionBar();

        assertNotNull("No action bar has been found", actionBar);
        assertFalse("Action bar is shown", actionBar.isShowing());
    }

    /**
     * When viewing the drawer, action bar is shown.
     */
    @Test
    public void test2() {
        // Open the drawer
        onView(withText(R.string.up)).perform(click());

        // Check that action bar is showing
        final ActionBar actionBar = launcherActivityTestRule.getActivity().getActionBar();

        assertNotNull("No action bar has been found", actionBar);
        assertTrue("Action bar is hidden", actionBar.isShowing());
    }

    /**
     * When toggling drawer view, when the drawer is open, the action bar
     * remains showing.
     */
    @Test
    public void test3() {
        // Open the drawer
        onView(withText(R.string.up)).perform(click());
        // Open the action bar menu
        openActionBarOverflowOrOptionsMenu(launcherActivityTestRule.getActivity());
        // Toggle
        onView(withText(R.string.showAsGrid)).perform(click());
        // Check that action bar is (still) showing
        final ActionBar actionBar = launcherActivityTestRule.getActivity().getActionBar();

        assertNotNull("No action bar has been found", actionBar);
        assertTrue("Action bar is hidden", actionBar.isShowing());
    }

}