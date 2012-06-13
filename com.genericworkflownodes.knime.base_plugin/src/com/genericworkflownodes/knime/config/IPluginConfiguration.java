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
package com.genericworkflownodes.knime.config;

import java.util.Map;
import java.util.Properties;

/**
 * Provides all plugin specific configuration settings.
 * 
 * @author aiche
 */
public interface IPluginConfiguration {

	/**
	 * The name of the plugin.
	 * 
	 * @return The name of the configured plugin.
	 */
	String getPluginName();

	/**
	 * The path where all binaries are located.
	 * 
	 * @return The path where all binaries are located.
	 */
	String getBinariesPath();

	/**
	 * General properties of the plugin.
	 * 
	 * @return A {@link Properties} object containing additional properties of
	 *         the plugin.
	 */
	Properties getPluginProperties();

	/**
	 * Environment variables which need to be set, to execute the tools.
	 * 
	 * @return A {@link Map} of environment variables that need to be set, to
	 *         execute the tools contained in the plugin.
	 */
	Map<String, String> getEnvironmentVariables();

}
