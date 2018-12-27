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

package de.clemensbartz.android.launcher.tasks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.comparators.ApplicationModelComparator;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.models.ApplicationModel;

/**
 * Load applications for the drawer list adapter.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class LoadDrawerListAdapterTask extends AsyncTask<Integer, Integer, Integer> {

    /** Weak reference to the context. */
    private final WeakReference<Context> contextWeakReference;
    /** Weak reference to the drawer controller. */
    private final WeakReference<DrawerController> drawerControllerWeakReference;
    /** Weak reference to the list adapter. */
    private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;

    /**
     * Create a new drawer list adapter task.
     * @param context the context
     * @param drawerListAdapter the drawer list adapter
     * @param drawerController the drawer controller
     */
    public LoadDrawerListAdapterTask(final Context context, final DrawerController drawerController, final DrawerListAdapter drawerListAdapter) {
        contextWeakReference = new WeakReference<>(context);
        drawerControllerWeakReference = new WeakReference<>(drawerController);
        drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
    }

    @Override
    protected void onPreExecute() {
        final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();

        if (drawerListAdapter != null) {
            drawerListAdapter.clear();
            drawerListAdapter.filter();
        }
    }

    @Override
    protected Integer doInBackground(final Integer... integers) {
        // Check for existing weak references
        final Context context = contextWeakReference.get();
        final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();
        final DrawerController drawerController = drawerControllerWeakReference.get();

        if (context == null || drawerListAdapter == null || drawerController == null) {
            return null;
        }

        // Add apps based on version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For latest Android versions, use LauncherApps
            final LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);

            if (launcherApps == null) {
                return null;
            }

            drawerListAdapter.addAll(getApplicationModelsByLauncherApps(launcherApps, drawerController));
        } else {
            // For older Android versions, use Package Manager
            drawerListAdapter.addAll(getApplicationModelByResolveInfos(context.getPackageManager(), drawerController));
        }

        // Check for cancelling before sorting apps
        if (isCancelled()) {
            return null;
        }

        // Sort apps
        drawerListAdapter.sort(new ApplicationModelComparator(context));

        return 0;
    }

    @Override
    protected void onPostExecute(final Integer integer) {
        final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();

        if (integer != null && integer > -1 && drawerListAdapter != null) {
            new FilterDrawerListAdapterTask(drawerListAdapter).execute();
        }
    }

    /**
     * Return all launchable application models.
     * @param launcherApps the launcher apps instance to query on
     * @param drawerController the drawer list adapter
     * @return a list of application models
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<ApplicationModel> getApplicationModelsByLauncherApps(final LauncherApps launcherApps, final DrawerController drawerController) {
        final UserHandle userHandle = Process.myUserHandle();

        if (userHandle == null) {
            return new ArrayList<>();
        }

        final List<ApplicationModel> applicationModels = new ArrayList<>();

        for (final LauncherActivityInfo launcherActivityInfo : launcherApps.getActivityList(null, Process.myUserHandle())) {
            // Break if the task has been stopped
            if (isCancelled()) {
                return new ArrayList<>();
            }

            final ApplicationInfo applicationInfo = launcherActivityInfo.getApplicationInfo();

            if (applicationInfo == null || !applicationInfo.enabled) {
                continue;
            }

            if (launcherActivityInfo.getComponentName() == null
                    || launcherActivityInfo.getComponentName().getClassName() == null
                    || launcherActivityInfo.getComponentName().getPackageName() == null) {
                continue;
            }

            final ApplicationModel applicationModel = new ApplicationModel();
            applicationModel.className = launcherActivityInfo.getComponentName().getClassName();
            applicationModel.packageName = launcherActivityInfo.getComponentName().getPackageName();
            applicationModel.label = getLabel(launcherActivityInfo.getLabel(), launcherActivityInfo.getName());

            applicationModel.hidden = drawerController.isHiding(applicationModel);

            applicationModels.add(applicationModel);

        }

        return applicationModels;
    }

    /**
     * Return all launchable applications models based on resolve infos. This is a legacy method
     * for devices <= LOLLIPOP (21).
     * @param packageManager the package manager
     * @param drawerController the drawer controller
     * @return a list of application models
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private List<ApplicationModel> getApplicationModelByResolveInfos(final PackageManager packageManager, final DrawerController drawerController) {
        final List<ApplicationModel> applicationModels = new ArrayList<>();

        for (final ResolveInfo resolveInfo : getLaunchableResolveInfos(packageManager)) {
            // Break if the task has been stopped
            if (isCancelled()) {
                return new ArrayList<>();
            }

            // Skip for non-launchable, non-existing activities
            if (!resolveInfo.activityInfo.exported
                    || resolveInfo.activityInfo.packageName == null
                    || resolveInfo.activityInfo.name == null) {

                continue;
            }

            final ApplicationModel applicationModel = new ApplicationModel();
            applicationModel.packageName = resolveInfo.activityInfo.packageName;
            applicationModel.className = resolveInfo.activityInfo.name;
            applicationModel.label = getLabel(resolveInfo.loadLabel(packageManager), resolveInfo.activityInfo.name);

            applicationModel.hidden = drawerController.isHiding(applicationModel);

            applicationModels.add(applicationModel);
        }

        return applicationModels;
    }

    /**
     * Query package manager for all launchable resolve infos.
     * @param packageManager the package manager
     * @return a list of apps that are launchable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private List<ResolveInfo> getLaunchableResolveInfos(final PackageManager packageManager) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return packageManager.queryIntentActivities(intent, 0);
    }

    /**
     * Return the label for an app. This function checks for <code>null</code> values.
     * @param label the label of the app
     * @param name the name of the app
     * @return the label, the name or empty string, if one of the previous values are <code>null</code>
     */
    private String getLabel(final CharSequence label, final String name) {
        if (label != null) {
            return label.toString();
        }

        if (name != null) {
            return name;
        }

        return "";
    }
}
