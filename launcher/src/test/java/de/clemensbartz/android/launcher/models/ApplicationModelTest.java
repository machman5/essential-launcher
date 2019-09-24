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

package de.clemensbartz.android.launcher.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for {@link ApplicationModel}.
 * @author Clemens Bartz
 * @since 2.2
 */
public class ApplicationModelTest {

    /**
     * Test to check if the assignment of properties works properly.
     */
    @Test
    public void test1() {
        final ApplicationModel applicationModel = new ApplicationModel();

        // hidden property
        applicationModel.hidden = false;
        assertFalse("Hidden is not false", applicationModel.hidden);

        // label property
        final String testLabel = "testLabel";
        applicationModel.label = testLabel;
        assertEquals("Label does not match", testLabel, applicationModel.label);

        // className property
        final String testClassName = "testClassName";
        applicationModel.className = testClassName;
        assertEquals("Class name does not match", testClassName, applicationModel.className);

        // packageName property
        final String testPackageName = "testPackageName";
        applicationModel.packageName = testPackageName;
        assertEquals("Package name does not match", testPackageName, applicationModel.packageName);
    }
}