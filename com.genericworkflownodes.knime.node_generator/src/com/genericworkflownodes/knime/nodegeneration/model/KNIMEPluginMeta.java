package com.genericworkflownodes.knime.nodegeneration.model;

import java.security.InvalidParameterException;
import java.util.Properties;

public class KNIMEPluginMeta {

	/**
	 * Returns the plugin name.
	 * <p>
	 * If no configuration could be found, the name is created based on the
	 * given package name. e.g. org.roettig.foo will result in foo
	 * 
	 * @param packageName
	 * @return
	 */
	private static String getPluginName(Properties props, String packageName) {
		String pluginname = props.getProperty("pluginName");
		if (pluginname != null && !pluginname.isEmpty()) {
			return pluginname;
		}

		int idx = packageName.lastIndexOf(".");
		if (idx == -1) {
			return packageName;
		}
		return packageName.substring(idx + 1);
	}

	/**
	 * Checks if the plugin name is valid.
	 * 
	 * @param obj
	 * @param id
	 */
	private static boolean isPluginNameValid(final String pluginName) {
		return pluginName != null && pluginName.matches("^\\w+$");
	}

	/**
	 * Returns the plugin version.
	 * 
	 * @param packageName
	 * @return
	 */
	private static String getPluginVersion(final Properties props) {
		return props.getProperty("pluginVersion");
	}

	/**
	 * Checks whether a given package version is valid.
	 * 
	 * @param pluginVersion
	 * @return
	 */
	private static boolean isPluginVersionValid(final String pluginVersion) {
		// TODO
		return true;
	}

	/**
	 * Returns the package name the generated plugin uses. (e.g.
	 * org.roettig.foo).
	 * 
	 * @param props
	 * @return
	 */
	private static String getPackageRoot(final Properties props) {
		return props.getProperty("pluginPackage");
	}

	/**
	 * Checks whether a given package name is valid.
	 * 
	 * @param packageName
	 * @param id
	 * @return true if package name is valid; false otherwise
	 */
	public static boolean isValidPackageRoot(final String packageName) {
		return packageName != null
				&& packageName
						.matches("^([A-Za-z_]{1}[A-Za-z0-9_]*(\\.[A-Za-z_]{1}[A-Za-z0-9_]*)*)$");
	}

	/**
	 * Returns the package name the generated plugin uses. (e.g.
	 * org.roettig.foo).
	 * 
	 * @param props
	 * @return
	 */
	private static String getNodeRepositoyPath(final Properties props) {
		return props.getProperty("nodeRepositoyRoot");
	}

	/**
	 * Checks whether a given package name is valid.
	 * 
	 * @param nodeRepositoryPath
	 * @param id
	 * @return true if package name is valid; false otherwise
	 */
	public static boolean isNodeRepositoyPathValid(
			final String nodeRepositoryPath) {
		// TODO
		return true;
	}

	private final String name;
	private final String version;
	private final String packageRoot;
	private final String nodeRepositoyPath;

	public KNIMEPluginMeta(Properties properties) {
		this.packageRoot = getPackageRoot(properties);
		if (this.packageRoot == null || this.packageRoot.isEmpty()) {
			throw new InvalidParameterException("No package name was specified");
		}
		if (!isValidPackageRoot(this.packageRoot)) {
			throw new InvalidParameterException("The given package name \""
					+ this.packageRoot + "\" is invalid");
		}

		this.name = getPluginName(properties, this.packageRoot);
		if (this.packageRoot == null || this.name.isEmpty()) {
			throw new InvalidParameterException("No plugin name was specified");
		}
		if (!isPluginNameValid(this.name)) {
			throw new InvalidParameterException("The plugin name \""
					+ this.name
					+ "\" must only contain alpha numeric characters");
		}

		this.version = getPluginVersion(properties);
		if (this.version == null || this.version.isEmpty()) {
			throw new InvalidParameterException(
					"No plugin version was specified");
		}
		if (!isPluginVersionValid(this.version)) {
			throw new InvalidParameterException("The plugin version \""
					+ this.version + "\" is not valid");
		}

		this.nodeRepositoyPath = getNodeRepositoyPath(properties);
		if (this.nodeRepositoyPath == null || this.nodeRepositoyPath.isEmpty()) {
			throw new InvalidParameterException(
					"No path within the node repository was specified");
		}
		if (!isNodeRepositoyPathValid(this.version)) {
			throw new InvalidParameterException("The node repository path \""
					+ this.nodeRepositoyPath + "\" is not valid");
		}
	}

	/**
	 * Gets the KNIME plugin's name.
	 * <p>
	 * e.g. KNIME Test
	 * 
	 * @return The plugin's name.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns the KNIME plugin's version.
	 * <p>
	 * e.g. 0.1.1
	 * 
	 * @return The plugin's version.
	 */
	public final String getVersion() {
		return this.version;
	}

	/**
	 * Returns the KNIME plugin's root package name
	 * <p>
	 * e.g. de.fu_berlin.imp.seqan
	 * 
	 * @return The plugin's package root.
	 */
	public final String getPackageRoot() {
		return this.packageRoot;
	}

	/**
	 * Returns the path within KNIME node repository.
	 * <p>
	 * e.g. community/foo/bar
	 * 
	 * @return The path where the tool should be registered in the tool
	 *         registry.
	 */
	public final String getNodeRepositoryRoot() {
		return this.nodeRepositoyPath;
	}
}
