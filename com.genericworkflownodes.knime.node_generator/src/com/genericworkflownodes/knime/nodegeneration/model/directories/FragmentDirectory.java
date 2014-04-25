/**
 * Copyright (c) 2013, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

import com.genericworkflownodes.knime.nodegeneration.model.directories.build.NodesBuildBinaryResourcesDirectory;
import com.genericworkflownodes.knime.nodegeneration.model.meta.FragmentMeta;

/**
 * Abstract representation of a fragment project/directory.
 * 
 * @author aiche
 */
public class FragmentDirectory extends PluginDirectory {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 4561247274907458731L;

    private NodesBuildBinaryResourcesDirectory binaryResourcesDirectory;

    /**
     * Create the directory.
     * 
     * @param directory
     * @param payload
     * @param
     * @throws FileNotFoundException
     */
    public FragmentDirectory(Directory directory, FragmentMeta fragmentMeta)
            throws PathnameIsNoDirectoryException {
        super(new File(directory, fragmentMeta.getId()));
        File payloadDirectory = new File(this, "payload");
        binaryResourcesDirectory = new NodesBuildBinaryResourcesDirectory(
                payloadDirectory);
    }

    /**
     * Returns the source directory where this fragments's binary resources
     * (i.e., the shipped executables) are stored.
     * 
     * @return
     */
    public NodesBuildBinaryResourcesDirectory getBinaryResourcesDirectory() {
        return binaryResourcesDirectory;
    }
}
