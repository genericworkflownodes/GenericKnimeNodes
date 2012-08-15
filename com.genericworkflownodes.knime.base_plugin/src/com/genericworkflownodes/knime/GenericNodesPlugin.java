/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime;

import org.ballproject.knime.base.util.FileStash;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.BundleContext;

import com.genericworkflownodes.knime.mime.DefaultMIMEtypeRegistry;
import com.genericworkflownodes.knime.mime.IMIMEtypeRegistry;
import com.genericworkflownodes.knime.mime.demangler.DemanglerRegistry;
import com.genericworkflownodes.knime.mime.demangler.IDemanglerRegistry;
import com.genericworkflownodes.knime.preferences.PreferenceInitializer;

/**
 * This is the OSGI bundle activator.
 * 
 * @author roettig,aiche
 */
public class GenericNodesPlugin extends AbstractUIPlugin {
	/**
	 * The shared instance.
	 */
	private static GenericNodesPlugin GKN_PLUGIN;

	/**
	 * The central static logger.
	 */
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(GenericNodesPlugin.class);

	/**
	 * Debuggin state of the plugin.
	 */
	private static boolean DEBUG = false;

	/**
	 * Central IMIMETypeRegistry.
	 */
	private static IMIMEtypeRegistry MIMETYPE_REGISTRY = new DefaultMIMEtypeRegistry();

	/**
	 * Central registry for all Demanglers.
	 */
	private static IDemanglerRegistry DEMANGLER_REGISTRY = new DemanglerRegistry();

	/**
	 * Logging method for debugging purpose.
	 * 
	 * @param message
	 *            The message to log.
	 */
	public static void log(final String message) {
		if (GenericNodesPlugin.DEBUG) {
			System.out.println(message);
			LOGGER.info(message);
		}
	}

	/**
	 * Check if the plugin is in DEBUG mode.
	 * 
	 * @return True if debugging is enabled, false otherwise.
	 */
	public static boolean isDebug() {
		return GenericNodesPlugin.DEBUG;
	}

	/**
	 * Change debug setting.
	 */
	public static void toggleDebug() {
		GenericNodesPlugin.DEBUG = !GenericNodesPlugin.DEBUG;
		System.out.println("toggling Debug Mode");
	}

	/**
	 * Sets the debug status of the plugin.
	 * 
	 * @param debugEnabled
	 *            The new debug status.
	 */
	public static void setDebug(final boolean debugEnabled) {
		GenericNodesPlugin.DEBUG = debugEnabled;
		System.out.println("setting Debug Mode :" + debugEnabled);
	}

	/**
	 * Access the central {@link IMIMEtypeRegistry} of the plugin.
	 * 
	 * @return The central, static {@link IMIMEtypeRegistry}.
	 */
	public static IMIMEtypeRegistry getMIMEtypeRegistry() {
		return MIMETYPE_REGISTRY;
	}

	/**
	 * Access the central {@link IDemanglerRegistry} of the plugin.
	 * 
	 * @return The central, static {@link IDemanglerRegistry}.
	 */
	public static IDemanglerRegistry getDemanglerRegistry() {
		return DEMANGLER_REGISTRY;
	}

	/**
	 * The constructor.
	 */
	public GenericNodesPlugin() {
		super();
		GKN_PLUGIN = this;
	}

	/**
	 * This method is called upon plug-in activation.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this GKN_PLUGIN could not be started
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		log("starting GKN_PLUGIN: GenericNodesPlugin");

		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		FileStash
				.getInstance()
				.setStashDirectory(
						store.getString(PreferenceInitializer.PREF_FILE_STASH_LOCATION));

	}

	/**
	 * This method is called when the plug-in is stopped.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this GKN_PLUGIN could not be stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		GKN_PLUGIN = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Singleton instance of the Plugin
	 */
	public static GenericNodesPlugin getDefault() {
		return GKN_PLUGIN;
	}

}
