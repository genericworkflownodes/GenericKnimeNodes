/**
 * Copyright (c) 2013, Stephan Aiche, Björn Kahlert.
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
package com.genericworkflownodes.knime.nodegeneration.model.meta;

import java.util.Properties;

import com.genericworkflownodes.knime.nodegeneration.model.directories.FeatureSourceDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

/**
 * 
 * Meta information of an Eclipse plugin.
 * 
 * @author aiche, bkahlert, jpfeuffer
 */
public class PluginMeta {

    protected String id;
    protected String version;
    
    private static final String PLUGIN_VERSION_REGEX = "^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$";
    private static final String PLUGIN_NAME_KEY = "pluginName";
    private static final String PLUGIN_VERSION_KEY = "pluginVersion";
	private static final String PLUGIN_PACKAGE_KEY = "pluginPackage";
	private static final String NODE_REPOSITORY_ROOT_KEY = "nodeRepositoryRoot";

    /**
     * Returns the plugin name.
     * <p>
     * If no configuration could be found, the name is created based on the
     * given package name. e.g. org.roettig.foo will result in foo
     * 
     * @param packageName
     * @return
     */
    protected static String getPluginName(Properties props, String packageName) {
        String pluginname = props.getProperty(PLUGIN_NAME_KEY);
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
    protected static boolean isPluginNameValid(final String pluginName) {
        return pluginName != null && pluginName.matches("^\\w+$");
    }

    /**
     * Returns the plugin version.
     * 
     * @param packageName
     * @return
     */
    protected static String getPluginVersion(final Properties props) {
        return props.getProperty(PLUGIN_VERSION_KEY);
    }

    /**
     * Checks whether a given package version is a proper OSGI version, i.e., it
     * should match ^\d+(\.\d+(\.\d+(.[a-zA-Z0-9]+)?)?)?$.
     * 
     * @param pluginVersion
     *            The plugin version as string which should be tested.
     * @return True if it is a valid version, false otherwise.
     */
    protected static boolean isPluginVersionValid(final String pluginVersion) {
        return pluginVersion.matches(PLUGIN_VERSION_REGEX);
    }

    /**
     * Returns the package name the generated plugin uses. (e.g.
     * org.roettig.foo).
     * 
     * @param props
     * @return
     */
    protected static String getPackageRoot(final Properties props) {
        return props.getProperty(PLUGIN_PACKAGE_KEY);
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
     * @todo this should be in the GeneratedPluginMeta only!
     * Returns the package name the generated plugin uses. (e.g.
     * org.roettig.foo).
     * 
     * @param props
     * @return
     */
    protected static String getNodeRepositoryPath(final Properties props) {
        return props.getProperty(NODE_REPOSITORY_ROOT_KEY);
    }

    /**
     * @todo this should be in the GeneratedPluginMeta only!
     * Checks whether a given package name is valid.
     * 
     * @param nodeRepositoryPath
     * @param id
     * @return true if package name is valid; false otherwise
     */
    public static boolean isNodeRepositoryPathValid(
            final String nodeRepositoryPath) {
        // TODO
        return true;
    }

    /**
     * @param id The plugin ID. E.g., de.openms.knime 
     * @param version E.g., 2.8.0.qualifier
     * @param isResourceOnly If it is only a resource plugin (
     * 	no nodes will be generated but folders will be provided
     *  for all other plugins via the DLLProvider extension point of GKN)
     */
    public PluginMeta(String id, String version) {
        this.id = id;
        this.version = version;
    }
    
    /**
     * @param sourceFolder A source folder with plugin.properties
     */
    public PluginMeta(NodesSourceDirectory sourceDir) {
        Properties p = sourceDir.getProperties();
        this.id = getPackageRoot(p);
        this.version = getPluginVersion(p);
    }
    
    /**
     * @param sourceFolder A source folder with feature.properties
     */
    public PluginMeta(FeatureSourceDirectory sourceDir) {
        Properties p = sourceDir.getProperties();
        this.id = getPackageRoot(p);
        this.version = getPluginVersion(p);
    }

    /**
     * Returns the plugin id.
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the plugin version.
     * 
     * @return
     */
    public String getVersion() {
        return version;
    }
    
}
