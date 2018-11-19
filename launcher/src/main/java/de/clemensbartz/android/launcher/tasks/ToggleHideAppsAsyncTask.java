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

import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;

/**
 * Task to toggle hiding apps in the drawer.
 * @author Clemens Bartz
 * @since 1.6
 */
public final class ToggleHideAppsAsyncTask extends AsyncTask<Boolean, Integer, Integer> {

    /** The weak reference to the adapter. */
    private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;

    /**
     * Create a new task to toggle hiding apps.
     * @param drawerListAdapter the drawer list adapter
     */
    public ToggleHideAppsAsyncTask(final DrawerListAdapter drawerListAdapter) {
        drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
    }

    @Override
    protected Integer doInBackground(final Boolean... booleans) {
        if (booleans.length != 1 || booleans[0] == null) {
            return -1;
        }

        final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();

        if (drawerListAdapter != null) {
            final boolean hideApps = booleans[0];

            drawerListAdapter.setHidingApps(hideApps);

            return 0;
        }

        return -1;
    }

    @Override
    protected void onPostExecute(final Integer integer) {
        if (integer != null && integer >= 0) {
            final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();

            if (drawerListAdapter != null) {
                drawerListAdapter.filter();
            }
        }
    }
}
