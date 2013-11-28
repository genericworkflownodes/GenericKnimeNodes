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
package com.genericworkflownodes.knime.nodegeneration.model.directories.source;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.genericworkflownodes.knime.nodegeneration.model.directories.Directory;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta;
import com.genericworkflownodes.knime.nodegeneration.model.meta.ContributingPluginMeta.InvalidPluginException;

/**
 * Abstraction of a directory containing a collection of plugins to include in
 * the final feature.
 * 
 * @author aiche, bkahlert
 */
public class ContributingPluginsDirectory extends Directory {

    private static final Logger LOGGER = Logger
            .getLogger(ContributingPluginsDirectory.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = -6496032916192735091L;

    public ContributingPluginsDirectory(File contributingPluginsDirectory)
            throws PathnameIsNoDirectoryException {
        super(contributingPluginsDirectory);
    }

    /**
     * 
     * @return
     */
    public List<ContributingPluginMeta> getContributingPluginMetas() {

        List<ContributingPluginMeta> contributingPluginMetas = new ArrayList<ContributingPluginMeta>();

        if (!this.exists())
            return contributingPluginMetas;

        String[] directories = this.list(new FilenameFilter() {
            @Override
            public boolean accept(File arg0, String arg1) {
                return new File(arg0, arg1).isDirectory();
            }
        });

        for (String directory : directories) {
            try {
                contributingPluginMetas.add(ContributingPluginMeta
                        .create(new Directory(new File(this, directory))));

            } catch (PathnameIsNoDirectoryException e) {
                LOGGER.log(
                        Level.SEVERE,
                        "Should never occur. Using the filter we guarantee that the given directory is a directory");
                LOGGER.log(Level.SEVERE, e.getMessage());
            } catch (InvalidPluginException e) {
                LOGGER.warning(e.getMessage());
            }
        }

        return contributingPluginMetas;
    }
}
