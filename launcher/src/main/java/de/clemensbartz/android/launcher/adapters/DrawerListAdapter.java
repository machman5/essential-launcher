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

package de.clemensbartz.android.launcher.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.models.ApplicationModel;
import de.clemensbartz.android.launcher.tasks.LoadApplicationModelIconIntoImageViewTask;
import de.clemensbartz.android.launcher.util.LocaleUtil;

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
    @NonNull
    private static final String FILTER_SEPARATOR = " ";

    /** The list of all application models. */
    @NonNull
    private final List<ApplicationModel> unfilteredList = new ArrayList<>();
    /** The list of filtered application models. */
    @NonNull
    private final List<ApplicationModel> filteredList = new ArrayList<>();

    /** The default drawable. */
    @NonNull
    private final Drawable defaultDrawable;
    /** The locale of the context. */
    @NonNull
    private final Locale locale;

    /** The lower-cased lowerCaseFilter string. */
    @NonNull
    private String lowerCaseFilter = "";
    /** Should hidden apps be shown. */
    private boolean showHiddenApps = false;

    /**
     * Initializes a new adapter.
     * @param context the activity
     * @param defaultDrawable the default drawable
     */
    public DrawerListAdapter(
            @NonNull final Context context,
            @NonNull final Drawable defaultDrawable) {

        super(context, R.layout.grid_drawer_item);
        this.defaultDrawable = defaultDrawable;
        this.locale = LocaleUtil.getLocale(context);
    }

    @NonNull
    @Override
    public View getView(final int position,
                        @Nullable final View convertView,
                        @NonNull final ViewGroup parent) {

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

        if (viewHolder != null && viewHolder.icon != null && viewHolder.name != null) {
            viewHolder.icon.setContentDescription(resolveInfo.label);
            viewHolder.name.setText(resolveInfo.label);
            // Load icon asynchronously
            new LoadApplicationModelIconIntoImageViewTask(viewHolder.icon, resolveInfo, getContext().getPackageManager(), defaultDrawable).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return v;
    }

    @Override
    @NonNull
    public ApplicationModel getItem(final int position) {
        return filteredList.get(position);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public int getPosition(@Nullable final ApplicationModel item) {
        if (item != null) {
            return filteredList.indexOf(item);
        } else {
            return -1;
        }
    }

    @Override
    public boolean isEmpty() {
        return unfilteredList.isEmpty();
    }

    @Override
    public void add(@Nullable final ApplicationModel object) {
        if (object != null) {
            unfilteredList.add(object);
        }
    }

    @Override
    public void addAll(@NonNull final Collection<? extends ApplicationModel> collection) {
        unfilteredList.addAll(collection);
    }

    @Override
    public void addAll(@NonNull final ApplicationModel... items) {
        unfilteredList.addAll(Arrays.asList(items));
    }

    @Override
    public void remove(@Nullable final ApplicationModel object) {
        if (object != null) {
            unfilteredList.remove(object);
        }
    }

    @Override
    public void clear() {
        unfilteredList.clear();
    }

    @Override
    public void sort(@NonNull final Comparator<? super ApplicationModel> comparator) {
        Collections.sort(unfilteredList, comparator);
    }

    @Override
    public boolean onQueryTextSubmit(@Nullable final String query) {
        if (query == null) {
            lowerCaseFilter = "";
        } else {
            lowerCaseFilter = query.toLowerCase(locale);
        }

        filter();

        return true;
    }

    @Override
    public boolean onQueryTextChange(@Nullable final String newText) {
        return onQueryTextSubmit(newText);
    }

    /**
     *
     * @return if the drawer is hiding apps
     */
    public boolean isShowingHiddenApps() {
        return showHiddenApps;
    }

    /**
     * Set if the drawer should be showing hidden apps.
     * @param showHiddenApps new boolean values
     */
    public void setShowHiddenApps(final boolean showHiddenApps) {
        this.showHiddenApps = showHiddenApps;
    }

    /**
     * Update the filtered list.
     */
    public void filter() {
        filteredList.clear();

        // Check for an empty string or a string only consisting of spaces
        if (lowerCaseFilter.isEmpty() || lowerCaseFilter.trim().isEmpty()) {
            for (final ApplicationModel applicationModel : unfilteredList) {
                if (!showHiddenApps && applicationModel.hidden) {
                    continue;
                }

                filteredList.add(applicationModel);
            }

            notifyDataSetChanged();

            return;
        }

        final String[] lowerCaseWords = lowerCaseFilter.split(FILTER_SEPARATOR);

        for (final ApplicationModel applicationModel : unfilteredList) {
            for (final String lowerCaseWord : lowerCaseWords) {
                if (lowerCaseWord.isEmpty() || lowerCaseWord.trim().isEmpty()) {
                    continue;
                }

                if (!showHiddenApps && applicationModel.hidden) {
                    continue;
                }

                if (applicationModel.label.toLowerCase(locale).contains(lowerCaseWord)
                        || applicationModel.className.toLowerCase(locale).contains(lowerCaseWord)
                        || applicationModel.packageName.toLowerCase(locale).contains(lowerCaseWord)) {

                    filteredList.add(applicationModel);

                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Return the resource for the view.
     * @param view the view
     * @return the resource id or <code>-1</code>, if none was found
     */
    private int getResource(@NonNull final View view) {
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
    static final class ViewHolder {
        /** The view for the icon. */
        @Nullable ImageView icon;
        /** The view for the label. */
        @Nullable TextView name;
    }
}
