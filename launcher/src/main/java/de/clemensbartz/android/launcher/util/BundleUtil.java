/*
 * Copyright (C) 2018  Clemens Bartz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.clemensbartz.android.launcher.util;

import android.appwidget.AppWidgetManager;
import android.os.Bundle;

/**
 * Factory to create option bundles.
 * @author Clemens Bartz
 * @since 1.0
 */
public final class BundleUtil {

    /**
     * Hidden contstructor.
     */
    private BundleUtil() {

    }

    public static Bundle getWidgetOptionsBundle(final int minimumWidth, final int minimumHeight, final int maximumWidth, final int maximumHeight) {
        final Bundle bundle = new Bundle(4);

        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, minimumWidth);
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, maximumWidth);
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, minimumHeight);
        bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, maximumHeight);

        return bundle;
    }
}
