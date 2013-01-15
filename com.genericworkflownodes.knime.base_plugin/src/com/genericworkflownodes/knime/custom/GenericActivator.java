/**
 * Copyright (c) 2012, Björn Kahlert, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.mime.IMIMEtypeRegistry;
import com.genericworkflownodes.knime.payload.IPayloadDirectory;
import com.genericworkflownodes.knime.payload.OSGIBundlePayloadDirectory;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;
import com.genericworkflownodes.util.ZipUtils;

/**
 * This class is an abstract bundle activator which holds the code necessary to
 * register a generated plugin.
 * 
 * @author aiche
 */
public abstract class GenericActivator extends AbstractUIPlugin {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(GenericActivator.class.getCanonicalName());

	/**
	 * Plugin properties.
	 */
	private Properties props = new Properties();

	/**
	 * Plugin specific environment variables.
	 */
	private Map<String, String> environmentVariables = new HashMap<String, String>();

	/**
	 * An abstraction of the payload directory.
	 */
	private IPayloadDirectory payloadDirectory;

	/**
	 * The bundle context for access inside the activator.
	 */
	private BundleContext bundleContext;

	/**
	 * Default c'tor.
	 */
	public GenericActivator() {
		super();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		bundleContext = null;
	}

	/**
	 * This method carries out all tasks needed to initialize a plugin.
	 * 
	 * <ul>
	 * <li>registerNodes contained in the plugin</li>
	 * <li>extract contained binaries</li>
	 * <li>register extracted binaries in the run time</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             In case of io errors.
	 */
	public final void initializePlugin() throws IOException {
		registerNodes();

		// initialize the payload directory
		payloadDirectory = new OSGIBundlePayloadDirectory(bundleContext);

		final IPreferenceStore pStore = getPreferenceStore();
		pStore.setValue("binaries_path", payloadDirectory.getPath()
				.getCanonicalPath());

		loadPluginProperties();

		// We extract the payload only if we can find nothing inside the
		// referenced directory. If the directory is not empty we assume that
		// the payload was already extracted and registered in a previous run.
		if (payloadDirectory.isEmpty()) {
			extractBinaries();
			makeExtractedBinariesExecutable();
			registerExtractedBinaries();
		}
		registerMimeTypes();
	}

	/**
	 * Tries to extract platform specific binaries from the plugin.jar.
	 * 
	 * @throws IOException
	 *             In case of IO errors.
	 */
	private void extractBinaries() throws IOException {
		tryExtractPayloadZIP(payloadDirectory.getPath());
	}

	/**
	 * Loads the plugin.properties file from the plugin.jar.
	 * 
	 * @throws IOException
	 *             In case of IO errors.
	 */
	private void loadPluginProperties() throws IOException {
		props.load(this.getClass().getResourceAsStream("plugin.properties"));
		if (GenericNodesPlugin.isDebug()) {
			GenericNodesPlugin
					.log("com.genericworkflownodes.sample.copyfasta plugin properties are ... ");
			for (Object key : props.keySet()) {
				GenericNodesPlugin.log(key + " -> " + props.get(key));
			}
		}
	}

	/**
	 * Tries to make all binaries contained in the extracted payload executable.
	 */
	private void makeExtractedBinariesExecutable() {
		if (payloadDirectory.getExecutableDirectory() != null) {
			for (File execFile : payloadDirectory.getExecutableDirectory()
					.listFiles()) {
				execFile.setExecutable(true);
			}
		}
	}

	/**
	 * Tests if a zip file with the name binaries.zip is available and extracts
	 * it.
	 * 
	 * @param nodeBinariesDir
	 *            Target directory where it should be extracted to
	 * @return true if the specified zip file was found and extracted correctly.
	 * @throws IOException
	 *             Exception is thrown in case of io problems.
	 */
	private boolean tryExtractPayloadZIP(final File nodeBinariesDir)
			throws IOException {
		// check if a zip file for that combination of OS and data model exists
		if (getBinaryLocation().getResourceAsStream(getZipFileName()) != null) {
			// extract it
			ZipUtils.decompressTo(nodeBinariesDir, getBinaryLocation()
					.getResourceAsStream(getZipFileName()));

			// load the associated properties and store them as environment
			// variable
			loadEnvironmentVariables(nodeBinariesDir);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tries to load data from the platform specific ini file, contained in the
	 * plugin.jar.
	 * 
	 * @param os
	 *            The operating system.
	 * @param dataModel
	 *            The data model (32 or 64 bit).
	 * @throws IOException
	 *             I thrown in case of IO errors.
	 */
	private void loadEnvironmentVariables(final File targetDirectory)
			throws IOException {
		Properties envProperites = new Properties();

		File iniFile = new File(targetDirectory, getINIFileName());

		// check if we extracted also an ini file
		if (!iniFile.exists()) {
			throw new IOException("No ini found at location: "
					+ iniFile.getAbsolutePath());
		}

		// load the properties file
		envProperites.load(new FileInputStream(iniFile));

		for (Object key : envProperites.keySet()) {
			String k = key.toString();
			String v = envProperites.getProperty(k);
			environmentVariables.put(k, v);
		}
	}

	/**
	 * Returns a correctly formated ini file name for the given combination of
	 * os and dataModel.
	 * 
	 * @param os
	 *            The operating system.
	 * @param dataModel
	 *            The data model (32 or 64 bit).
	 * @return Returns a string like binaries_win_32.ini.
	 */
	private String getINIFileName() {
		return "binaries.ini";
	}

	/**
	 * Returns the zip file name in the binres package.
	 * 
	 * @return Returns a string like binaries.zip.
	 */
	private String getZipFileName() {
		return "binaries.zip";
	}

	/**
	 * Reads the list of {@link MIMEType}s associated with the plugin and
	 * registers them in the central {@link IMIMEtypeRegistry}.
	 */
	private void registerMimeTypes() {
		IMIMEtypeRegistry registry = (IMIMEtypeRegistry) PlatformUI
				.getWorkbench().getService(IMIMEtypeRegistry.class);
		if (registry != null) {
			for (String mimeType : getMIMETypes()) {
				registry.registerMIMEtype(mimeType);
			}
		}
	}

	/**
	 * Registers all nodes included in the plugin as external tools in the
	 * PluginPreferenceToolLocator.
	 * 
	 * @see com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator
	 */
	private void registerNodes() {

		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {
			for (ExternalTool tool : getTools()) {
				toolLocator.registerTool(tool);
			}
		}
	}

	/**
	 * Adds all extracted binaries to the tool registry.
	 */
	private void registerExtractedBinaries() {

		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {

			// get binary path
			File binaryDirectory = payloadDirectory.getExecutableDirectory();

			// for each node find the executable
			for (ExternalTool tool : getTools()) {
				File executable = getExecutableName(binaryDirectory,
						tool.getExecutableName());
				if (executable != null) {
					// register executalbe in the ToolFinder
					toolLocator.setToolPath(tool, executable,
							ToolPathType.SHIPPED);

					try {
						// check if we need to adjust the type
						if (toolLocator.getConfiguredToolPathType(tool) == ToolPathType.UNKNOWN) {
							toolLocator.updateToolPathType(tool,
									ToolPathType.SHIPPED);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					// TODO: handle non existent binaries, check if we have a
					// configured one, otherwise warn
					LOGGER.warning("Did not find any binaries for your platform.");
				}
			}
		}

	}

	/**
	 * Helper function to find an executable based on a node name and the
	 * directory where the binaries are located.
	 * 
	 * @param binDir
	 *            Directory containing all binaries associated to the plugin.
	 * @param nodename
	 *            The name of the node for which the executable should be found.
	 * @return A {@link File} pointing to the executable (if one was found) or
	 *         null (if no executable was found).
	 */
	private File getExecutableName(final File binDir, final String nodename) {
		for (String extension : new String[] { "", ".bin", ".exe" }) {
			File binFile = new File(binDir, nodename + extension);
			if (binFile.canExecute()) {
				return binFile;
			}
		}
		return null;
	}

	/**
	 * Get the plugin specific proberties stored in the plugin.properties file.
	 * 
	 * @return The properties loaded for this plugin.
	 */
	public final Properties getProperties() {
		return props;
	}

	/**
	 * Get the environment variable customizations stored in the payload config
	 * file (e.g., binaries_mac_64.ini).
	 * 
	 * @return A {@link Map} containing environment variables and there
	 *         respective value as set in the binaries_...ini file.
	 */
	public final Map<String, String> getEnvironment() {
		return environmentVariables;
	}

	/**
	 * 
	 * @return
	 */
	public abstract List<ExternalTool> getTools();

	/**
	 * Returns the list of {@link MIMEType}s provided by the plugin.
	 * 
	 * @return Returns the list of {@link MIMEType}s provided by the plugin.
	 */
	public abstract List<String> getMIMETypes();

	/**
	 * Returns the {@link Class} where the binaries are located.
	 * 
	 * @return Returns the {@link Class} where the binaries are located.
	 */
	public abstract Class<?> getBinaryLocation();

}
