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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.util.IntentUtil;

/**
 * Listener for handling clicks on items for all {@link AdapterView AdapterViews.}.
 * @since 2.0
 * @author Clemens Bartz
 */
public final class AdapterViewOnItemClickListener implements AdapterView.OnItemClickListener {

    /** The context in which to handle these requests. */
    private final Context context;

    /**
     * Create a new click listener in a context.
     * @param context the context
     */
    public AdapterViewOnItemClickListener(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public void onItemClick(@Nullable final AdapterView<?> parent, @Nullable final View view, final int position, final long id) {
        if (parent == null) {
            return;
        }

        final Object object = parent.getAdapter().getItem(position);

        if (object instanceof ApplicationModel) {
            final ApplicationModel applicationModel = (ApplicationModel) object;

            if (applicationModel.packageName == null || applicationModel.className == null) {
                return;
            }

            final ComponentName component = new ComponentName(applicationModel.packageName, applicationModel.className);
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(component);

            if (IntentUtil.isCallable(context.getPackageManager(), intent)) {
                context.startActivity(intent);
            }
        }
    }
}
