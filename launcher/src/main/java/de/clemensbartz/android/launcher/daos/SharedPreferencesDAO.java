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

package de.clemensbartz.android.launcher.daos;

import android.content.SharedPreferences;

/**
 * DAO for accessing shared preferences. This class is designed as a SharedPreference-aware
 * Singleton.
 * @since 2.0
 * @author Clemens Bartz
 */
public final class SharedPreferencesDAO {

    /** The version code for shared preferences. Only positive numbers are allowed. */
    private static final int VERSION = 1;
    /** Key for the version. */
    private static final String KEY_VERSION = "version";

    /** The instance of this class. */
    private static SharedPreferencesDAO instance;

    /** Preferences value. */
    private final SharedPreferences preferences;

    /**
     * Get the DAO for the shared preferences.
     * @param preferences the shared preferences
     * @return the DAO
     */
    public static SharedPreferencesDAO getInstance(final SharedPreferences preferences) {
        if (instance != null && instance.getPreferences() == preferences) {
            return instance;
        }

        instance = new SharedPreferencesDAO(preferences);
        return instance;
    }

    /**
     * Create a new DAO.
     * @param preferences the preferences to encapsulate
     */
    private SharedPreferencesDAO(final SharedPreferences preferences) {
        this.preferences = preferences;
    }

    /**
     *
     * @return the current shared preferences object
     */
    private SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * Load preference values.
     */
    public void loadValues() {
        // Delete old values
        final int currentVersion = preferences.getInt(KEY_VERSION, 0);
        if (currentVersion != VERSION) {
            preferences.edit().clear().apply();
            preferences.edit().putInt(KEY_VERSION, VERSION).apply();
        }
    }

    /**
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the value for key, or <code>defaultValue</code>, if no value exists
     */
    public int getInt(final String key, final int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Set the new value for key.
     * @param key the key
     * @param value the new value
     */
    public void putInt(final String key, final int value) {
        preferences.edit().putInt(key, value).apply();
    }

    /**
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the value for key, or <code>defaultValue</code>, if no value exists
     */
    public String getString(final String key, final String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * Set the new value for key.
     * @param key the key
     * @param value the new value
     */
    public void putString(final String key, final String value) {
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Check if a key exists.
     * @param key the key
     * @return <code>true</code>, if it exists, otherwise <code>false</code>
     */
    public boolean contains(final String key) {
        return preferences.contains(key);
    }

    /**
     * Remove target key from the preferences.
     * @param key the key
     */
    public void remove(final String key) {
        preferences.edit().remove(key).apply();
    }

}
