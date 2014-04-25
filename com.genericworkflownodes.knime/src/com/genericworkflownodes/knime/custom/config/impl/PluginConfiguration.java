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

import java.util.Properties;

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
     * C'tor for {@link PluginConfiguration}.
     * 
     * @param pluginId
     *            The id of the plugin.
     * @param pluginName
     *            Then name of the plugin.
     * @param props
     *            Additional properties.
     */
    public PluginConfiguration(final String pluginId, final String pluginName,
            final Properties props) {
        m_pluginId = pluginId;
        m_pluginName = pluginName;
        m_props = props;
        m_binaryManager = new BinaryManager(getClass());
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

}
