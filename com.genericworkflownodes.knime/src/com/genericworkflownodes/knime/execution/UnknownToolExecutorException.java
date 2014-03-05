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
package com.genericworkflownodes.knime.execution;

/**
 * Indicates that the requested {@link IToolExecutor} is unknown.
 * 
 * @author aiche
 */
public class UnknownToolExecutorException extends Exception {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -5031966351686061877L;

    /**
     * C'tor.
     * 
     * @param executor
     *            The name of the unknown {@link IToolExecutor}.
     */
    public UnknownToolExecutorException(String executor) {
        super(
                String.format(
                        "The tool executor '%s' is not known. Please check your configuration.",
                        executor));
    }

    /**
     * C'tor.
     * 
     * @param executor
     *            The name of the unknown {@link IToolExecutor}.
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            getCause() method). (A null value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public UnknownToolExecutorException(String executor, Throwable cause) {
        super(
                String.format(
                        "The tool executor '%s' is not known. Please check your configuration.",
                        executor), cause);
    }

}
