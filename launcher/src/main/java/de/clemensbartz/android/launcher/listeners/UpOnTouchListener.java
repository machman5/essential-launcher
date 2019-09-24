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

package de.clemensbartz.android.launcher.listeners;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.controllers.ViewController;

/**
 * Touch Listener for Up button.
 * @author Clemens Bartz
 * @since 2.2
 */
public class UpOnTouchListener implements View.OnTouchListener {

    /** Weak reference to the {@link ViewController}. */
    @NonNull
    private final WeakReference<ViewController> viewControllerWeakReference;

    /**
     * Create a new touch listener.
     */
    public UpOnTouchListener(@Nullable final ViewController viewController) {
        viewControllerWeakReference = new WeakReference<>(viewController);
    }

    @Override
    public boolean onTouch(@Nullable final View view, @NonNull final MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_UP) {
            return false;
        }

        final ViewController viewController = viewControllerWeakReference.get();

        if (viewController != null) {
            //viewController.showDetail();

            if (view != null) {
                view.performClick();
            }

            return true;
        }

        return false;
    }
}
