/**
 * Copyright (c) 2014, aiche.
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
package com.genericworkflownodes.knime.custom.config;

/**
 * Indicates that no binary was found.
 * 
 * @author aiche
 */
public class NoBinaryAvailableException extends Exception {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 7532082552728754620L;

    /**
     * Error message.
     */
    private static final String EXCEPTION_MESSAGE = "Couldn't find a binary file for the executable %s. "
            + "Please refer to the GKN Troubleshooting guide on how to provide missing binaries.";

    /**
     * C'tor.
     * 
     * @param executableName
     *            The executable that wasn't found.
     */
    public NoBinaryAvailableException(final String executableName) {
        super(String.format(EXCEPTION_MESSAGE, executableName));
    }

    /**
     * C'tor.
     * 
     * @param executableName
     *            The executable that wasn't found.
     * @param cause
     *            Exception causing a failure during find.
     */
    public NoBinaryAvailableException(final String executableName,
            Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE, executableName), cause);
    }

}
