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
package com.genericworkflownodes.knime.toolfinderservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author aiche
 */
public class ExternalToolTest {

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.toolfinderservice.ExternalTool#hashCode()}
     * .
     */
    @Test
    public void testHashCode() {
        ExternalTool tool = new ExternalTool("test.plugin", "testtool",
                "some_executable");
        assertTrue(tool.hashCode() == tool.hashCode());
        ExternalTool tool2 = new ExternalTool("test.plugin", "testtool2",
                "some_executable");
        assertFalse(tool.hashCode() == tool2.hashCode());
        ExternalTool tool3 = new ExternalTool("test.plugin", "testtool",
                "some_executable2");
        assertFalse(tool.hashCode() == tool3.hashCode());
        ExternalTool tool4 = new ExternalTool("test.plugin2", "testtool",
                "some_executable");
        assertFalse(tool.hashCode() == tool4.hashCode());
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.toolfinderservice.ExternalTool#ExternalTool(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testExternalTool() {
        ExternalTool tool = new ExternalTool("test.plugin", "testtool",
                "some_executable");
        assertEquals("test.plugin", tool.getPluginName());
        assertEquals("testtool", tool.getToolName());
        assertEquals("some_executable", tool.getExecutableName());
        assertEquals("test.plugin_testtool", tool.getKey());
        assertEquals("test.plugin_testtool", tool.toString());
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.toolfinderservice.ExternalTool#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
        ExternalTool tool = new ExternalTool("test.plugin", "testtool",
                "some_executable");
        // check this == obj
        assertTrue(tool.equals(tool));

        ExternalTool eTool = new ExternalTool("test.plugin", "testtool",
                "some_executable");
        // check equality
        assertTrue(tool.equals(eTool));

        // check error handling
        assertFalse(tool.equals(null));
        assertFalse(tool.equals(""));

        // check in-equality
        ExternalTool tool2 = new ExternalTool("test.plugin", "testtool2",
                "some_executable");
        assertFalse(tool.equals(tool2));
        ExternalTool tool3 = new ExternalTool("test.plugin", "testtool",
                "some_executable2");
        assertFalse(tool.equals(tool3));
        ExternalTool tool4 = new ExternalTool("test.plugin2", "testtool",
                "some_executable");
        assertFalse(tool.equals(tool4));
    }

}
