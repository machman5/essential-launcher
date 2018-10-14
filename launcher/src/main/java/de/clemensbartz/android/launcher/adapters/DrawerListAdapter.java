/*
 * Copyright (C) 2017  Clemens Bartz
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

package de.clemensbartz.android.launcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.models.ApplicationModel;

/**
 * Array adapter for the drawer. Takes an @{ApplicationModel}.
 *
 * @author Clemens Bartz
 * @since 1.0
 */
public final class DrawerListAdapter extends ArrayAdapter<ApplicationModel> implements SearchView.OnQueryTextListener {

    /** The layouts per abs list view. */
    private static final int[] ITEM_RESOURCE_IDS = {
            R.layout.grid_drawer_item,
            R.layout.list_drawer_item
    };

    /** The separator for search terms. */
    private static final String FILTER_SEPARATOR = " ";

    /** The list of all application models. */
    private final List<ApplicationModel> unfilteredList = new ArrayList<>();
    /** The list of filtered application models. */
    private final List<ApplicationModel> filteredList = new ArrayList<>();

    /** The lower-cased lowerCaseFilter string. */
    private String lowerCaseFilter = "";

    /**
     * Initializes a new adapter.
     * @param context the activity
     */
    public DrawerListAdapter(
            final Context context) {

        super(context, R.layout.grid_drawer_item);
    }

    @Override
    public View getView(final int position,
                        final View convertView,
                        final ViewGroup parent) {

        ViewHolder viewHolder;
        View v = convertView;

        if (convertView == null) {
            v = LayoutInflater.from(getContext()).inflate(getResource(parent), null);

            viewHolder = new ViewHolder();
            viewHolder.icon = v.findViewById(R.id.icon);
            viewHolder.name = v.findViewById(R.id.name);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        final ApplicationModel resolveInfo = getItem(position);

        if (resolveInfo != null && viewHolder != null) {
            viewHolder.icon.setContentDescription(resolveInfo.label);
            viewHolder.icon.setImageDrawable(resolveInfo.icon);
            viewHolder.name.setText(resolveInfo.label);
        }

        return v;
    }

    @Override
    public ApplicationModel getItem(final int position) {
        return filteredList.get(position);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public int getPosition(final ApplicationModel item) {
        return filteredList.indexOf(item);
    }

    @Override
    public boolean isEmpty() {
        return unfilteredList.isEmpty();
    }

    @Override
    public void add(final ApplicationModel object) {
        unfilteredList.add(object);

        filter();
    }

    @Override
    public void remove(final ApplicationModel object) {
        unfilteredList.remove(object);

        filter();
    }

    @Override
    public void clear() {
        unfilteredList.clear();

        filter();
    }

    @Override
    public void sort(final Comparator<? super ApplicationModel> comparator) {
        Collections.sort(unfilteredList, comparator);

        filter();
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        lowerCaseFilter = (query == null) ? "" : query.toLowerCase();

        filter();

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        return onQueryTextSubmit(newText);
    }

    /**
     * Update the filtered list.
     */
    private void filter() {
        filteredList.clear();

        // Check for an empty string or a string only consisting of spaces
        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.trim().isEmpty()) {
            filteredList.addAll(unfilteredList);

            notifyDataSetChanged();

            return;
        }

        final String[] lowerCaseWords = lowerCaseFilter.split(FILTER_SEPARATOR);

        for (ApplicationModel applicationModel : unfilteredList) {
            for (String lowerCaseWord : lowerCaseWords) {
                if (lowerCaseWord.isEmpty() || lowerCaseWord.trim().isEmpty()) {
                    continue;
                }

                if (applicationModel.label.toLowerCase().contains(lowerCaseWord) ||
                        applicationModel.className.toLowerCase().contains(lowerCaseWord) ||
                        applicationModel.packageName.toLowerCase().contains(lowerCaseWord)) {

                    filteredList.add(applicationModel);

                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Return the resource for the view
     * @param view the view
     * @return the resource id or <code>-1</code>, if none was found
     */
    private int getResource(final View view) {
        switch (view.getId()) {
            case R.id.gvApplications:
                return ITEM_RESOURCE_IDS[0];
            case R.id.lvApplications:
                return ITEM_RESOURCE_IDS[1];
            default:
                return -1;
        }
    }

    /**
     * View holder class.
     */
    private static class ViewHolder {
        /** The view for the icon. */
        private ImageView icon;
        /** The view for the label. */
        private TextView name;
    }
}
