/**
 * Copyright (c) 2012, Marc Röttig.
 * Copyright (c) 2012-2014, Stephan Aiche.
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
package com.genericworkflownodes.knime.parameter;

/**
 * The InvalidParameterValueException is thrown when the supplied value for a
 * parameter could not be validated.
 *
 * @author roettig
 */
public class InvalidParameterValueException extends Exception {

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 5408531919859345420L;

    /**
     * Constructor.
     *
     * @param msg
     *            the detail message.
     */
    public InvalidParameterValueException(final String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg
     *            the detail message.
     * @param t
     *            the cause.
     */
    public InvalidParameterValueException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
