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
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Listener for clicking on menu items that resemble shortcut info items.
 * @author Clemens Bartz
 * @since 2.0
 */
@TargetApi(Build.VERSION_CODES.N_MR1)
@RequiresApi(Build.VERSION_CODES.N_MR1)
public final class ShortcutInfoOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {

    /** The shortcut info. */
    @NonNull
    private final ShortcutInfo shortcutInfo;
    /** The launcher apps. */
    @NonNull
    private final LauncherApps launcherApps;

    /**
     * Create a new listener for clicking menu items.
     * @param shortcutInfo the shortcut info
     * @param launcherApps the launcher apps
     */
    public ShortcutInfoOnMenuItemClickListener(@NonNull final ShortcutInfo shortcutInfo, @NonNull final LauncherApps launcherApps) {
        this.shortcutInfo = shortcutInfo;
        this.launcherApps = launcherApps;
    }

    @Override
    public boolean onMenuItemClick(@Nullable final MenuItem item) {
        if (launcherApps.hasShortcutHostPermission()) {
            launcherApps.startShortcut(shortcutInfo, null, null);

            return true;
        }

        return false;
    }
}
