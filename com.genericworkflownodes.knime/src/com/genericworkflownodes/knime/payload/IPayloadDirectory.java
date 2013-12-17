/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.payload;

import java.io.File;

/**
 * Abstraction of the payload storage directory. Provides information on the
 * location and the version of the included binaries.
 * 
 * By definition the referred directories exist after constructing this object,
 * so no {@link File#exists()} checks need to be performed.
 * 
 * @author aiche
 */
public interface IPayloadDirectory {

    /**
     * Returns the file system location of the payload directory.
     * 
     * @return The file system location of the payload directory.
     */
    File getPath();

    /**
     * Returns the directory where the executables are located. In most cases it
     * will be getPath()/bin.
     * 
     * @return The executable directory.
     */
    File getExecutableDirectory();

    /**
     * Returns true if no payload is contained in the referenced directories.
     * 
     * @return $true$ if no payload exists, $false$ otherwise.
     */
    boolean isEmpty();

}
