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

package de.clemensbartz.androidx.matcher;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Provides additional functionality not provided by
 * {@link androidx.test.espresso.matcher.ViewMatchers ViewMatchers} class
 * of Espresso.
 * @since 2.2
 * @author Clemens Bartz
 */
public final class ViewMatchers {

    /**
     * Hidden constructor.
     */
    public ViewMatchers() {

    }

    /**
     * Returns a matcher for checking a child at a position.
     * @param parentMatcher the parent matcher
     * @param position the position
     * @return a matcher
     */
    public static Matcher<View> childAtPosition(
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

    /**
     * Returns a matcher for checking the text of a view.
     * @param text the text
     * @return a matcher
     */
    public static Matcher<View> hasText(final CharSequence text) {
        return new TextMatcher(text);
    }
}
