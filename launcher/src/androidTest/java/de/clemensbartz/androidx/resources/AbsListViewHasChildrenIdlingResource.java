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

package de.clemensbartz.androidx.resources;

import android.widget.AbsListView;

import androidx.test.espresso.IdlingResource;

/**
 * Idling Resource for checking the number of children in an {@link AbsListView}.
 * @since 2.2
 * @author Clemens Bartz
 */
public final class AbsListViewHasChildrenIdlingResource implements IdlingResource {

    /** The AbsListView to check. */
    private final AbsListView absListView;
    /** The minimum number of children. */
    private final int minChildren;

    /** The resource callback. */
    private ResourceCallback callback;

    /**
     * Create a new resource for a list view and a minimum number of children.
     * @param absListView the list view
     * @param minChildren the minimum number of children
     */
    public AbsListViewHasChildrenIdlingResource(final AbsListView absListView, final int minChildren) {
        if (minChildren < 0) {
            throw new IllegalArgumentException("Number of children cannot be negative: " + minChildren);
        }

        if (absListView == null) {
            throw new IllegalArgumentException("You must supply an actual absListView instance.");
        }

        this.absListView = absListView;
        this.minChildren = minChildren;
    }


    @Override
    public String getName() {
        return absListView.toString() + ":" + minChildren;
    }

    @Override
    public boolean isIdleNow() {
        final boolean idle = absListView.getChildCount() >= minChildren;

        if (idle && callback != null) {
            callback.onTransitionToIdle();
        }

        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(final ResourceCallback callback) {
        this.callback = callback;
    }
}
