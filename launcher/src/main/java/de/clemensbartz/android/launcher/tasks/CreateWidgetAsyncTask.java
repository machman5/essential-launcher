/*
 * Copyright (C) 2018  Clemens Bartz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.clemensbartz.android.launcher.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.controllers.WidgetController;

/**
 * Task to set the visibility of the menu items.
 * <br>
 * This is necessary as otherwise we are not getting back to the main thread.
 * @author Clemens Bartz
 * @since 1.6
 */
public final class CreateWidgetAsyncTask extends AsyncTask<Integer, Integer, Integer> {

    /** Weak reference to the widget controller. */
    private final WeakReference<WidgetController> widgetControllerWeakReference;

    /**
     * Create a new async task.
     * @param widgetController the widget controller
     */
    public CreateWidgetAsyncTask(final WidgetController widgetController) {
        widgetControllerWeakReference = new WeakReference<>(widgetController);
    }

    @Override
    protected Integer doInBackground(final Integer... widgetIds) {
        return widgetIds[0];
    }

    @Override
    protected void onPostExecute(final Integer widgetId) {
        final WidgetController widgetController = widgetControllerWeakReference.get();

        if (widgetController != null) {
            widgetController.createWidget(widgetId);
        }
    }
}
