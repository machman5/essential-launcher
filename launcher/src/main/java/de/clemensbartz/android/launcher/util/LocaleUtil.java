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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

/**
 * Utility class for handling {@link Locale Locales}.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class LocaleUtil {

    /**
     * Hidden constructor.
     */
    private LocaleUtil() {
        // nothing here
    }

    /**
     * Get the local for a context.
     * @param context the context
     * @return a locale
     */
    @Nullable
    public static Locale getLocale(@NonNull final Context context) {
        final Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }

        return locale;
    }
}
