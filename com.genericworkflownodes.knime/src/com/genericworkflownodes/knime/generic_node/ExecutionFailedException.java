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
package com.genericworkflownodes.knime.generic_node;

/**
 * @author aiche
 * 
 */
public class ExecutionFailedException extends Exception {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -8290707909912450927L;

    /**
     * C'tor.
     * 
     * @param nodeName
     *            The name of the Node that couldn't be executed.
     */
    public ExecutionFailedException(final String nodeName) {
        super(String.format("Failed to execute node %s", nodeName));
    }

    /**
     * C'tor.
     * 
     * @param nodeName
     *            The name of the Node that couldn't be executed.
     * @param t
     *            The cause.
     */
    public ExecutionFailedException(String nodeName, Throwable t) {
        super(String.format("Failed to execute node %s", nodeName), t);
    }
}
