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

import android.annotation.TargetApi;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;

import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.LauncherAppsUtil;

/**
 * Listener for creating the context menu for dock items.
 * @author Clemens Bartz
 * @since 2.0
 */
@TargetApi(Build.VERSION_CODES.N_MR1)
@RequiresApi(Build.VERSION_CODES.N_MR1)
public final class DockOnCreateContextMenuListener implements View.OnCreateContextMenuListener {

    /** Weak reference for the launcher apps. */
    @NonNull
    private final WeakReference<LauncherApps> launcherAppsWeakReference;

    /**
     * Create a new listener.
     * @param launcherApps the launcher apps service
     */
    public DockOnCreateContextMenuListener(@Nullable final LauncherApps launcherApps) {

        launcherAppsWeakReference = new WeakReference<>(launcherApps);
    }

    @Override
    public void onCreateContextMenu(@NonNull final ContextMenu contextMenu, @Nullable final View view, @Nullable final ContextMenu.ContextMenuInfo contextMenuInfo) {
        if (view instanceof ImageView && view.getTag() instanceof ApplicationModel) {
            final ApplicationModel applicationModel = (ApplicationModel) view.getTag();

            contextMenu.setHeaderTitle(applicationModel.label);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                final LauncherApps launcherApps = launcherAppsWeakReference.get();

                for (final ShortcutInfo shortcutInfo : LauncherAppsUtil.getShortcutInfos(launcherApps, applicationModel)) {

                    final MenuItem shortInfoMenuItem = contextMenu.add(0, 0, 0, shortcutInfo.getShortLabel());
                    shortInfoMenuItem.setOnMenuItemClickListener(new ShortcutInfoOnMenuItemClickListener(shortcutInfo, launcherApps));
                }
            }
        }
    }
}
