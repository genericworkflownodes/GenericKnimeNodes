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
package com.genericworkflownodes.knime.custom.config;

import java.util.Properties;

/**
 * Provides all plugin specific configuration settings.
 * 
 * @author aiche
 */
public interface IPluginConfiguration {

    /**
     * The id of the plugin.
     * 
     * @return The name of the configured plugin.
     */
    String getPluginId();

    /**
     * General properties of the plugin.
     * 
     * @return A {@link Properties} object containing additional properties of
     *         the plugin.
     */
    Properties getPluginProperties();

    /**
     * The name of the plugin as it would be shown in the GUI.
     * 
     * @return
     */
    String getPluginName();

    /**
     * Gives access to the binary manager, responsible for the current plugin.
     * 
     * @return The binary manager for this plugin.
     */
    BinaryManager getBinaryManager();
}
