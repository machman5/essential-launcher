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

package de.clemensbartz.android.launcher.comparators;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * The comparator of strings with locale.
 * @since 2.1
 * @author Clemens Bartz
 */
public final class LocaledStringComparator implements Comparator<String> {

    /** The locale. */
    @Nullable
    private final Locale locale;

    /**
     * Create a new comparator.
     * @param locale the locale
     */
    public LocaledStringComparator(final @Nullable Locale locale) {
        this.locale = locale;
    }

    @Override
    public int compare(final @NonNull String string1, @NonNull final String string2) {
        if (locale != null) {
            return Collator.getInstance(locale).compare(string1, string2);
        } else {
            return string1.compareTo(string2);
        }
    }
}
