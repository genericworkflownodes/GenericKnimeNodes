package com.genericworkflownodes.knime.nodegeneration.model.meta;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

public class GeneratedPluginMeta extends PluginMeta {

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
    public final NodesSourceDirectory sourceDir;
    public final ArrayList<FragmentMeta> generatedFragmentMetas;

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
        super(sourceDirectory);

        sourceDir = sourceDirectory;
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
        
        if (sourceDirectory.getPayloadDirectory() != null)
        {
        	generatedFragmentMetas = sourceDirectory.getPayloadDirectory().getFragmentMetas(this);
        } else {
        	generatedFragmentMetas = new ArrayList<FragmentMeta>();
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
