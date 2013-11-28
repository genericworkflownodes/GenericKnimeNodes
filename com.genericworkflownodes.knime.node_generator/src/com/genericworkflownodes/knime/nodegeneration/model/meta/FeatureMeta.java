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

import org.apache.commons.io.FileUtils;

import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

/**
 * Meta information of the generated feature. The feature will bundle all
 * generated plugins, fragments, and additionally provided plugins.
 * 
 * @author aiche, bkahlert
 */
public class FeatureMeta extends PluginMeta {

    private final String name;

    private final String description;
    private final String copyright;
    private final String license;

    /**
     * Constructs the feature meta information given the node source directory.
     * 
     * @param sourceDirectory
     */
    public FeatureMeta(NodesSourceDirectory sourceDirectory,
            GeneratedPluginMeta pluginMeta) {
        super(pluginMeta.getId(), pluginMeta.getVersion());
        try {
            name = pluginMeta.getName();

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

}
