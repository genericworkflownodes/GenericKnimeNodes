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
package com.genericworkflownodes.knime.nodegeneration.model.directories;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author jpfeuffer
 * 
 */
public class MavenParentDirectory extends Directory {

    /**
     * 
     */
    private static final long serialVersionUID = -8934613520286247501L;

    private File projectFile;
    private File pomXml;

    public MavenParentDirectory(Directory directory)
            throws PathnameIsNoDirectoryException, FileNotFoundException {
        super(directory, false);
        projectFile = new File(this, ".project");
        pomXml = new File(this, "pom.xml");
    }

    /**
     * Returns the .project file.
     * 
     * @return
     */
    public File getProjectFile() {
        return projectFile;
    }

    /**
     * Returns the .project file.
     * 
     * @return
     */
    public File getPomXml() {
        return pomXml;
    }

}
