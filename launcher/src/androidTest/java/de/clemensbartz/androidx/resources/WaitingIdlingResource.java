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

import androidx.test.espresso.IdlingResource;

/**
 * Idling Resource for waiting a pre-defined interval.
 * @since 2.2
 * @author Clemens Bartz
 */
public class WaitingIdlingResource implements IdlingResource {

    /** The starting milliseconds. */
    private final long startingMilliseconds;
    /** The amount of milliseconds to wait. */
    private final long milliseconds;

    /** The callback. */
    private ResourceCallback callback;

    /**
     * Create a new idling resource for a certain amount.
     * @param milliseconds the number of milliseconds to wait
     */
    public WaitingIdlingResource(final long milliseconds) {
        if (milliseconds < 0) {
            throw new IllegalArgumentException("milliseconds cannot be smaller than 0");
        }

        this.milliseconds = milliseconds;
        this.startingMilliseconds = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return WaitingIdlingResource.class.getSimpleName() + ":" + milliseconds;
    }

    @Override
    public boolean isIdleNow() {
        final boolean isIdle = System.currentTimeMillis() - startingMilliseconds > milliseconds;

        if (isIdle && callback != null) {
            callback.onTransitionToIdle();
        }

        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(final ResourceCallback callback) {
        this.callback = callback;
    }
}
