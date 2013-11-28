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
 * Provides some general implementations for classes implementing
 * {@link IPayloadDirectory}.
 * 
 * @author aiche
 */
abstract class AbstractPayloadDirectory implements IPayloadDirectory {

    private static String BIN_EXT = "bin";

    @Override
    public File getExecutableDirectory() {
        File executablePath = new File(getPath(), BIN_EXT);
        executablePath.mkdirs();
        return executablePath;
    }

    @Override
    public boolean isEmpty() {
        return (getExecutableDirectory().list().length == 0);
    }
}
