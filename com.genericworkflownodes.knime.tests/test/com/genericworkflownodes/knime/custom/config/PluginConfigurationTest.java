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
package com.genericworkflownodes.knime.custom.config;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.genericworkflownodes.knime.custom.config.impl.PluginConfiguration;

/**
 * Test for {@link PluginConfiguration}.
 * 
 * @author aiche
 */
public class PluginConfigurationTest {

    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: "
                    + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: "
                    + temp.getAbsolutePath());
        }

        return (temp);
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.custom.config.PluginConfiguration#PluginConfiguration(java.lang.String, java.lang.String, java.lang.String, java.util.Properties)}
     * .
     */
    @Test
    public void testPluginConfiguration() {
        Properties pluginProperties = new Properties();
        pluginProperties.setProperty("test.property", "value");
        PluginConfiguration pc = new PluginConfiguration("plugin.id",
                "plugin.name", pluginProperties, getClass());
        assertEquals("plugin.id", pc.getPluginId());
        assertEquals("plugin.name", pc.getPluginName());
        assertEquals("value",
                pc.getPluginProperties().getProperty("test.property"));
    }

}
