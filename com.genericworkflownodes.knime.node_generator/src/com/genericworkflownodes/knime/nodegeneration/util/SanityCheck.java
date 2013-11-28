/**
 * Copyright (c) 2013, Luis de la Garza.
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
package com.genericworkflownodes.knime.nodegeneration.util;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

/**
 * Simple class that performs some checks and informs the user about missing
 * files, folders, etc.
 * 
 * @author Luis de la Garza
 */
public class SanityCheck {
    
    private final String sourceDirectory;
    
    /**
     * Constructor.
     * 
     * @param sourceDirectory The folder in which all files to generate the nodes should be found.
     */
    public SanityCheck(final String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Returns a collection of warnings.
     * @return The warnings
     */
    public Collection<String> getWarnings() {
        final Collection<String> warnings = new LinkedList<String>();
        insertMessageIfFileIsMissing(NodesSourceDirectory.PAYLOAD_DIRECTORY, "Payload directory", warnings);
        insertMessageIfFileIsMissing(NodesSourceDirectory.ICONS_DIRECTORY, "Icons directory", warnings);
        insertMessageIfFileIsMissing(NodesSourceDirectory.CONTRIBUTING_PLUGINS_DIRECTORY, "Third-party plug-ins directory", warnings);
        return warnings;
    }
    
    
    /**
     * Returns a collection of errors.
     * @return The errors.
     */
    public Collection<String> getErrors() {
        final Collection<String> errors = new LinkedList<String>();
        insertMessageIfFileIsMissing(NodesSourceDirectory.DESCRIPTORS_DIRECTORY, "Descriptors (CTD) directory", errors);
        insertMessageIfFileIsMissing(NodesSourceDirectory.PLUGIN_PROPERTIES_FILE, "Plugin properties file", errors);
        insertMessageIfFileIsMissing(NodesSourceDirectory.DESCRIPTION_FILE, "Description file", errors);
        insertMessageIfFileIsMissing(NodesSourceDirectory.COPYRIGHT_FILE, "Copyright file", errors);
        insertMessageIfFileIsMissing(NodesSourceDirectory.LICENSE_FILE, "License file", errors);
        return errors;
    }
    
    private void insertMessageIfFileIsMissing(final String name, final String userFriendlyName, final Collection<String> collection) {
        if (!new File(sourceDirectory, name).exists()) {
            collection.add(userFriendlyName + " was not found in its expected location [" + sourceDirectory + File.separator + name + ']');
        }
    }
}
