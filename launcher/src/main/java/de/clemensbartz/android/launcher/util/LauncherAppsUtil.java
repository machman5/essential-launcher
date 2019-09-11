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

package de.clemensbartz.android.launcher.util;

import android.annotation.TargetApi;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import de.clemensbartz.android.launcher.models.ApplicationModel;

/**
 * Utility class for handling launcher apps requests.
 * @author Clemens Bartz
 * @since 2.0
 */
@TargetApi(Build.VERSION_CODES.N_MR1)
@RequiresApi(Build.VERSION_CODES.N_MR1)
public final class LauncherAppsUtil {

    /**
     * Hidden constructor.
     */
    private LauncherAppsUtil() {

    }

    /**
     * Retrieve all available shortcut infos for an application model from the launcher apps instance.
     * @param launcherApps the launcher apps instance to check
     * @param applicationModel the application model to check
     * @return a list of shortcuts or an empty list, if shortcuts are not permitted or
     * none were found
     */
    @NonNull
    public static List<ShortcutInfo> getShortcutInfos(@Nullable final LauncherApps launcherApps, @Nullable final ApplicationModel applicationModel) {

        // Create an empty list to return in case something went wrong
        final List<ShortcutInfo> emptyShortcutInfos = new ArrayList<>(0);

        // Check for permissions
        if (launcherApps == null || applicationModel == null || !launcherApps.hasShortcutHostPermission()) {
            return emptyShortcutInfos;
        }

        // Build the query
        final LauncherApps.ShortcutQuery shortcutQuery = new LauncherApps.ShortcutQuery();
        shortcutQuery.setQueryFlags(LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC | LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST | LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED);
        shortcutQuery.setPackage(applicationModel.packageName);

        // List all of them
        final List<ShortcutInfo> shortcuts = launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle());

        // Check if shortcuts were returned
        if (shortcuts == null) {
            return emptyShortcutInfos;
        }

        // Then return them
        return shortcuts;
    }
}
