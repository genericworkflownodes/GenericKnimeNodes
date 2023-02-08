/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom.config.impl;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import com.genericworkflownodes.knime.custom.config.BinaryManager;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;

/**
 * Default implementation of {@link IPluginConfiguration}.
 *
 * @author aiche
 *
 */
public class PluginConfiguration implements IPluginConfiguration {

    /**
     * The name of the plugin.
     */
    private final String m_pluginId;

    /**
     * The name of the plugin for the GUI.
     */
    private final String m_pluginName;

    /**
     * Additional properties of the plugin.
     */
    private Properties m_props;

    /**
     * The binary manager for the plugin.
     */
    private final BinaryManager m_binaryManager;

    /**
     * The tool properties
     */
    private final Properties m_toolProps;

    /**
     * The tool specific properties
     */
    private final Map<String, Properties> m_specificToolProps;

   /**
    * The docker machine if specified, else 'default'
    */
    private final String m_dockerMachine;

    /**
     * The version
     */
     private final String m_version;

    /**
     * The raw version
     */
    private final Version m_raw_version;

    /**
     * The version display layer.
     */
    private VersionDisplayLayer m_version_display = VersionDisplayLayer.NONE;

    /**
     * C'tor for {@link PluginConfiguration}.
     *
     * @param pluginId
     *            The id of the plugin.
     * @param pluginName
     *            Then name of the plugin.
     * @param props
     *            Additional properties.
     * @param classFromPlugin
     *            A class from the specific plugin, needed to proper initialize
     *            BinaryManager.
     */
    @SuppressWarnings("rawtypes")
    public PluginConfiguration(final String pluginId, final String pluginName,
            final Properties props, Class classFromPlugin) {
        m_pluginId = pluginId;
        m_pluginName = pluginName;
        m_props = props;
        Bundle bundle = FrameworkUtil.getBundle(classFromPlugin);
        m_raw_version = bundle.getVersion();
        m_version = m_raw_version.toString();
        m_binaryManager = new BinaryManager(classFromPlugin);
        Properties p = new Properties();
        Map<String,Properties> toolMap = new Hashtable<String,Properties>();
        for (String key: m_props.stringPropertyNames()) {
            if (key.startsWith("tool.")) {
                String value = m_props.getProperty(key);
                p.put(key, value);
                String[] keyElements = key.split("\\.");
                if (keyElements.length > 2) {
                    String tool_key = "";
                    for (int i=2; i<keyElements.length; i++) {
                        tool_key+=keyElements[i];
                    }
                    if (toolMap.containsKey(keyElements[1])) {
                        toolMap.get(keyElements[1]).put(tool_key, value);
                    } else {
                        Properties p_tool = new Properties();
                        p_tool.put(tool_key, value);
                        toolMap.put(keyElements[1], p_tool);
                    }
                }
            }
        }
        m_toolProps = p;
        m_specificToolProps = toolMap;
        m_dockerMachine = props.getProperty("dockerMachine","default");

        // Set the version display from the plugin.properties
        try {
            String fromString = props.getProperty("versionDisplayLayer");
            if (fromString != null && !fromString.isEmpty()) {
                m_version_display = VersionDisplayLayer
                        .valueOf(fromString.toUpperCase().trim());
            }
        } catch (IllegalArgumentException e) {
            m_version_display = VersionDisplayLayer.NONE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getPluginVersion() {
        return m_version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Version getRawPluginVersion() {
        return m_raw_version;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getPluginId() {
        return m_pluginId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Properties getPluginProperties() {
        return m_props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName() {
        return m_pluginName;
    }

    @Override
    public BinaryManager getBinaryManager() {
        return m_binaryManager;
    }

    @Override
    public Properties getToolProperties() {
        return m_toolProps;
    }

    @Override
    public Properties getToolProperty(String toolName) {
        return m_specificToolProps.get(toolName);
    }

    @Override
    public String getDockerMachine() {
        return m_dockerMachine;
    }

    @Override
    public VersionDisplayLayer getVersionDisplayLayer() {
        return m_version_display;
    }

}
