/*
 * Copyright (C) 2018  Clemens Bartz
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

package de.clemensbartz.android.launcher.controllers;

import android.app.ActionBar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.clemensbartz.android.launcher.R;

/**
 * Controller for flipping between views inside the layout. Central element is the {@link ViewFlipper}.
 * <br/>
 * This class is designed for handling one static "home" and subsequent views are offset by index.
 * Only one index can be selected and is the default when requesting the call for the detail page.
 * <br/>
 * This class is intended only for the lifetime of an activity.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class ViewController extends GestureDetector.SimpleOnGestureListener {

    /** The minimum travel velocity for a motion event to occur. */
    private static final int MINIMUM_VELOCITY_Y = 50;
    /** The minimum trave distance for a motion event to occur. */
    private static final int MINIMUM_DISTANCE_Y = 50;
    /** Key for choosing which layout to display. */
    @NonNull
    public static final String KEY_DRAWER_LAYOUT = "drawerLayout";

    /** Id to identify the home layout. */
    private static final int HOME_ID = 0;
    /** Id to identify the grid layout. */
    public static final int GRID_ID = 1;
    /** Id to identify the list layout. */
    public static final int LIST_ID = 2;

    /** The view switcher. */
    @NonNull
    private final ViewFlipper viewFlipper;

    /** The current index for the view, only values greater than 0 are supported. */
    private int currentDetailIndex = 0;
    /** The action bar. */
    @Nullable
    private ActionBar actionBar;
    /** The menu of the action bar. */
    @Nullable
    private Menu actionBarMenu;


    /**
     * Create a new controller around a view flipper.
     * @param viewFlipper the view flipper
     */
    public ViewController(@NonNull final ViewFlipper viewFlipper) {
        this.viewFlipper = viewFlipper;
    }

    /**
     * Check if a supplied index is valid.
     * @param detailIndex the detail index
     * @return if it is valid
     */
    private boolean isValidDetailIndex(final int detailIndex) {
        return detailIndex >= 0 && viewFlipper.getChildCount() > detailIndex - 1;
    }

    /**
     * Set the displayed child to the index and set the visibility of actionbar accordingly.
     * @param index a valid index
     */
    private void switchTo(final int index) {
        if (index == HOME_ID) {
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
        } else {
            if (actionBar != null && !actionBar.isShowing()) {
                actionBar.show();
            }
        }

        viewFlipper.setDisplayedChild(index);
    }

    /**
     * Show the home page.
     */
    public void showHome() {
        switchTo(HOME_ID);
    }

    /**
     * Show the currently selected detail page. Will show home if detail is not correct.
     */
    public void showDetail() {
        if (!isValidDetailIndex(currentDetailIndex)) {
            showHome();

            return;
        }

        switchTo(currentDetailIndex);
    }

    /**
     *
     * @return the currently selected detail
     */
    public int getCurrentDetailIndex() {
        return currentDetailIndex;
    }

    /**
     * Set the new detail index. Upon setting, the view flipper must already contain that child.
     * @param currentDetailIndex the new detail index
     */
    public void setCurrentDetailIndex(final int currentDetailIndex) {
        if (isValidDetailIndex(currentDetailIndex)) {
            this.currentDetailIndex = currentDetailIndex;
        } else {
            this.currentDetailIndex = HOME_ID;
        }

        if (actionBarMenu != null) {
            actionBarMenu.findItem(R.id.abm_grid_toggle).setChecked(currentDetailIndex == GRID_ID);
        }
    }

    /**
     * Set the new action bar.
     * @param actionBar the new action bar, or <code>null</code>, if none should be regarded
     */
    public void setActionBar(@Nullable final ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    /**
     * Set the new action bar menu.
     * @param actionBarMenu the action bar menu
     */
    public void setActionBarMenu(@Nullable final Menu actionBarMenu) {
        this.actionBarMenu = actionBarMenu;
    }

    @Override
    public boolean onFling(@NonNull final MotionEvent e1, @NonNull final MotionEvent e2, final float velocityX, final float velocityY) {
        if (Math.abs(velocityY) < MINIMUM_VELOCITY_Y) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        final float differenceY = e1.getY() - e2.getY();

        if (differenceY > MINIMUM_DISTANCE_Y) {
            // Do swipe up
            showDetail();

            return true;
        } else {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
