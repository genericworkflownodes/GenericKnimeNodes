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
 * Indicates that the requested {@link ICommandGenerator} is unknown.
 * 
 * @author aiche
 */
public class UnknownCommandGeneratorException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4509777899761532363L;

    /**
     * C'tor.
     * 
     * @param commandGenerator
     *            Name of the unknown {@link ICommandGenerator}.
     */
    public UnknownCommandGeneratorException(String commandGenerator) {
        super(
                String.format(
                        "The ICommandGenerator '%s' is not known. Please check your configuration.",
                        commandGenerator));
    }

    /**
     * C'tor.
     * 
     * @param commandGenerator
     *            Name of the unknown {@link ICommandGenerator}.
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            getCause() method). (A null value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public UnknownCommandGeneratorException(String commandGenerator,
            Throwable cause) {
        super(
                String.format(
                        "The ICommandGenerator '%s' is not known. Please check your configuration.",
                        commandGenerator), cause);
    }
}
