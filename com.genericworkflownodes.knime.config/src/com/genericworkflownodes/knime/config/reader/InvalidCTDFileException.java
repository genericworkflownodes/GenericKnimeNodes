/**
 * Copyright (c) 2014, Stephan Aiche.
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
package com.genericworkflownodes.knime.config.reader;

/**
 * Indicates that the ctd file was invalid.
 * 
 * @author aiche
 */
public class InvalidCTDFileException extends Exception {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -1737908441724923353L;

    /**
     * C'tor.
     * 
     * @param message
     *            Details on the cause of the exception.
     */
    public InvalidCTDFileException(String message) {
        super(message);
    }

    /**
     * C'tor.
     * 
     * @param message
     *            Details on the cause of the exception.
     * @param t
     *            The cause.
     */
    public InvalidCTDFileException(String message, Throwable t) {
        super(message, t);
    }
}
