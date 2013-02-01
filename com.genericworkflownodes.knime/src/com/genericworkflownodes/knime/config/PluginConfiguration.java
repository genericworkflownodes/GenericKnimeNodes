/**
 * 
 */
package com.genericworkflownodes.knime.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

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
	private String pluginName;

	/**
	 * The path where all binaries are stored (as string).
	 */
	private String binariesPath;

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
	 * @param pluginName
	 *            The name of the plugin.
	 * @param binPath
	 *            The path where all the binaries are located.
	 * @param props
	 *            Additional properties.
	 * @param env
	 *            A {@link Map} containing entries for environment variables,
	 *            needed to execute the binaries located in the plugin.
	 */
	public PluginConfiguration(final String pluginName, final String binPath,
			final Properties props, final Map<String, String> env) {

		this.pluginName = pluginName;
		this.binariesPath = binPath;
		this.props = props;
		this.env = env;

		// fix environment variables
		fixEnvironmentVariables();
	}

	/**
	 * This methods expands place holders inside the environment variables with
	 * the correct values.
	 */
	private void fixEnvironmentVariables() {
		for (String envName : this.env.keySet()) {
			if (env.get(envName).contains("$ROOT")) {
				// update the map entry with the correct path
				env.put(envName,
						env.get(envName).replace("$ROOT",
								getBinariesPath() + File.separator));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return pluginName;
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

}
