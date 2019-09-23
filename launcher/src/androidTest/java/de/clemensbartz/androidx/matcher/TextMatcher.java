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
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher to check whether the text of view is equal.
 * <br />
 * Since Android does not feature an interface but this class aims to be general,
 * each and every sub class will need to implemented individually.
 * @since 2.2
 * @author Clemens Bartz
 */
final class TextMatcher extends TypeSafeMatcher<View> {

    /** The text to match against. */
    private final CharSequence text;

    /**
     * Create a new text matcher.
     * @param text the text to search for
     */
    TextMatcher(final CharSequence text) {
        if (text == null) {
            throw new NullPointerException("The text must not be null.");
        }

        this.text = text;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("The text of item the item does not match: " + text);
    }

    @Override
    protected boolean matchesSafely(final View item) {

        if (item instanceof TextView) {
            final TextView textView = (TextView) item;

            return text.equals(textView.getText());
        } else {
            throw new UnsupportedOperationException("This matcher does not support type " + item.getClass().getSimpleName());
        }
    }
}
