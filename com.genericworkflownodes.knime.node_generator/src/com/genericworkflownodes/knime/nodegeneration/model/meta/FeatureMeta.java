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

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;

import com.genericworkflownodes.knime.nodegeneration.exceptions.DuplicateNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.exceptions.InvalidNodeNameException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.FeatureSourceDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

/**
 * Meta information of the generated feature. The feature will bundle all
 * generated plugins, fragments, and additionally provided plugins.
 * 
 * @author aiche, bkahlert
 */
public class FeatureMeta extends PluginMeta {
	
    private final static Pattern VERSION_PATTERN = Pattern
            .compile("^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9]+)?$");
    
    private static final Logger LOGGER = Logger.getLogger(FeatureMeta.class
            .getCanonicalName());

    private final String name;
    private final String groupid;

    private final String description;
    private final String copyright;
    private final String license;
    
    public final ArrayList<GeneratedPluginMeta> generatedPluginMetas;
    public final ArrayList<ContributingPluginMeta> contributingPluginMetas;
    public final FeatureSourceDirectory featureSourceDir;

	private final String category;

    /**
     * Constructs the feature meta information given a singleton node source directory.
     * 
     * @param sourceDirectory
     * @throws PathnameIsNoDirectoryException 
     */
    public FeatureMeta(NodesSourceDirectory sourceDirectory, String nodeGeneratorLastChangeDate) throws PathnameIsNoDirectoryException {
        super(sourceDirectory);
        try {
            featureSourceDir = new FeatureSourceDirectory(sourceDirectory);
            generatedPluginMetas = new ArrayList<GeneratedPluginMeta>();
            generatedPluginMetas.add(new GeneratedPluginMeta(sourceDirectory, nodeGeneratorLastChangeDate));
            
            name = generatedPluginMetas.get(0).getName(); // take name of the plugin in the same folder
            id = generatedPluginMetas.get(0).getPackageRoot() + ".feature"; // take the plugins ID and add .feature
            groupid = id;
            contributingPluginMetas = sourceDirectory.getContributingPluginsDirectory().getContributingPluginMetas();
            category = "";

            description = FileUtils.readFileToString(sourceDirectory
                    .getDescriptionFile());
            copyright = FileUtils.readFileToString(sourceDirectory
                    .getCopyrightFile());
            license = FileUtils.readFileToString(sourceDirectory
                    .getLicenseFile());
        } catch (IOException e) {
            throw new InvalidParameterException(
                    "Could not read meta information.\n" + e.getMessage());
        }
    }
    
    /**
     * Constructs the feature meta information given a feature source directory with plugin subdirectories.
     * 
     * @param sourceDirectory
     * @throws DuplicateNodeNameException 
     * @throws InvalidNodeNameException 
     * @throws DocumentException 
     * @throws IOException 
     * @throws PathnameIsNoDirectoryException 
     */
    public FeatureMeta(FeatureSourceDirectory sourceDirectory, String nodeGeneratorLastChangeDate) throws PathnameIsNoDirectoryException, IOException, DocumentException, InvalidNodeNameException, DuplicateNodeNameException {
        super(sourceDirectory);
        try {
        	featureSourceDir = sourceDirectory;
            name = sourceDirectory.getProperty("featureName", sourceDirectory.getName());
            id = sourceDirectory.getProperty("featureId", sourceDirectory.getName());
            version = sourceDirectory.getProperty("featureVersion", sourceDirectory.getName());
            groupid = sourceDirectory.getProperty("groupId", sourceDirectory.getName());
            category = sourceDirectory.getProperty("category", "");

            description = FileUtils.readFileToString(sourceDirectory
                    .getDescriptionFile());
            copyright = FileUtils.readFileToString(sourceDirectory
                    .getCopyrightFile());
            license = FileUtils.readFileToString(sourceDirectory
                    .getLicenseFile());
        } catch (IOException e) {
            throw new InvalidParameterException(
                    "Could not read meta information.\n" + e.getMessage());
        }
        generatedPluginMetas = sourceDirectory.getGeneratedSubPluginMetas(nodeGeneratorLastChangeDate);
        contributingPluginMetas = 
        		sourceDirectory.getContributingPluginsDirectory() != null ?
        				sourceDirectory.getContributingPluginsDirectory().getContributingPluginMetas()
        				: new ArrayList<ContributingPluginMeta>();
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
     * Returns the description of this plugin.
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the copyright information for this plugin.
     * 
     * @return
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Returns the license information for this plugin.
     * 
     * @return
     */
    public String getLicense() {
        return license;
    }
    
    public Matcher matchVersion(final String version) {
        Matcher m = VERSION_PATTERN.matcher(version);

        // via definition this has to be true
        boolean found = m.matches();
        if (!found || m.groupCount() != 4)
        {
        	LOGGER.log(Level.SEVERE, "Version should be compliant to the pattern ^(\\d+)(\\.\\d+)?(\\.\\d+)?(.[a-zA-Z0-9-_]+)?$."
        			+ "This should not happen since it was checked during reading of the files. Please report as bug.");
        }

        return m;
    }
   
    
    public String findLatestQualifier(String generatorQualifier) {

        String highestQualifier = "";
        if (generatorQualifier != null)
            highestQualifier = generatorQualifier;

        for (GeneratedPluginMeta fMeta : this.generatedPluginMetas) {
            Matcher m = matchVersion(fMeta.getVersion());
            if (m.group(4) != null
                    && m.group(4).compareTo(highestQualifier) > 0) {
                highestQualifier = m.group(4);
            }
        }

        for (ContributingPluginMeta cMeta : this.contributingPluginMetas) {
            Matcher m = matchVersion(cMeta.getVersion());
            if (m.group(4) != null
                    && m.group(4).compareTo(highestQualifier) > 0) {
                highestQualifier = m.group(4);
            }

        }

        return highestQualifier;
    }

	public String getGroupid() {
		return groupid;
	}

	public String getCategory() {
		return category;
	}

}
