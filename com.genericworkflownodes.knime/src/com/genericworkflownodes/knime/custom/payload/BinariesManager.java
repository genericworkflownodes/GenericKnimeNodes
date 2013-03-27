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
package com.genericworkflownodes.knime.custom.payload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.genericworkflownodes.knime.custom.GenericActivator;
import com.genericworkflownodes.knime.payload.IPayloadDirectory;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator;

/**
 * Manages the extraction and registration of shipped binaries.
 * 
 * @author aiche
 */
public class BinariesManager implements IRunnableWithProgress {

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

	public BinariesManager(IPayloadDirectory payloadDirectory,
			final GenericActivator genericActivator) {
		// initialize the payload directory
		this.payloadDirectory = payloadDirectory;
		this.genericActivator = genericActivator;
	}

	/**
	 * Checks if the payload exists and is valid.
	 * 
	 * @return
	 */
	public boolean hasValidPayload() {
		// check if all extracted paths still available, if one fails,
		// re-extract
		if (payloadDirectory.isEmpty()) {
			return false;
		} else {
			boolean extractedPayloadIsValid = true;
			try {
				// for each node find the executable
				for (ExternalTool tool : genericActivator.getTools()) {
					File toolExecutable = PluginPreferenceToolLocator
							.getToolLocatorService().getToolPath(tool,
									ToolPathType.SHIPPED);
					// check if it exists and if it is in our payload
					if (!toolExecutable.exists()
							|| !toolExecutable.getAbsolutePath().startsWith(
									payloadDirectory.getPath()
											.getAbsolutePath())) {
						extractedPayloadIsValid = false;
						if (PluginPreferenceToolLocator.getToolLocatorService()
								.getConfiguredToolPathType(tool) == ToolPathType.SHIPPED)
							PluginPreferenceToolLocator.getToolLocatorService()
									.updateToolPathType(tool,
											ToolPathType.UNKNOWN);
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
	 * @param monitor
	 * 
	 * @throws IOException
	 *             In case of IO errors.
	 */
	private void extractBinaries(IProgressMonitor monitor) throws IOException {
		// clear the directory to avoid inconsistencies
		cleanPayload();
		// extract the actual payload
		extractPayloadZIP(monitor);
		// mark everything under payloadDirectory.getExecutableDirectory() as
		// isExecutable
		makePayloadExecutable();
	}

	public void cleanPayload() throws IOException {
		FileUtils.deleteDirectory(payloadDirectory.getPath());
	}

	/**
	 * Sets the executable bit for every file under
	 * {@link IPayloadDirectory#getExecutableDirectory()}.
	 */
	private void makePayloadExecutable() {
		@SuppressWarnings("unchecked")
		Iterator<File> fIt = FileUtils.iterateFiles(
				payloadDirectory.getExecutableDirectory(),
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		while (fIt.hasNext()) {
			fIt.next().setExecutable(true);
		}
	}

	/**
	 * Tests if a zip file with the name binaries.zip is available and extracts
	 * it.
	 * 
	 * @param monitor
	 * 
	 * @throws IOException
	 *             Exception is thrown in case of io problems.
	 */
	private void extractPayloadZIP(IProgressMonitor monitor) throws IOException {
		// check if a zip file for that combination of OS and data model exists
		if (hasPayload()) {
			// count number of entries in the zip file
			monitor.beginTask(
					"Checking shipped binaries for plugin "
							+ genericActivator.getPluginConfiguration()
									.getPluginName(), IProgressMonitor.UNKNOWN);
			int numEntries = ZipUtils.countEntries(getPayloadStream());
			monitor.done();

			// extract it
			monitor.beginTask(
					"Extracting shipped binaries for plugin "
							+ genericActivator.getPluginConfiguration()
									.getPluginName(), numEntries);
			ZipUtils.decompressTo(payloadDirectory.getPath(),
					getPayloadStream(), monitor);
			monitor.done();
		}
	}

	/**
	 * Returns true if the given plugin has a payload fragment.
	 * 
	 * @return
	 */
	public boolean hasPayload() {
		InputStream pStream = getPayloadStream();
		return pStream != null;
	}

	private InputStream getPayloadStream() {
		Enumeration<URL> e = genericActivator.getBundle().findEntries("/",
				getZipFileName(), true);
		if (e != null && e.hasMoreElements()) {
			try {
				return e.nextElement().openStream();
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		} else {
			return null;
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
				PluginPreferenceToolLocator.getToolLocatorService()
						.setToolPath(tool, executable, ToolPathType.SHIPPED);

				try {
					// check if we need to adjust the type
					if (PluginPreferenceToolLocator.getToolLocatorService()
							.getConfiguredToolPathType(tool) == ToolPathType.UNKNOWN) {
						PluginPreferenceToolLocator.getToolLocatorService()
								.updateToolPathType(tool, ToolPathType.SHIPPED);
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
			if (binFile.exists()) {
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

		Map<String, String> environmentVariables = new HashMap<String, String>();
		for (Object key : envProperites.keySet()) {
			String k = key.toString();
			String v = envProperites.getProperty(k);
			// transfer the environment variables into the generic activator
			environmentVariables.put(k, v);
		}

		// transfer the variables to the pluginconfig
		genericActivator.getPluginConfiguration().updateEnvironmentVariables(
				environmentVariables);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			extractBinaries(monitor);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	/**
	 * Finalizes the binaries by registering them and loading the necessary
	 * environment variables.
	 */
	public void register() {
		try {
			loadEnvironmentVariables();
			registerExtractedBinaries();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		}
	}
}
