/**
 * 
 */
package com.genericworkflownodes.knime.config;

import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of {@link IPluginConfiguration}
 * 
 * @author aiche
 * 
 */
public class PluginConfiguration implements IPluginConfiguration {

	private String pluginName;
	private String binariesPath;
	private Properties props;
	private Map<String, String> env;

	/**
	 * 
	 * @param pluginName
	 * @param binPath
	 * @param props
	 * @param env
	 */
	public PluginConfiguration(String pluginName, String binPath,
			Properties props, Map<String, String> env) {

		this.pluginName = pluginName;
		this.binariesPath = binPath;
		this.props = props;
		this.env = env;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPluginName() {
		return pluginName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBinariesPath() {
		return binariesPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Properties getPluginProperties() {
		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getEnvironmentVariables() {
		return env;
	}

}
