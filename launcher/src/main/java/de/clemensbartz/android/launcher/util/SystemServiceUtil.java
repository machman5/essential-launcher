/*
 * Copyright (C) 2019  Clemens Bartz
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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Utility class to query for system services.
 * @since 2.3
 */
public final class SystemServiceUtil {

    /**
     * Hidden constructor.
     */
    private SystemServiceUtil() {
        // Do nothing here
    }

    /**
     * Get a system service or return the default value.
     * @param context the context to get the service from
     * @param name the name of the service
     * @param def the return value
     * @return the service, or <code>def</code>, if an error occurred
     */
    @Nullable
    public static Object getSystemServiceOrDefault(@NonNull final Context context, @NonNull final String name, @Nullable final Object def) {
        try {
            return context.getSystemService(name);
        } catch (final Exception e) {
            return def;
        }
    }
}
