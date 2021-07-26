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
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * Test for {@link Architecture}.
 * 
 * @author aiche
 */
public class ArchitectureTest {

    @Test
    public void testGetArchitecture() {
        // try to interpret system property before calling getArchitecture
        String dataModel = System.getProperty("sun.arch.data.model");
        if ("64".equals(dataModel)) {
            assertEquals(Architecture.X86_64, Architecture.getArchitecture());
        } else {
            assertEquals(Architecture.X86, Architecture.getArchitecture());
        }
        assertNotSame(Architecture.UNKNOWN, Architecture.getArchitecture());
    }

    @Test
    public void testFromString() {
        assertEquals(Architecture.X86_64, Architecture.fromString("64"));
        assertEquals(Architecture.X86, Architecture.fromString("32"));
        assertEquals(Architecture.UNKNOWN, Architecture.fromString("not-valid"));
    }

    @Test
    public void testToString() {
        assertEquals("64", Architecture.X86_64.toString());
        assertEquals("32", Architecture.X86.toString());
        assertEquals("", Architecture.UNKNOWN.toString());
    }

    @Test
    public void testToOsgiArch() {
        assertEquals("x86_64", Architecture.X86_64.toOsgiArch());
        assertEquals("x86", Architecture.X86.toOsgiArch());
        assertEquals("", Architecture.UNKNOWN.toOsgiArch());
    }

}
