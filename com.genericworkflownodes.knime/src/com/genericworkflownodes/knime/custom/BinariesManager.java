/**
 * Copyright (c) 2013, Stephan Aiche.
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
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.payload.IPayloadDirectory;
import com.genericworkflownodes.knime.payload.OSGIBundlePayloadDirectory;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;
import com.genericworkflownodes.util.ZipUtils;

/**
 * Manages the extraction and registriation of shipped binaries.
 * 
 * @author aiche
 */
public class BinariesManager {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BinariesManager.class
			.getCanonicalName());

	/**
	 * An abstraction of the payload directory.
	 */
	private IPayloadDirectory payloadDirectory;

	private GenericActivator genericActivator;

	/**
	 * Plugin specific environment variables.
	 */
	private Map<String, String> environmentVariables = new HashMap<String, String>();

	public BinariesManager(final BundleContext bundleContext,
			final GenericActivator genericActivator) {
		// initialize the payload directory
		payloadDirectory = new OSGIBundlePayloadDirectory(bundleContext);
		this.genericActivator = genericActivator;
	}

	public void run() throws IOException {
		// We extract the payload only if we can find nothing inside the
		// referenced directory. If the directory is not empty we assume that
		// the payload was already extracted
		if (checkExtractedPayload()) {
			extractBinaries();
		}

		// make sure everything is extracted and ready to run
		if (!checkExtractedPayload()) {
			// load the associated properties and store them as environment
			// variable
			loadEnvironmentVariables();
			registerExtractedBinaries();
		}
	}

	private boolean checkExtractedPayload() {
		// check if all extracted paths still available, if one fails,
		// re-extract
		if (payloadDirectory.isEmpty()) {
			return false;
		} else {
			boolean extractedPayloadIsValid = true;
			try {
				IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
						.getWorkbench().getService(IToolLocatorService.class);

				if (toolLocator != null) {
					// for each node find the executable
					for (ExternalTool tool : genericActivator.getTools()) {
						File toolExecutable = toolLocator.getToolPath(tool,
								ToolPathType.SHIPPED);
						// check if it exists
						if (!toolExecutable.exists()) {
							extractedPayloadIsValid = false;
							if (toolLocator.getConfiguredToolPathType(tool) == ToolPathType.SHIPPED)
								toolLocator.updateToolPathType(tool,
										ToolPathType.UNKNOWN);
						}

						// check if it is in our payload
						if (!toolExecutable.getAbsolutePath().startsWith(
								payloadDirectory.getPath().getAbsolutePath())) {
							extractedPayloadIsValid = false;
							if (toolLocator.getConfiguredToolPathType(tool) == ToolPathType.SHIPPED)
								toolLocator.updateToolPathType(tool,
										ToolPathType.UNKNOWN);

						}
					}
				}
			} catch (Exception e) {
				LOGGER.warning("Error during payload check. Assume there is no extracted payload.");
				LOGGER.warning(e.getMessage());
				return false;
			}
			return extractedPayloadIsValid;
		}
	}

	/**
	 * Tries to extract platform specific binaries from the plugin.jar.
	 * 
	 * @throws IOException
	 *             In case of IO errors.
	 */
	private void extractBinaries() throws IOException {
		// clear the directory to avoid inconsistencies
		FileUtils.deleteDirectory(payloadDirectory.getPath());
		tryExtractPayloadZIP();
	}

	/**
	 * Tests if a zip file with the name binaries.zip is available and extracts
	 * it.
	 * 
	 * @throws IOException
	 *             Exception is thrown in case of io problems.
	 */
	private void tryExtractPayloadZIP() throws IOException {
		// check if a zip file for that combination of OS and data model exists
		if (genericActivator.getBinaryLocation().getResourceAsStream(
				getZipFileName()) != null) {
			// extract it
			ZipUtils.decompressTo(payloadDirectory.getPath(), genericActivator
					.getBinaryLocation().getResourceAsStream(getZipFileName()));
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
	 * Adds all extracted binaries to the tool registry.
	 */
	private void registerExtractedBinaries() {

		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {

			// get binary path
			File binaryDirectory = payloadDirectory.getExecutableDirectory();

			// for each node find the executable
			for (ExternalTool tool : genericActivator.getTools()) {
				File executable = getExecutableName(binaryDirectory,
						tool.getExecutableName());
				if (executable != null) {
					// make executable
					executable.setExecutable(true);
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
	 * Tries to load data from the platform specific ini file, contained in the
	 * plugin.jar.
	 * 
	 * @throws IOException
	 *             I thrown in case of IO errors.
	 */
	private void loadEnvironmentVariables() throws IOException {
		Properties envProperites = new Properties();

		File iniFile = new File(payloadDirectory.getPath(), getINIFileName());

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
}
