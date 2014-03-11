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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory.PathnameIsNoDirectoryException;
import com.genericworkflownodes.knime.nodegeneration.model.directories.PluginDirectory;

/**
 * Meta information of a contributing plugin that will be included in the
 * generated feature.
 * 
 * @author aiche, bkahlert
 */
public class ContributingPluginMeta extends PluginMeta {

    public static class InvalidPluginException extends
            InvalidParameterException {
        private static final long serialVersionUID = -2737810313996216102L;

        public InvalidPluginException(Directory pluginDirectory) {
            super(String.format("%s does not contain a valid plugin.",
                    pluginDirectory.getAbsolutePath()));
        }
    }

    private static final Logger LOGGER = Logger
            .getLogger(ContributingPluginMeta.class.getName());

    private static final Pattern patternBundleVersion = Pattern
            .compile("^Bundle-Version:\\s*(.*)$");
    private static final Pattern patternBundleSymbolicName = Pattern
            .compile("^Bundle-SymbolicName:\\s*([0-9a-zA-Z_.-]*);?.*?$");

    public static ContributingPluginMeta create(
            Directory contributingPluginDirectory) {
        try {
            PluginDirectory pluginDirectory = new PluginDirectory(
                    contributingPluginDirectory);

            @SuppressWarnings("unchecked")
            List<String> lines = FileUtils.readLines(pluginDirectory
                    .getManifestMf());

            String bundleVersion = null;
            String bundleSymbolicName = null;

            for (String line : lines) {
                if (bundleVersion == null) {
                    Matcher matcherBundleVersion = patternBundleVersion
                            .matcher(line);
                    if (matcherBundleVersion.matches()) {
                        bundleVersion = matcherBundleVersion.group(1);
                    }
                }
                if (bundleSymbolicName == null) {
                    Matcher matcherBundleSymbolicName = patternBundleSymbolicName
                            .matcher(line);
                    if (matcherBundleSymbolicName.matches()) {
                        bundleSymbolicName = matcherBundleSymbolicName.group(1);
                    }
                }
            }

            if (bundleVersion != null && bundleSymbolicName != null) {
                return new ContributingPluginMeta(bundleSymbolicName,
                        bundleVersion, contributingPluginDirectory);
            }

        } catch (PathnameIsNoDirectoryException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Should never occur. Using the filter we guarantee that the given directory is a directory");
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new InvalidPluginException(contributingPluginDirectory);
    }

    private final Directory contributingPluginDirectory;

    /**
     * Constructs a {@link ContributingPluginMeta} given an id and version.
     * 
     * @param id
     * @param version
     */
    private ContributingPluginMeta(String id, String version,
            Directory contributingPluginDirectory) {
        super(id, version);
        this.contributingPluginDirectory = contributingPluginDirectory;
    }

    /**
     * Returns the directory that contains this contributing plugin.
     * 
     * @return
     */
    public Directory getContributingPluginDirectory() {
        return contributingPluginDirectory;
    }
}
