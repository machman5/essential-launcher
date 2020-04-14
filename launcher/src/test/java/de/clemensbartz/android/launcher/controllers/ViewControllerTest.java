/*
 * Copyright (C) 2020  Clemens Bartz
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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ViewController}.
 * @author Clemens Bartz
 * @since 2.3
 */
public class ViewControllerTest {

    /**
     * Check that each gesture has a unique id.
     */
    @Test
    public void test001() {
        final List<Integer> ids = new ArrayList<>(ViewController.Gestures.values().length);

        for (ViewController.Gestures gesture : ViewController.Gestures.values()) {
            assertFalse("ID already exists", ids.contains(gesture.getId()));

            ids.add(gesture.getId());
        }
    }

    /**
     * Check that each gesture does not have null values in any of its fields.
     */
    @Test
    public void test002() {
        for (ViewController.Gestures gesture : ViewController.Gestures.values()) {
            assertNotNull("ID cannot be null", gesture.getId());
            assertNotNull("Default value cannot be null", gesture.getDefaultValue());
            assertNotNull("Key cannot be null", gesture.getKey());
        }
    }

    /**
     * Check that the gesture is using a valid id by default.
     */
    @Test
    public void test003() {
        for (ViewController.Gestures gesture : ViewController.Gestures.values()) {
            boolean success = false;

            for (int id : ViewController.VALID_IDS) {
                if (id == gesture.getId()) {
                    success = true;
                    break;
                }
            }

            assertTrue("Did not find anything for id " + gesture.getId(), success);
        }
    }

}