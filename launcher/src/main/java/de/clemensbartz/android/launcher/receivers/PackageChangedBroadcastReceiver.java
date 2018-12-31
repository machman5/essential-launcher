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

package de.clemensbartz.android.launcher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.controllers.DockController;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.tasks.LoadDockTask;
import de.clemensbartz.android.launcher.tasks.LoadDrawerListAdapterTask;

/**
 * Receiver for listening for changed packages. This class only holds weak references and
 * needs to be updated on every "start" event before registering this receiver.
 * <br/>
 * This class is intended to live forever.
 * @author Clemens Bartz
 * @since 2.0
 */
public class PackageChangedBroadcastReceiver extends BroadcastReceiver {

    /** The instance of the receiver. */
    private static PackageChangedBroadcastReceiver instance;

    /** Weak reference to the dock controller. */
    private WeakReference<DockController> dockControllerWeakReference;
    /** Weak reference to the drawer controller. */
    private WeakReference<DrawerController> drawerControllerWeakReference;
    /** Weak reference to the shared preferences dao. */
    private WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference;
    /** Weak reference to the drawer list adapter. */
    private WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;

    /**
     * Create a new changed broad receiver.
     */
    private PackageChangedBroadcastReceiver() {
        this.dockControllerWeakReference = new WeakReference<>(null);
        this.drawerControllerWeakReference = new WeakReference<>(null);
        this.sharedPreferencesDAOWeakReference = new WeakReference<>(null);
        this.drawerListAdapterWeakReference = new WeakReference<>(null);
    }

    /**
     *
     * @return the instance of the receiver
     */
    public static PackageChangedBroadcastReceiver getInstance() {
        if (instance == null) {
            instance = new PackageChangedBroadcastReceiver();
        }

        return instance;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Update dock
        final DockController dockController = dockControllerWeakReference.get();
        final SharedPreferencesDAO sharedPreferencesDAO = sharedPreferencesDAOWeakReference.get();

        if (dockController != null && sharedPreferencesDAO != null) {
            new LoadDockTask(sharedPreferencesDAO, dockController).execute();
        }

        // Update drawer
        final DrawerController drawerController = drawerControllerWeakReference.get();
        final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();

        if (drawerController != null && drawerListAdapter != null && context != null) {
            new LoadDrawerListAdapterTask(context, drawerController, drawerListAdapter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }
}
