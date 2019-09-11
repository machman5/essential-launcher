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

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;

/**
 * Test case to check for accessibility errors.
 * @author Clemens Bartz
 * @since 2.2
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AccessibilityCheckTest {

    /**
     * Enable accessibility checks.
     */
    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    /**
     * Add test rule.
     */
    @Rule
    public ActivityTestRule<Launcher> launcherActivityTestRule = new ActivityTestRule<>(Launcher.class);

    /**
     * Open the drawer and check that the up icon disappeared.
     */
    @Test
    public void test1() {
        onView(withText(R.string.up)).perform(click());
        onView(withText(R.string.up)).check(matches(not(isDisplayed())));
    }

}