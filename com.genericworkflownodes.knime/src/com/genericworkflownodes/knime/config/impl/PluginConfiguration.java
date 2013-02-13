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
package com.genericworkflownodes.knime.config.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.genericworkflownodes.knime.config.IPluginConfiguration;

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
	private final String pluginId;

	/**
	 * The path where all binaries are stored (as string).
	 */
	private final String binariesPath;

	/**
	 * The name of the plugin for the GUI.
	 */
	private final String pluginName;

	/**
	 * Additional properties of the plugin.
	 */
	private Properties props;

	/**
	 * A {@link Map} containing entries for environment variables, needed to
	 * execute the binaries located in the plugin.
	 */
	private Map<String, String> env;

	/**
	 * C'tor for {@link PluginConfiguration}.
	 * 
	 * @param pluginId
	 *            The name of the plugin.
	 * @param binariesPath
	 *            The path where all the binaries are located.
	 * @param props
	 *            Additional properties.
	 * @param env
	 *            A {@link Map} containing entries for environment variables,
	 *            needed to execute the binaries located in the plugin.
	 */
	public PluginConfiguration(final String pluginId, final String pluginName,
			final String binariesPath, final Properties props) {

		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.binariesPath = binariesPath;
		this.props = props;
		env = new HashMap<String, String>();
	}

	/**
	 * This methods expands place holders inside the environment variables with
	 * the correct values.
	 */
	private void fixEnvironmentVariables() {
		for (String envName : env.keySet()) {
			if (env.get(envName).contains("$ROOT")) {
				// update the map entry with the correct path
				env.put(envName,
						env.get(envName).replace("$ROOT",
								new File(getBinariesPath()).getAbsolutePath()));

			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginId() {
		return pluginId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getBinariesPath() {
		return binariesPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Properties getPluginProperties() {
		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Map<String, String> getEnvironmentVariables() {
		return env;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEnvironmentVariables(final Map<String, String> env) {
		this.env.clear();
		this.env.putAll(env);
		fixEnvironmentVariables();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPluginName() {
		return pluginName;
	}

}
