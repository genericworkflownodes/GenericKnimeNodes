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

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.BundleContext;

/**
 * This is the OSGI bundle activator.
 * 
 * @author roettig,aiche
 */
public class GenericNodesPlugin extends AbstractUIPlugin {

    /**
     * The shared instance.
     */
    private static GenericNodesPlugin gknPLugin;

    /**
     * The central static logger.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(GenericNodesPlugin.class);

    /**
     * Debugging state of the plug-in.
     */
    private static boolean isDebugModeEnabled = false;

    /**
     * Logging method for debugging purpose.
     * 
     * @param message
     *            The message to log.
     */
    public static void log(final String message) {
        if (GenericNodesPlugin.isDebugModeEnabled) {
            LOGGER.info(message);
        }
    }

    /**
     * Check if the plug-in is in isDebugModeEnabled mode.
     * 
     * @return True if debugging is enabled, false otherwise.
     */
    public static boolean isDebug() {
        return GenericNodesPlugin.isDebugModeEnabled;
    }

    /**
     * Sets the isDebugModeEnabled status of the plug-in.
     * 
     * @param debugEnabled
     *            The new isDebugModeEnabled status.
     */
    public static void setDebug(final boolean debugEnabled) {
        GenericNodesPlugin.isDebugModeEnabled = debugEnabled;
        LOGGER.debug("Setting GKN isDebugModeEnabled mode: " + debugEnabled);
    }

    /**
     * This method is called upon plug-in activation.
     * 
     * @param context
     *            The OSGI bundle context
     * @throws Exception
     *             If this gknPLugin could not be started
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        gknPLugin = this;
        log("starting gknPLugin: GenericNodesPlugin");
    }

    /**
     * This method is called when the plug-in is stopped.
     * 
     * @param context
     *            The OSGI bundle context
     * @throws Exception
     *             If this gknPLugin could not be stopped
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        gknPLugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return Singleton instance of the Plugin
     */
    public static GenericNodesPlugin getDefault() {
        return gknPLugin;
    }

}
