package com.genericworkflownodes.knime.nodegeneration.model.meta;

import java.security.InvalidParameterException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.genericworkflownodes.knime.nodegeneration.exceptions.InvalidVersionException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

public class GeneratedPluginMeta extends PluginMeta {

    private static final String PLUGIN_VERSION_REGEX = "^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$";

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
     * Checks whether a given package version is a proper OSGI version, i.e., it
     * should match ^\d+(\.\d+(\.\d+(.[a-zA-Z0-9]+)?)?)?$.
     * 
     * @param pluginVersion
     *            The plugin version as string which should be tested.
     * @return True if it is a valid version, false otherwise.
     */
    private static boolean isPluginVersionValid(final String pluginVersion) {
        return pluginVersion.matches(PLUGIN_VERSION_REGEX);
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

    /**
     * Updates the version qualifier of the plug-in meta. Update the qualifier
     * part of the plug-in version, e.g., 0.1.0.20000101 update with 20100101 ->
     * 0.1.0.20100101
     * 
     * @param qualifier
     *            The potentially higher qualifier.
     */
    protected String updateVersion(String qualifier){
        final Pattern p = Pattern
                .compile("^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$");
        Matcher m = p.matcher(getVersion());
        boolean found = m.find();
        if (!found)
        	throw new InvalidParameterException("Version " + getVersion() + " should be compliant to the pattern ^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9-_]+)?$");

        // version has no qualifier
        String newVersion = m.group(1)
                + (m.group(2) != null ? m.group(2) : ".0")
                + (m.group(3) != null ? m.group(3) : ".0");
        // append qualifier
        if (m.group(4) == null
                || qualifier.compareTo(m.group(4).substring(1)) > 0) {
            // external qualifier
        	if (!qualifier.isEmpty())
        	{
        		newVersion += "." + qualifier;
        	}
        } else {
            // our own
            newVersion += m.group(4);
        }
        return newVersion;
    }

    private final String name;
    private final String nodeRepositoyPath;
    private final String generatedPluginVersion;

    /**
     * Creates a Meta info object for the generated plug-in based on the
     * information contained in the source directory.
     * 
     * @param sourceDirectory
     *            The directory containing the plug-in that will be generated.
     * @param nodeGeneratorQualifier
     *            The version qualifier of the node generator.
     */
    public GeneratedPluginMeta(NodesSourceDirectory sourceDirectory,
            String nodeGeneratorQualifier) {
        super(getPackageRoot(sourceDirectory.getProperties()),
                getPluginVersion(sourceDirectory.getProperties()));

        // update the version qualifier based on the version of the node
        // generator
        if (nodeGeneratorQualifier != null) {
            generatedPluginVersion = updateVersion(nodeGeneratorQualifier);
        } else {
            generatedPluginVersion = getVersion();
        }

        if (getId() == null || getId().isEmpty()) {
            throw new InvalidParameterException("No package name was specified");
        }
        if (!isValidPackageRoot(getPackageRoot())) {
            throw new InvalidParameterException("The given package name \""
                    + getPackageRoot() + "\" is invalid");
        }

        name = getPluginName(sourceDirectory.getProperties(), getPackageRoot());
        if (name == null || name.isEmpty()) {
            throw new InvalidParameterException("No plugin name was specified");
        }
        if (!isPluginNameValid(name)) {
            throw new InvalidParameterException("The plugin name \"" + name
                    + "\" must only contain alpha numeric characters");
        }

        if (getVersion() == null || getVersion().isEmpty()) {
            throw new InvalidParameterException(
                    "No plugin version was specified");
        }
        if (!isPluginVersionValid(getVersion())) {
            throw new InvalidParameterException("The plugin version \""
                    + getVersion() + "\" is not valid");
        }

        nodeRepositoyPath = getNodeRepositoyPath(sourceDirectory
                .getProperties());
        if (nodeRepositoyPath == null || nodeRepositoyPath.isEmpty()) {
            throw new InvalidParameterException(
                    "No path within the node repository was specified");
        }
        if (!isNodeRepositoyPathValid(getVersion())) {
            throw new InvalidParameterException("The node repository path \""
                    + nodeRepositoyPath + "\" is not valid");
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
        return name;
    }

    /**
     * Returns the KNIME plugin's root package name
     * <p>
     * e.g. de.fu_berlin.imp.seqan
     * 
     * @return The plugin's package root.
     */
    public final String getPackageRoot() {
        return getId();
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
        return nodeRepositoyPath;
    }

    /**
     * Returns the version of the generated plug-in which includes also version
     * information from the node generator.
     * 
     * @return The version of the generated plugin.
     */
    public final String getGeneratedPluginVersion() {
        return generatedPluginVersion;
    }
}
