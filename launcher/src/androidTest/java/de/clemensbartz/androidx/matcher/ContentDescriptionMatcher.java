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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher to check whether the text of the content description is equal.
 * @since 2.2
 * @author Clemens Bartz
 */
final class ContentDescriptionMatcher extends TypeSafeMatcher<View> {

    /** The content description to check against. */
    private final CharSequence contentDescription;

    /**
     * Create a new content description matcher for a text.
     * @param contentDescription the text to check
     */
    ContentDescriptionMatcher(final CharSequence contentDescription) {
        if (contentDescription == null) {
            throw new NullPointerException("contentDescription must not be null");
        }

        this.contentDescription = contentDescription;
    }

    @Override
    protected boolean matchesSafely(final View item) {
        return contentDescription.equals(item.getContentDescription());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("checks that content description is equal to " + contentDescription);
    }

    @Override
    protected void describeMismatchSafely(final View item, final Description mismatchDescription) {
        mismatchDescription.appendText("content description was '" + item.getContentDescription() + "' but we expected " + contentDescription);
    }
}
