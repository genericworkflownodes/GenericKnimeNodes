package com.genericworkflownodes.knime.nodegeneration.model.meta;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

public class GeneratedPluginMeta extends PluginMeta {
	
	public class Dependency {
		//TODO check for valid Strings, IDs
		public Dependency(String pluginID, String versionRange)
		{
			this.pluginID = pluginID;
			this.versionRange = versionRange;
		}
		private final String pluginID;
		private final String versionRange;
		
		public String PluginID() {
			return pluginID;
		}
		public String getVersionRange() {
			return versionRange;
		}
	}

	private static final String PLUGIN_RESOURCE_PROVIDER_TARGET_KEY = "resourceProviderTarget";
	private static final String PLUGIN_EXTRA_DEPENDENCIES = "extraDependencies";
	
    /**
     * Updates the version qualifier of the plug-in meta.
     * If the version consists of less than 4 parts, it will be filled with
     * ".0"s plus the qualifier string given (prepended with a ".").
     * E.g., version "1" with qualifier "202301011212" will become "1.0.0.202301011212".
     * If the version has the string "genqualifier" as the fourth part, "genqualifier" will be replaced
     * with the given qualifier.
     * Otherwise, nothing happens.
     * 
     * @param qualifier
     *            The qualifier to replace if the version
     */
    protected String addReplaceGenQualifier(String version, String qualifier){
        final Pattern p = Pattern
                .compile("^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$");
        Matcher m = p.matcher(version);
        boolean found = m.find();
        if (!found)
        	throw new InvalidParameterException("Version " + getVersion() + " should be compliant to the pattern ^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9-_]+)?$");

        
        String newVersion = m.group(1)
                + (m.group(2) != null ? m.group(2) : ".0")
                + (m.group(3) != null ? m.group(3) : ".0");
        // append/replace (gen)qualifier
        if (m.group(4) == null
        		|| m.group(4).equals("genqualifier")){
            // external qualifier or current generation time
        	if (!qualifier.isEmpty())
        	{
        		newVersion += "." + qualifier;
        	}
        } else {
            // the qualifier as given in the properties
            newVersion += m.group(4);
        }
        return newVersion;
    }

    private final String name;
    private final String nodeRepositoryPath;
    private final String generatedPluginVersion;
    public final NodesSourceDirectory sourceDir;
    public final ArrayList<FragmentMeta> generatedFragmentMetas;
    public ArrayList<Dependency> extraDependencies;
	private final String resourceProviderTarget;

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

        resourceProviderTarget = getResourceProviderTarget(sourceDirectory.getProperties());
        extraDependencies = getDependencies(sourceDirectory.getProperties());
        sourceDir = sourceDirectory;
        
        // update the version qualifier based on the version of the node
        // generator
        if (nodeGeneratorQualifier != null) {
            generatedPluginVersion = addReplaceGenQualifier(getVersion(), nodeGeneratorQualifier);
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

        if (!isResourceOnly())
        {
            nodeRepositoryPath = getNodeRepositoryPath(sourceDirectory
                    .getProperties());
            if (nodeRepositoryPath == null || nodeRepositoryPath.isEmpty()) {
                throw new InvalidParameterException(
                        "No path within the node repository was specified");
            }
            if (!isNodeRepositoryPathValid(getVersion())) {
                throw new InvalidParameterException("The node repository path \""
                        + nodeRepositoryPath + "\" is not valid");
            }
        } else {
        	nodeRepositoryPath = "";
        }
        
        if (sourceDirectory.getPayloadDirectory() != null)
        {
        	generatedFragmentMetas = sourceDirectory.getPayloadDirectory().getFragmentMetas(this);
        } else {
        	generatedFragmentMetas = new ArrayList<FragmentMeta>();
        }
    }

	/**
     * Gets if the KNIME plugin to be generated is a resource-only plugin or contains nodes.
     * <p>
     * TODO we could determine it based on the descriptors folder also!
     * 
     * @return The plugin's name.
     */
    private String getResourceProviderTarget(Properties properties) {
		return properties.getProperty(PLUGIN_RESOURCE_PROVIDER_TARGET_KEY, "");
	}
    
    
	/**
     * Gets if the KNIME plugin to be generated is a resource-only plugin or contains nodes.
     * <p>
     * TODO we could determine it based on the descriptors folder also!
     * 
     * @return The plugin's name.
     */
    private ArrayList<Dependency> getDependencies(Properties properties) {
		String[] deps = properties.getProperty(PLUGIN_EXTRA_DEPENDENCIES, "").split(";");
		ArrayList<Dependency> result = new ArrayList<Dependency>();
		for (String dep : deps)
		{
			if(!dep.isBlank())
			{
				String[] id_ver = dep.split(":");
				result.add(new Dependency(id_ver[0], id_ver[1]));
			}
		}
		return result;
	}
    
    
	/**
     * Gets if the KNIME plugin to be generated is a resource-only plugin or contains nodes.
     * <p>
     * TODO we could determine it based on the descriptors folder also!
     * 
     * @return The plugin's name.
     */
    public final boolean isResourceOnly() {
		return !resourceProviderTarget.isEmpty();
	}
    
	/**
     * Returns the target name of this resource providing plugin.
     * GKN ToolExecutor implementations can use that to only accept resources from specific
     * providers. This is parsed from the resourceProviderTarget property of the
     * plugins.properties.
     * <p>
     * 
     * @return The target name which can be an arbitrary identifier.
     */
	public String getResourceProviderTarget() {
		return resourceProviderTarget;
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
        return nodeRepositoryPath;
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

	public ArrayList<Dependency> getExtraDependencies() {
		return extraDependencies;
	}
	
	public String getExtraDependenciesAsConcatString() {
		String ret = "";
		for (Dependency dep : extraDependencies)
		{
			ret += ",\n " + dep.pluginID + ";bundle-version=\"" + dep.versionRange + "\"";
		}
		return ret;
	}

}
