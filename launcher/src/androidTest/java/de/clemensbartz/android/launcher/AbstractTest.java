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
import android.view.View;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.rule.ActivityTestRule;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesViews;
import static de.clemensbartz.androidx.matcher.ViewMatchers.childAtPosition;
import static org.hamcrest.Matchers.allOf;

/**
 * Abstract test class. All instrumentation tests have to have this
 * class as their super class.
 * @author Clemens Bartz
 * @since 2.2
 */
public abstract class AbstractTest {

    /**
     * Add test rule.
     */
    @Rule
    public ActivityTestRule<Launcher> launcherActivityTestRule = new ActivityTestRule<>(Launcher.class, false, false);

    /**
     * Check accessibility along the way.
     */
    @BeforeClass
    public static void enableAccessibilityChecks() {
        try {
            AccessibilityChecks.enable().setRunChecksFromRootView(true).setSuppressingResultMatcher(getSuppressingResultMatcher());
        } catch (final IllegalStateException e) {
            // do nothing
        }
    }

    @Before
    public final void before() {
        launcherActivityTestRule.launchActivity(null);
    }

    @After
    public final void after() {
        // Reset settings
        launcherActivityTestRule.getActivity().getPreferences(Context.MODE_PRIVATE).edit().clear().commit();
        // Clear activity
        launcherActivityTestRule.finishActivity();
    }

    /**
     * @return any excluded matchers
     */
    private static Matcher<? super AccessibilityViewCheckResult> getSuppressingResultMatcher() {
        //noinspection unchecked
        return allOf(
                // Exclude the "More Options" ActionBar overflow button for API 23 and higher
                matchesViews(
                        allOf(
                                withContentDescription("More options"),
                                childAtPosition(
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                2),
                                        1),
                                isDisplayed()
                        )
                )
        );
    }
}
