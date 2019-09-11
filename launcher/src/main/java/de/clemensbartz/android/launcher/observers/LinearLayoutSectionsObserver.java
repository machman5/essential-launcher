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

package de.clemensbartz.android.launcher.observers;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.clemensbartz.android.launcher.listeners.SectionLabelOnClickListener;

/**
 * Observer to update sections in a vertical linear layout.
 * @since 2.1
 * @author Clemens Bartz
 * @param <T> an array adapter that also supports indexing
 */
public final class LinearLayoutSectionsObserver<T extends ArrayAdapter & SectionIndexer> extends DataSetObserver {

    /** The text size for the linear layout items. */
    private static final float TEXT_SIZE = 20.0f;
    /** The padding of text views in relative layout. */
    private static final int PADDING_BOTTOM = 10;
    /** Minimal item count. */
    private static final int MINIMUM_ITEM_COUNT = 3;

    /** The context. */
    @NonNull
    private final Context context;
    /** The list view to see the sections. */
    @NonNull
    private final ListView listView;
    /** The linear layout to update. */
    @NonNull
    private final LinearLayout linearLayout;
    /** The drawer list adapter. */
    @NonNull
    private final T sectionedArrayAdapter;
    /** The height of the action bar. */
    private final int actionBarHeight;

    /**
     * Update the linear layout based on sections.
     * @param context the context to create the layout in
     * @param actionBarHeight the height of the action bar
     * @param listView the list view to be sectioned
     * @param linearLayout the linear layout to update
     * @param sectionedArrayAdapter the array adapter with sections to query from
     */
    public LinearLayoutSectionsObserver(
            @NonNull final Context context,
            final int actionBarHeight,
            @NonNull final ListView listView,
            @NonNull final LinearLayout linearLayout,
            @NonNull final T sectionedArrayAdapter) {

        this.context = context;
        this.listView = listView;
        this.linearLayout = linearLayout;
        this.sectionedArrayAdapter = sectionedArrayAdapter;
        this.actionBarHeight = actionBarHeight;

        sectionedArrayAdapter.registerDataSetObserver(this);
    }

    @Override
    public void onChanged() {
        final int linearLayoutHeight = linearLayout.getMeasuredHeight();

        if (linearLayoutHeight <= 0) {
            return;
        }

        final Object[] sections = sectionedArrayAdapter.getSections();

        // Reduce number of child views according to sections
        for (int i = linearLayout.getChildCount() - 1; i >= sections.length; i--) {
            linearLayout.removeViewAt(i);
        }
        // Increase number of child views according to sections
        for (int i = linearLayout.getChildCount(); i < sections.length; i++) {
            final TextView textView = new TextView(context);
            linearLayout.addView(textView);
        }

        // Check if number of views is the same
        if (sections.length != linearLayout.getChildCount()) {
            throw new RuntimeException("Not enough children.");
        }

        // Fill content of sections to child views
        for (int i = 0; i < sections.length; i++) {
            if (!(sections[i] instanceof String)) {
                continue;
            }

            final String text = (String) sections[i];
            final int pos = sectionedArrayAdapter.getPositionForSection(i);

            final View view = linearLayout.getChildAt(i);

            if (view instanceof TextView) {
                final TextView textView = (TextView) view;

                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
                textView.setText(text);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(0, 0, 0, PADDING_BOTTOM);
                textView.setVisibility(View.VISIBLE);
                textView.setClickable(true);

                textView.setOnClickListener(new SectionLabelOnClickListener(listView, pos));
            }
        }

        hideOverlappingItems();
    }

    /**
     * Hide all overlapping items.
     */
    private void hideOverlappingItems() {

        if (linearLayout.getChildCount() <= MINIMUM_ITEM_COUNT) {
            return;
        }

        // Update the view
        linearLayout.requestLayout();

        final int linearLayoutHeight = linearLayout.getMeasuredHeight() - actionBarHeight;

        if (linearLayoutHeight <= 0) {
            return;
        }

        final int measuredHeight = (int) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, getTextSize(), context.getResources().getDisplayMetrics())) + PADDING_BOTTOM;

        final int difference = (measuredHeight * linearLayout.getChildCount()) - linearLayoutHeight;

        // Only do something if items are overlapping
        if (difference <= 0) {
            return;
        }

        final int toBeHiddenItems = (difference / measuredHeight) + 2;
        final int numberOfItems = linearLayout.getChildCount() - 2; // Remove first and last icon from the list

        final int hideEveryItem = numberOfItems / toBeHiddenItems;

        for (int i = hideEveryItem; i < linearLayout.getChildCount() - 1; i = i + hideEveryItem) {
            linearLayout.getChildAt(i).setVisibility(View.GONE);
        }
    }

    /**
     *
     * @return the text size the user wants
     */
    private float getTextSize() {
        return context.getResources().getConfiguration().fontScale * TEXT_SIZE;
    }
}
