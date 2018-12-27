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

package de.clemensbartz.android.launcher.controllers;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.ViewFlipper;

import de.clemensbartz.android.launcher.Launcher;
import de.clemensbartz.android.launcher.R;
import de.clemensbartz.android.launcher.daos.SharedPreferencesDAO;
import de.clemensbartz.android.launcher.tasks.CreateWidgetAsyncTask;
import de.clemensbartz.android.launcher.tasks.ShowWidgetListAsPopupMenuTask;
import de.clemensbartz.android.launcher.util.BundleUtil;
import de.clemensbartz.android.launcher.util.IntentUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Controlling for handling widgets in a FrameLayout.
 * <br/>
 * This class is intended only for the lifetime of an activity.
 * @author Clemens Bartz
 * @since 2.0
 */
public final class WidgetController {

    /** Key for the appWidgetId property. */
    public static final String KEY_APPWIDGET_ID = "appWidgetId";
    /** Key for the appWidgetLayout property. */
    public static final String KEY_APPWIDGET_LAYOUT = "appWidgetLayout";

    /** Extra code for APP_WIDGET_CONFIGURE. */
    private static final String EXTRA_APP_WIDGET_CONFIGURE = "EL_APP_WIDGET_CONFIGURE";
    /** Request code for binding widget. */
    private static final int REQUEST_BIND_APPWIDGET = 0;
    /** Request code for creating a widget. */
    private static final int REQUEST_CREATE_APPWIDGET = 1;
    /** A quarter. */
    private static final double QUARTER = 0.25;
    /** A half. */
    private static final double HALF = 0.5;
    /** Three quarter. */
    private static final double THREE_QUARTER = 0.75;
    /** Two third. */
    private static final double TWO_THIRD = 0.66;
    /** Default height in dp of the dock. */
    private static final int DOCK_HEIGHT = 82;
    // Layout constants for widget
    /** Layout constant for default full screen layout. */
    private static final int WIDGET_LAYOUT_FULL_SCREEN = -1;
    /** Layout constant for top half widget. */
    private static final int WIDGET_LAYOUT_TOP_QUARTER = 0;
    /** Layout constant for top third widget. */
    private static final int WIDGET_LAYOUT_TOP_THIRD = 5;
    /** Layout constant for top half widget. */
    private static final int WIDGET_LAYOUT_TOP_HALF = 10;
    /** Layout constant for center widget reduced. */
    private static final int WIDGET_LAYOUT_CENTER = 15;
    /** Layout constant for bottom half widget. */
    private static final int WIDGET_LAYOUT_BOTTOM_HALF = 20;
    /** Layout constant for bottom third widget. */
    private static final int WIDGET_LAYOUT_BOTTOM_THIRD = 25;
    /** Layout constant for bottom quarter widget. */
    private static final int WIDGET_LAYOUT_BOTTOM_QUARTER = 30;

    /** Default value for appWidgetId. */
    public static final int DEFAULT_APPWIDGET_ID = -1;
    /** Default value for appWidgetLayout. */
    public static final int DEFAULT_APPWIDGET_LAYOUT = WIDGET_LAYOUT_FULL_SCREEN;

    /** The shared preference dao. */
    private final SharedPreferencesDAO sharedPreferencesDAO;
    /** The launcher. */
    private final Launcher launcher;
    /** The top filler. */
    private final View vTopFiller;
    /** The bottom filler. */
    private final View vBottomFiller;
    /** The frame layout containing the widget. */
    private final FrameLayout flWidget;
    /** The view flipper. */
    private final ViewFlipper vsLauncher;
    /** The app widget manager. */
    private final AppWidgetManager appWidgetManager;
    /** The app widget host. */
    private final AppWidgetHost appWidgetHost;


    /** The temporary configure component for widgets. */
    private ComponentName widgetConfigure = null;

    /**
     * Create a new widget controller.
     * @param launcher the launcher to be created in
     * @param sharedPreferencesDAO the shared preference dao
     */
    public WidgetController(final Launcher launcher, final SharedPreferencesDAO sharedPreferencesDAO) {
        this.sharedPreferencesDAO = sharedPreferencesDAO;
        this.launcher = launcher;
        this.vTopFiller = launcher.findViewById(R.id.topFiller);
        this.vBottomFiller = launcher.findViewById(R.id.bottomFiller);
        this.flWidget = launcher.findViewById(R.id.flWidget);
        this.appWidgetManager = AppWidgetManager.getInstance(launcher);
        this.appWidgetHost = new AppWidgetHost(launcher, R.id.flWidget);
        this.vsLauncher = launcher.findViewById(R.id.vsLauncher);
    }

    /**
     * Start listening for widget changes.
     */
    public void startListening() {
        appWidgetHost.startListening();
    }

    /**
     * Stop listening for widget changes.
     */
    public void stopListening() {
        appWidgetHost.stopListening();
    }

    /**
     *
     * @return if an app widget is configured
     */
    public boolean isAppWidgetConfigured() {
        return sharedPreferencesDAO.getInt(KEY_APPWIDGET_ID, DEFAULT_APPWIDGET_ID) > -1;
    }

    /**
     * Handle activity results for asynchronous requests.
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data
     */
    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {

        if (resultCode == RESULT_OK) {
            final Integer appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

            if (requestCode == WidgetController.REQUEST_CREATE_APPWIDGET) {

                new CreateWidgetAsyncTask(this).execute(appWidgetId);
            } else if (requestCode == WidgetController.REQUEST_BIND_APPWIDGET) {
                if (widgetConfigure != null) {
                    configureWidget(appWidgetId, widgetConfigure);
                } else {
                    new CreateWidgetAsyncTask(this).execute(appWidgetId);
                }
            }
        }

        widgetConfigure = null;
    }

    /**
     * Add a host view to the frame layout for a widget id.
     * @param appWidgetId the widget id
     */
    public void addHostView(final int appWidgetId) {
        flWidget.removeAllViews();

        final AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo != null) {
            final AppWidgetHostView hostView = appWidgetHost.createView(launcher, appWidgetId, appWidgetInfo);
            hostView.setAppWidget(appWidgetId, appWidgetInfo);

            flWidget.addView(hostView);
            flWidget.requestLayout();

            final Bundle options = BundleUtil.getWidgetOptionsBundle(flWidget.getMeasuredWidth(), flWidget.getMeasuredHeight(), flWidget.getMeasuredWidth(), flWidget.getMeasuredHeight());

            hostView.updateAppWidgetOptions(options);

            sharedPreferencesDAO.putInt(KEY_APPWIDGET_ID, appWidgetId);
        } else {
            sharedPreferencesDAO.putInt(KEY_APPWIDGET_ID, DEFAULT_APPWIDGET_ID);
        }
    }

    /**
     * Adjust widget layout according to layout id.
     * @param appWidgetLayout the layout id.
     */
    public void adjustWidget(final int appWidgetLayout) {
        final ViewGroup.LayoutParams bottomLayout = vBottomFiller.getLayoutParams();
        final ViewGroup.LayoutParams topLayout = vTopFiller.getLayoutParams();

        final int screenHeightDp = vsLauncher.getHeight();

        // Check for consistent values
        if (screenHeightDp == Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
            bottomLayout.height = 0;
            topLayout.height = 0;
            return;
        }

        final double height = screenHeightDp - DOCK_HEIGHT;

        // Check for enough space to accommodate another dock
        if (height - DOCK_HEIGHT <= 0) {
            bottomLayout.height = 0;
            topLayout.height = 0;
            return;
        }

        double preciseBottomHeight = 0;
        double preciseTopHeight = 0;

        switch (appWidgetLayout) {
            case WIDGET_LAYOUT_TOP_QUARTER: // widget in top 1/4
                preciseBottomHeight = height * THREE_QUARTER;
                break;
            case WIDGET_LAYOUT_TOP_THIRD: // widget in top 1/3
                preciseBottomHeight = height * TWO_THIRD;
                break;
            case WIDGET_LAYOUT_TOP_HALF: // widget in top 1/2
                preciseBottomHeight = height * HALF;
                break;
            case WIDGET_LAYOUT_CENTER: // widget center with height adjusted
                preciseBottomHeight = height * QUARTER;
                preciseTopHeight = preciseBottomHeight;
                break;
            case WIDGET_LAYOUT_BOTTOM_HALF: // widget in bottom 1/2
                preciseTopHeight = height * HALF;
                break;
            case WIDGET_LAYOUT_BOTTOM_THIRD: // widget in bottom 1/3
                preciseTopHeight = height * TWO_THIRD;
                break;
            case WIDGET_LAYOUT_BOTTOM_QUARTER: // widget in bottom 1/4
                preciseTopHeight = height * THREE_QUARTER;
                break;
            default: // default: -1
                break;
        }

        bottomLayout.height = (int) Math.round(preciseBottomHeight);
        vBottomFiller.requestLayout();

        topLayout.height = (int) Math.round(preciseTopHeight);
        vTopFiller.requestLayout();

        if (flWidget.getChildCount() == 1) {
            final View child = flWidget.getChildAt(0);

            if (child instanceof AppWidgetHostView) {
                final AppWidgetHostView hostView = (AppWidgetHostView) child;

                final Bundle options = BundleUtil.getWidgetOptionsBundle(flWidget.getMeasuredWidth(), flWidget.getMeasuredHeight(), flWidget.getMeasuredWidth(), flWidget.getMeasuredHeight());

                hostView.updateAppWidgetOptions(options);
            }
        }

        sharedPreferencesDAO.putInt(KEY_APPWIDGET_LAYOUT, appWidgetLayout);
    }

    /**
     * Bind a widget for a provider.
     * @param provider the provider component
     * @param configure the configure component
     */
    public void bindWidget(final ComponentName provider, final ComponentName configure) {
        final int appWidgetId = appWidgetHost.allocateAppWidgetId();

        if (appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider)) {
            // Binding is allowed, go straight to configuring
            configureWidget(appWidgetId, configure);
        } else {
            // Ask for permission
            widgetConfigure = configure;

            final Intent intent = IntentUtil.createWidgetBindIntent(provider, appWidgetId);

            final Bundle options = new Bundle();
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN);
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, flWidget.getMinimumWidth());
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, flWidget.getWidth());
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, flWidget.getMinimumHeight());
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, flWidget.getHeight());

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options);

            launcher.startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
        }
    }

    /**
     * Configure a widget for a provider.
     * @param appWidgetId the appWidgetId
     * @param configure the configure component
     */
    private void configureWidget(final Integer appWidgetId, final ComponentName configure) {
        // Abort on invalid input
        if (appWidgetId == -1) {
            return;
        }

        if (configure != null) {
            final Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(EXTRA_APP_WIDGET_CONFIGURE, configure);

            if (IntentUtil.isCallable(launcher.getPackageManager(), intent)) {
                launcher.startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
            }
        } else {
            // Configuring not necessary, go strait to creation
            new CreateWidgetAsyncTask(this).execute(appWidgetId);
        }

        // Reset widget configure
        widgetConfigure = null;
    }

    /**
     * Create a widget from an intent.
     * @param appWidgetId the appWidgetId
     */
    public void createWidget(final Integer appWidgetId) {
        final int currentAppWidgetId = sharedPreferencesDAO.getInt(KEY_APPWIDGET_ID, DEFAULT_APPWIDGET_ID);

        if (currentAppWidgetId > -1) {
            appWidgetHost.deleteAppWidgetId(currentAppWidgetId);
            flWidget.removeAllViews();
            // Reset view
            adjustWidget(WIDGET_LAYOUT_FULL_SCREEN);
        }

        sharedPreferencesDAO.putInt(KEY_APPWIDGET_ID, appWidgetId);

        final MenuItem removeWidgetMenuItem = launcher.getActionBarMenuItem(R.id.abm_remove_widget);
        if (removeWidgetMenuItem != null) {
            removeWidgetMenuItem.setVisible(appWidgetId > -1);
        }

        final MenuItem layoutWidgetMenuItem = launcher.getActionBarMenuItem(R.id.abm_layout_widget);
        if (layoutWidgetMenuItem != null) {
            layoutWidgetMenuItem.setVisible(appWidgetId > -1);
        }

        addHostView(appWidgetId);
    }

    /**
     *
     * @return the top filler view
     */
    public View getTopFiller() {
        return vTopFiller;
    }

    /**
     * Request to choose the widget.
     */
    public void requestWidgetChoosing() {
        new ShowWidgetListAsPopupMenuTask(this, launcher, appWidgetManager).execute();
    }

    /**
     * Request to layout the widget.
     */
    public void requestWidgetLayoutChange() {
        final PopupMenu popupMenu = new PopupMenu(launcher, vTopFiller);

        final int currentLayout = sharedPreferencesDAO.getInt(KEY_APPWIDGET_LAYOUT, DEFAULT_APPWIDGET_LAYOUT);

        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_FULL_SCREEN, currentLayout, R.string.widgetLayoutFull);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_TOP_QUARTER, currentLayout, R.string.widgetLayoutTopQuarter);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_TOP_THIRD, currentLayout, R.string.widgetLayoutTopThird);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_TOP_HALF, currentLayout, R.string.widgetLayoutTopHalf);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_CENTER, currentLayout, R.string.widgetLayoutCenter);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_BOTTOM_HALF, currentLayout, R.string.widgetLayoutBottomHalf);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_BOTTOM_THIRD, currentLayout, R.string.widgetLayoutBottomThird);
        addLayoutPopupMenuItem(popupMenu, WIDGET_LAYOUT_BOTTOM_QUARTER, currentLayout, R.string.widgetLayoutBottomQuarter);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                adjustWidget(item.getItemId());
                return true;
            }
        });

        popupMenu.show();
    }

    /**
     * Request the removal of the widget.
     */
    public void requestWidgetRemoval() {
        new CreateWidgetAsyncTask(this).execute(-1);
    }

    /**
     * Add a menu item for the layout popup menu.
     * @param popupMenu the popup menu
     * @param widgetLayout the layout for the item
     * @param currentLayout the currently active item
     * @param resourceId the resource for the string
     */
    private void addLayoutPopupMenuItem(final PopupMenu popupMenu, final int widgetLayout, final int currentLayout, final int resourceId) {
        final MenuItem menuItem = popupMenu.getMenu().add(0, widgetLayout, 0, resourceId);
        menuItem.setCheckable(true);
        menuItem.setChecked(currentLayout == widgetLayout);
    }
}
