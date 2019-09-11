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

import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;

/**
 * Expand listener for the SearchView.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class SearchViewOnActionExpandListener implements MenuItem.OnActionExpandListener {

    @Override
    public boolean onMenuItemActionExpand(@NonNull final MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(@NonNull final MenuItem item) {
        final View view = item.getActionView();

        if (view instanceof SearchView) {
            final SearchView searchView = (SearchView) view;
            searchView.setQuery("", true);
        }

        return true;
    }
}
