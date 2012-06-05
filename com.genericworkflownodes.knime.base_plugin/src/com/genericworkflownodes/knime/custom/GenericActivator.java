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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.ballproject.knime.base.util.ZipUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.data.url.MIMEType;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.payload.IPayloadDirectory;
import com.genericworkflownodes.knime.payload.OSGIBundlePayloadDirectory;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;

public abstract class GenericActivator extends AbstractUIPlugin {

	private static final Logger LOGGER = Logger
			.getLogger(GenericActivator.class.getCanonicalName());

	private static GenericActivator plugin;

	private Properties props = new Properties();
	private Map<String, String> environmentVariables = new HashMap<String, String>();

	private IPayloadDirectory payloadDirectory;
	private BundleContext bundleContext;

	public GenericActivator() {
		super();
		plugin = this;
	}

	public static GenericActivator getDefault() {
		return plugin;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		bundleContext = null;
	}

	/**
	 * This method carries out all tasks needed to initialize a plugin:
	 * 
	 * <ul>
	 * <li>registerNodes contained in the plugin</li>
	 * <li>extract contained binaries</li>
	 * <li>register extracted binaries in the run time</li>
	 * </ul>
	 * 
	 * @throws IOException
	 */
	public void initializePlugin() throws IOException {
		registerNodes();

		// initialize the payload directory
		payloadDirectory = new OSGIBundlePayloadDirectory(bundleContext);

		final IPreferenceStore pStore = this.getPreferenceStore();
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

	private void extractBinaries() throws IOException {

		// get platform and architecture identifiers
		OperatingSystem os = OperatingSystem.getOS();
		Architecture arch = Architecture.getArchitecture();

		if (arch == Architecture.UNKNOWN) {
			LOGGER.warning("Unexpected architecure detected: falling back to 32 bit");
			arch = Architecture.X86;
		}

		boolean extracted64bitVersion = false;
		if (arch == Architecture.X86_64) {
			extracted64bitVersion = tryExtractPayloadZIP(
					payloadDirectory.getPath(), os, arch.toString());
		}

		// check if previous attempt worked or the OS is 32bit only
		if (!extracted64bitVersion) {
			tryExtractPayloadZIP(payloadDirectory.getPath(), os,
					Architecture.X86.toString());
		}
	}

	/**
	 * @throws IOException
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
	 * Tests if a zip file with the name binaries_{@p OperatingSystem}_{@p
	 * data_model}.zip is available and extracts it.
	 * 
	 * @param nodeBinariesDir
	 *            Target directory where it should be extracted to
	 * @param OperatingSystem
	 *            Identifier of the operating system to extract the appropriate
	 *            zip file
	 * @param data_model
	 *            Identifier for the datamodel that should be extracted.
	 * @return true if the specified zip file was found and extracted correctly.
	 * @throws IOException
	 */
	private boolean tryExtractPayloadZIP(File nodeBinariesDir,
			OperatingSystem os, String data_model) throws IOException {
		// check if a zip file for that combination of OS and data model exists
		if (getBinaryLocation().getResourceAsStream(
				getZipFileName(os, data_model)) != null) {
			// extract it
			ZipUtils.decompressTo(nodeBinariesDir, getBinaryLocation()
					.getResourceAsStream(getZipFileName(os, data_model)));

			// load the associated properties and store them as environment
			// variable
			loadEnvironmentVariables(os, data_model);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param os
	 * @param data_model
	 * @throws IOException
	 */
	private void loadEnvironmentVariables(OperatingSystem os, String data_model)
			throws IOException {
		Properties envProperites = new Properties();
		envProperites.load(getBinaryLocation().getResourceAsStream(
				getINIFileName(os, data_model)));
		for (Object key : envProperites.keySet()) {
			String k = key.toString();
			String v = envProperites.getProperty(k);
			environmentVariables.put(k, v);
		}
	}

	/**
	 * @param os
	 * @param data_model
	 * @return
	 */
	private String getINIFileName(OperatingSystem os, String data_model) {
		return "binaries_" + os + "_" + data_model + ".ini";
	}

	/**
	 * @param os
	 * @param data_model
	 * @return
	 */
	private String getZipFileName(OperatingSystem os, String data_model) {
		return "binaries_" + os + "_" + data_model + ".zip";
	}

	/**
	 * Reads the list of {@link MIMEType}s associated with the plugin and
	 * registers them in the central {@link MIMEtypeRegistry}.
	 */
	private void registerMimeTypes() {
		MIMEtypeRegistry registry = GenericNodesPlugin.getMIMEtypeRegistry();

		for (MIMEType mimeType : getMIMETypes()) {
			registry.registerMIMEtype(mimeType);
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
			String knimelessPackageName = getKNIMELessPackageName();

			for (String nodeName : this.getNodeNames()) {
				toolLocator.registerTool(new ExternalTool(knimelessPackageName,
						nodeName));
			}
		}
	}

	/**
	 * Extracts the package name from the plugin data.
	 * 
	 * @return
	 */
	private String getKNIMELessPackageName() {
		String packageName = this.getClass().getPackage().getName();
		String knimelessPackageName = packageName.substring(0,
				packageName.lastIndexOf(".knime"));
		return knimelessPackageName;
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

			// get package name
			String knimelessPackageName = getKNIMELessPackageName();

			// for each node find the executable
			for (String node : getNodeNames()) {
				File executable = getExecutableName(binaryDirectory, node);
				if (executable != null) {
					ExternalTool currentNode = new ExternalTool(
							knimelessPackageName, node);
					// register executalbe in the ToolFinder
					toolLocator.setToolPath(currentNode, executable,
							ToolPathType.SHIPPED);

					try {
						// check if we need to adjust the type
						if (toolLocator.getConfiguredToolPathType(currentNode) == ToolPathType.UNKNOWN) {
							toolLocator.updateToolPathType(currentNode,
									ToolPathType.SHIPPED);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					// TODO: handle non existent binaries, check if we have a
					// configured one, otherwise warn
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
	private File getExecutableName(File binDir, String nodename) {
		for (String extension : new String[] { "", ".bin", ".exe" }) {
			File binFile = new File(binDir, nodename + extension);
			if (binFile.canExecute())
				return binFile;
		}
		return null;
	}

	/**
	 * Get the plugin specific proberties stored in the plugin.properties file.
	 * 
	 * @return
	 */
	public Properties getProperties() {
		return this.props;
	}

	/**
	 * Get the environment variable customizations stored in the payload config
	 * file (e.g., binaries_mac_64.ini)
	 * 
	 * @return
	 */
	public Map<String, String> getEnvironment() {
		return environmentVariables;
	}

	/**
	 * Returns the list of nodes that the plugin provides.
	 * 
	 * @return
	 */
	public abstract List<String> getNodeNames();

	/**
	 * Returns the list of {@link MIMEType}s provided by the plugin.
	 * 
	 * @return
	 */
	public abstract List<MIMEType> getMIMETypes();

	/**
	 * Returns the {@link Class} where the binaries are located.
	 * 
	 * @return
	 */
	public abstract Class<?> getBinaryLocation();
}
