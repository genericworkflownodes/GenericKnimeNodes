/**
 * 
 */
package com.genericworkflownodes.knime.config.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.junit.Test;

/**
 * @author aiche
 * 
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
     * {@link com.genericworkflownodes.knime.config.impl.PluginConfiguration#PluginConfiguration(java.lang.String, java.lang.String, java.lang.String, java.util.Properties)}
     * .
     */
    @Test
    public void testPluginConfiguration() {
        Properties pluginProperties = new Properties();
        pluginProperties.setProperty("test.property", "value");
        PluginConfiguration pc = new PluginConfiguration("plugin.id",
                "plugin.name", "/path/to/binaries", pluginProperties);
        assertEquals("plugin.id", pc.getPluginId());
        assertEquals("plugin.name", pc.getPluginName());
        assertEquals("/path/to/binaries", pc.getBinariesPath());
        assertEquals("value",
                pc.getPluginProperties().getProperty("test.property"));
    }

    /**
     * Test method for
     * {@link com.genericworkflownodes.knime.config.impl.PluginConfiguration#updateEnvironmentVariables(java.util.Map)}
     * .
     * 
     * @throws IOException
     */
    @Test
    public void testUpdateEnvironmentVariables() throws IOException {
        Map<String, String> env = new TreeMap<String, String>();
        env.put("ENABLE_SOME_FEATURE", "TRUE");
        env.put("SOME_PATH_INSIDE_THE_PLUGIN", "$ROOT/path");

        File tmpDir = createTempDirectory();

        PluginConfiguration pc = new PluginConfiguration("plugin.id",
                "plugin.name", tmpDir.getAbsolutePath(), new Properties());

        pc.updateEnvironmentVariables(env);
        Map<String, String> fixedEnv = pc.getEnvironmentVariables();
        assertTrue(fixedEnv.containsKey("ENABLE_SOME_FEATURE"));
        assertEquals("TRUE", fixedEnv.get("ENABLE_SOME_FEATURE"));
        assertTrue(fixedEnv.containsKey("SOME_PATH_INSIDE_THE_PLUGIN"));
        assertEquals(String.format("%s/path", tmpDir.getAbsolutePath()),
                fixedEnv.get("SOME_PATH_INSIDE_THE_PLUGIN"));
    }

}
