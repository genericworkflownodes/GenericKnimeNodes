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

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.custom.payload.BinariesManager;
import com.genericworkflownodes.knime.mime.IMIMEtypeRegistry;
import com.genericworkflownodes.knime.payload.IPayloadDirectory;
import com.genericworkflownodes.knime.payload.OSGIBundlePayloadDirectory;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;

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
	 * An abstraction of the payload directory.
	 */
	private IPayloadDirectory payloadDirectory;

	/**
	 * The bundle context for access inside the activator.
	 */
	private BundleContext bundleContext;

	/**
	 * Handles the extraction of the payload.
	 */
	private BinariesManager binariesManager;

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
		binariesManager = new BinariesManager(payloadDirectory, this);
		final IPreferenceStore pStore = getPreferenceStore();
		pStore.setValue("binaries_path", payloadDirectory.getPath()
				.getCanonicalPath());

		loadPluginProperties();
		registerMimeTypes();
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
			GenericNodesPlugin.log(getPluginConfiguration().getPluginId()
					+ " plugin properties are ... ");
			for (Object key : props.keySet()) {
				GenericNodesPlugin.log(key + " -> " + props.get(key));
			}
		}
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
				LOGGER.log(Level.FINEST, "Register MIME Type: " + mimeType);
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
	 * Get the plugin specific proberties stored in the plugin.properties file.
	 * 
	 * @return The properties loaded for this plugin.
	 */
	public final Properties getProperties() {
		return props;
	}

	/**
	 * Gives access to the {@link BinariesManager} responsible for
	 * extracting/registering/handling the binaries contained in the payload.
	 * 
	 * @return the binariesManager
	 */
	public final BinariesManager getBinariesManager() {
		return binariesManager;
	}

	/**
	 * Returns a {@link List} of {@link ExternalTool}s contained in the plugin.
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
	 * Gives access to the plugin config of the derived plugin.
	 * 
	 * @return The plugin config.
	 */
	public abstract IPluginConfiguration getPluginConfiguration();

}
