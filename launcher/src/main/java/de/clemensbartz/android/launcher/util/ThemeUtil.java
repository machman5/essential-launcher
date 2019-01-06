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

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;

import de.clemensbartz.android.launcher.R;

/**
 * Util class for handling themes.
 * @author Clemens Bartz
 * @since 1.6
 */
public final class ThemeUtil {

    /**
     * Hidden constructor.
     */
    private ThemeUtil() {

    }

    /**
     * Adjust the theme of an activity.
     * <br>This function is necessary, because API 21-25 do not
     * support theme inheritance in emulators (and I assume in real
     * devices as well).
     * @param activity the activity to configure.
     */
    public static void setTheme(@NonNull final Activity activity) {
        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
            case Build.VERSION_CODES.KITKAT:
            case Build.VERSION_CODES.KITKAT_WATCH:
                activity.setTheme(R.style.API17ActivityStyle);
                break;
            case Build.VERSION_CODES.LOLLIPOP:
            case Build.VERSION_CODES.LOLLIPOP_MR1:
            case Build.VERSION_CODES.M:
            case Build.VERSION_CODES.N:
            case Build.VERSION_CODES.N_MR1:
            case Build.VERSION_CODES.O:
            case Build.VERSION_CODES.O_MR1:
                activity.setTheme(R.style.API21ActivityStyle);
                break;
            default:
                // leave highest default
        }
    }

    /**
     *
     * @param activity the activity where the action bar resides
     * @see <a href="https://gist.github.com/hamakn/8939eb68a920a6d7a498">hamakn's Github gist</a>
     * @return the height of the action bar of that activity
     */
    public static int getActionBarHeight(@NonNull final Activity activity) {
        // action bar height
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[] {
                        android.R.attr.actionBarSize
                }
        );
        final int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarHeight;
    }
}
