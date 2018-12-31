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

package de.clemensbartz.android.launcher.listeners;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.controllers.DockController;
import de.clemensbartz.android.launcher.controllers.DrawerController;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.IntentUtil;
import de.clemensbartz.android.launcher.util.LauncherAppsUtil;

/**
 * Context Menu Listener for all list views.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class AbsListViewOnCreateContextMenuListener implements View.OnCreateContextMenuListener {

    /** Request code for app info. */
    private static final int ITEM_APP_INFO = 1;
    /** Request code for app pinning. */
    private static final int ITEM_PINTO = 2;
    /** Request code for toggle hidden app. */
    private static final int ITEM_TOGGLE_HIDDEN = 3;

    /** Weak reference to the dock controller. */
    private final WeakReference<DockController> dockControllerWeakReference;
    /** Weak reference to the drawer controller. */
    private final WeakReference<DrawerController> drawerControllerWeakReference;
    /** Weak reference to the drawer list adapter. */
    private final WeakReference<DrawerListAdapter> drawerListAdapterWeakReference;
    /** Weak reference for the package manager. */
    private final WeakReference<PackageManager> packageManagerWeakReference;
    /** Weak reference for the launcher apps. */
    private final WeakReference<LauncherApps> launcherAppsWeakReference;

    /**
     * Create a new listener for the abstract list view.
     * @param packageManager the package manager
     * @param drawerListAdapter the drawer list adapter to get the icons from
     * @param dockController the dock controller
     * @param drawerController the drawer controller
     * @param context the context to get system services
     */
    public AbsListViewOnCreateContextMenuListener(
            final PackageManager packageManager,
            final DrawerController drawerController,
            final DrawerListAdapter drawerListAdapter,
            final DockController dockController,
            final Context context) {

        packageManagerWeakReference = new WeakReference<>(packageManager);
        drawerControllerWeakReference = new WeakReference<>(drawerController);
        drawerListAdapterWeakReference = new WeakReference<>(drawerListAdapter);
        dockControllerWeakReference = new WeakReference<>(dockController);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
            launcherAppsWeakReference = new WeakReference<>(launcherApps);
        } else {
            launcherAppsWeakReference = new WeakReference<>(null);
        }
    }

    @Override
    public void onCreateContextMenu(final ContextMenu contextMenu, final View view, final ContextMenu.ContextMenuInfo contextMenuInfo) {
        final DrawerListAdapter drawerListAdapter = drawerListAdapterWeakReference.get();
        final PackageManager packageManager = packageManagerWeakReference.get();
        final DockController dockController = dockControllerWeakReference.get();
        final DrawerController drawerController = drawerControllerWeakReference.get();

        if (packageManager == null || drawerListAdapter == null) {
            return;
        }

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
        final ApplicationModel applicationModel = drawerListAdapter.getItem(info.position);

        if (applicationModel == null) {
            return;
        }

        contextMenu.setHeaderTitle(applicationModel.label);

        // Optionally add Shortcuts
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final LauncherApps launcherApps = launcherAppsWeakReference.get();

            for (final ShortcutInfo shortcutInfo : LauncherAppsUtil.getShortcutInfos(launcherApps, applicationModel)) {

                final MenuItem shortInfoMenuItem = contextMenu.add(0, 0, 0, shortcutInfo.getShortLabel());
                shortInfoMenuItem.setOnMenuItemClickListener(new ShortcutInfoOnMenuItemClickListener(shortcutInfo, launcherApps));
            }
        }

        final MenuItem itemAppInfo = contextMenu.add(0, ITEM_APP_INFO, 0, R.string.showAppInfo);
        itemAppInfo.setIntent(IntentUtil.newAppDetailsIntent(applicationModel.packageName));

        final SubMenu pinAppSubMenu = contextMenu.addSubMenu(R.string.pinApp);
        for (int i = 0; i < DockController.NUMBER_OF_ITEMS; i++) {
            final MenuItem pinAppMenuItem = pinAppSubMenu.add(0, ITEM_PINTO, 0, Integer.toString(i + 1));

            final int index = i;

            pinAppMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {

                    if (dockController == null) {
                        return false;
                    }

                    dockController.updateDock(index, applicationModel);

                    return true;
                }
            });
        }

        if (drawerController != null) {
            final MenuItem toggleHiddenItem = contextMenu.add(0, ITEM_TOGGLE_HIDDEN, 0, R.string.hidden);
            toggleHiddenItem.setCheckable(true);
            toggleHiddenItem.setChecked(applicationModel.hidden);
            toggleHiddenItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    drawerController.toggleHide(applicationModel);

                    return true;
                }
            });
        }



    }
}
