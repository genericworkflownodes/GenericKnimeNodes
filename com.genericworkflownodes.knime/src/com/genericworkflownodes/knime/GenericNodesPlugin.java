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

import com.genericworkflownodes.util.Helper;

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
     * The Docker installation directory
     */
    private static String dockerInstallationDir = "";
    
    /**
     * The VM installation directory used by docker-machine
     */
    private static String vmInstllationDir = "";
    
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
        if (Helper.isWin()) {
            GenericNodesPlugin.setDockerInstallationDir("C:\\Program Files\\Docker Toolbox");
            GenericNodesPlugin.setVmInstllationDir("C:\\Program Files\\Oracle\\VirtualBox");
        } else if (Helper.isMac()) {
            GenericNodesPlugin.setDockerInstallationDir( "/usr/local/bin");
            GenericNodesPlugin.setVmInstllationDir("/usr/local/bin");
        } else{
            GenericNodesPlugin.setDockerInstallationDir( "/usr/bin");
            GenericNodesPlugin.setVmInstllationDir("/usr/bin");
        }
        gknPLugin = this;
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

    /**
     * @return the dockerInstallationDir
     */
    public static String getDockerInstallationDir() {
        return dockerInstallationDir;
    }

    /**
     * @param dockerInstallationDir the dockerInstallationDir to set
     */
    public static void setDockerInstallationDir(String dockerInstallationDir) {
        LOGGER.debug("Setting GKN dockerInstallationDir: " + dockerInstallationDir);
        GenericNodesPlugin.dockerInstallationDir = dockerInstallationDir;
    }

    /**
     * @return the vmInstllationDir
     */
    public static String getVmInstllationDir() {
        return vmInstllationDir;
    }

    /**
     * @param vmInstllationDir the vmInstllationDir to set
     */
    public static void setVmInstllationDir(String vmInstllationDir) {
        LOGGER.debug("Setting GKN vmInstllationDir: " + vmInstllationDir);
        GenericNodesPlugin.vmInstllationDir = vmInstllationDir;
    }
}
