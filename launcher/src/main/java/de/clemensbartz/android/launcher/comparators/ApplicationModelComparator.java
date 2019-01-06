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

package de.clemensbartz.android.launcher.comparators;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.LocaleUtil;

/**
 * Comparator for comparing {@link ApplicationModel ApplicationModels}.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class ApplicationModelComparator implements Comparator<ApplicationModel> {

    /** The context to do the comparison in. */
    @Nullable
    private final Locale locale;

    /**
     * Create a new comparator in a certain context.
     * @param context the context
     */
    public ApplicationModelComparator(@NonNull final Context context) {
        this.locale = LocaleUtil.getLocale(context);
    }

    @Override
    public int compare(@NonNull final ApplicationModel o1, @NonNull final ApplicationModel o2) {
        if (locale != null) {
            return Collator.getInstance(locale).compare(o1.label, o2.label);
        }

        if (o1.label != null) {
            return o1.label.compareTo(o2.label);
        } else {
            return "".compareTo(o2.label);
        }
    }
}
