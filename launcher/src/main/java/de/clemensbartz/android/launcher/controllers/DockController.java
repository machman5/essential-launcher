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

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.listeners.DockOnCreateContextMenuListener;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.tasks.LoadApplicationModelIconIntoImageViewTask;
import de.clemensbartz.android.launcher.util.IntentUtil;

/**
 * Controller for handling the dock with its items.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class DockController {

    /** The number of items in the dock. */
    public static final int NUMBER_OF_ITEMS = 7;
    /** The prefix for the pinned dock. */
    @NonNull
    public static final String PIN_PREFIX = "pin_";
    /** The separator between package and class name. */
    @NonNull
    public static final String SEPARATOR = "|";

    /** The list of sorted dock items. */
    @NonNull
    private final ArrayList<ImageView> dockItems;
    /** The weak reference for shared preference dao. */
    @NonNull
    private final WeakReference<SharedPreferencesDAO> sharedPreferencesDAOWeakReference;
    /** The weak reference for the package manager. */
    @NonNull
    private final WeakReference<PackageManager> packageManagerWeakReference;
    /** The default drawable. */
    @NonNull
    private final Drawable defaultDrawable;

    /**
     * Create a new controller for handling dock items.
     * @param context the context to be created in
     * @param dockItems the number of dock items
     * @param sharedPreferencesDAO the shared preference dao
     * @param packageManager the package manager
     * @param defaultDrawable the default drawable
     */
    public DockController(@NonNull final Context context, @Nullable final PackageManager packageManager, @Nullable final SharedPreferencesDAO sharedPreferencesDAO, @NonNull final Drawable defaultDrawable, @NonNull final ArrayList<ImageView> dockItems) {
        this.dockItems = dockItems;
        this.defaultDrawable = defaultDrawable;
        sharedPreferencesDAOWeakReference = new WeakReference<>(sharedPreferencesDAO);
        packageManagerWeakReference = new WeakReference<>(packageManager);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);

            if (launcherApps != null) {
                for (final ImageView imageView : dockItems) {
                    imageView.setOnCreateContextMenuListener(new DockOnCreateContextMenuListener(launcherApps));
                }
            }
        }

        for (final ImageView imageView : dockItems) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (view instanceof ImageView && view.getTag() instanceof ApplicationModel) {
                        final ApplicationModel applicationModel = (ApplicationModel) view.getTag();

                        final Intent intent = IntentUtil.newAppMainIntent(applicationModel.packageName, applicationModel.className);
                        if (IntentUtil.isCallable(context.getPackageManager(), intent)) {
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    /**
     * Update the dock.
     * @param index the index to update
     * @param applicationModel the application model to show or <code>null</code>, if none should be displayed
     */
    public void updateDock(final int index, @Nullable final ApplicationModel applicationModel) {
        if (index < 0 || index >= NUMBER_OF_ITEMS) {
            return;
        }

        // Check for clearing application model
        if (applicationModel == null) {
            clearIndex(index);
            removeFromDatabase(index);

            return;
        }

        if (applicationModel.packageName == null || applicationModel.className == null) {
            clearIndex(index);
            removeFromDatabase(index);

            return;
        }

        final PackageManager packageManager = packageManagerWeakReference.get();

        if (packageManager != null) {
            // Check for deleted packages
            try {
                packageManager.getPackageInfo(applicationModel.packageName, PackageManager.GET_ACTIVITIES);

                // Check if app is callable
                final Intent intent = IntentUtil.newAppMainIntent(applicationModel.packageName, applicationModel.className);

                if (IntentUtil.isCallable(packageManager, intent)) {
                    insertNewItem(index, applicationModel);
                } else {
                    clearIndex(index);
                    removeFromDatabase(index);
                }
            } catch (final PackageManager.NameNotFoundException e) {
                clearIndex(index);
                removeFromDatabase(index);
            }
        }
    }

    /**
     * Insert a new item.
     * @param index the index
     * @param applicationModel the non-null application model to insert
     */
    private void insertNewItem(final int index, @NonNull final ApplicationModel applicationModel) {
        // Update database
        final SharedPreferencesDAO sharedPreferencesDAO = sharedPreferencesDAOWeakReference.get();

        if (sharedPreferencesDAO != null) {
            final String key = getKey(index);
            final String value = applicationModel.packageName + SEPARATOR + applicationModel.className;

            sharedPreferencesDAO.putString(key, value);
        }

        // Update view
        final ImageView imageView = dockItems.get(index);
        imageView.setTag(applicationModel);

        // Load image
        final PackageManager packageManager = packageManagerWeakReference.get();

        if (packageManager != null) {
            new LoadApplicationModelIconIntoImageViewTask(imageView, applicationModel, packageManager, defaultDrawable).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Remove setting for set index value.
     * @param index the valid index to clear
     */
    public void clearIndex(final int index) {
        // Update view
        final ImageView imageView = dockItems.get(index);
        imageView.setTag(null);
        //Load image
        imageView.setImageDrawable(defaultDrawable);
    }

    /**
     * Remove index from database.
     * @param index the index
     */
    private void removeFromDatabase(final int index) {
        // Update database
        final SharedPreferencesDAO sharedPreferencesDAO = sharedPreferencesDAOWeakReference.get();

        if (sharedPreferencesDAO != null) {
            final String key = getKey(index);

            sharedPreferencesDAO.remove(key);
        }
    }

    /**
     * Create a database key for an index.
     * @param index the index
     * @return the key for given index
     */
    @NonNull
    private String getKey(final int index) {
        return PIN_PREFIX + Integer.toString(index);
    }

}
