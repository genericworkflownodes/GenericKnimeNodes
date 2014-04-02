/**
 * Copyright (c) 2014, Stephan Aiche.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.os;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.genericworkflownodes.knime.os.OperatingSystem;

/**
 * Test for {@link OperatingSystem}.
 * 
 * @author aiche
 */
public class OperatingSystemTest {

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.os.OperatingSystem#getOS()}.
     */
    @Test
    public void testGetOS() {
        String os = System.getProperty("os.name");

        if (os.toLowerCase().contains("nux")
                || os.toLowerCase().contains("nix")) {
            assertEquals(OperatingSystem.UNIX, OperatingSystem.getOS());
        } else if (os.toLowerCase().contains("mac")) {
            assertEquals(OperatingSystem.MAC, OperatingSystem.getOS());
        } else {
            assertEquals(OperatingSystem.WIN, OperatingSystem.getOS());
        }
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.os.OperatingSystem#fromString(java.lang.String)}
     * .
     */
    @Test
    public void testFromString() {
        assertEquals(OperatingSystem.WIN, OperatingSystem.fromString("win"));
        assertEquals(OperatingSystem.MAC, OperatingSystem.fromString("mac"));
        assertEquals(OperatingSystem.UNIX, OperatingSystem.fromString("lnx"));
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.os.OperatingSystem#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("win", OperatingSystem.WIN.toString());
        assertEquals("mac", OperatingSystem.MAC.toString());
        assertEquals("lnx", OperatingSystem.UNIX.toString());
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.os.OperatingSystem#toOsgiOs()}.
     */
    @Test
    public void testToOsgiOs() {
        assertEquals("win32", OperatingSystem.WIN.toOsgiOs());
        assertEquals("macosx", OperatingSystem.MAC.toOsgiOs());
        assertEquals("linux", OperatingSystem.UNIX.toOsgiOs());
    }

}
