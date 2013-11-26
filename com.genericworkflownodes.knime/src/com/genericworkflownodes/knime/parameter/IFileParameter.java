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
package com.genericworkflownodes.knime.parameter;

import java.security.Policy.Parameters;

import com.genericworkflownodes.knime.port.Port;

/**
 * Describes the interface requirements for {@link Parameters} representing
 * either files or lists of files.
 * 
 * @author aiche
 * 
 */
public interface IFileParameter {

    /**
     * Sets the port associated to this parameter.
     * 
     * @param port
     *            The new port.
     */
    void setPort(final Port port);

    /**
     * Returns the port associated to this parameter.
     * 
     * @return The port.
     */
    Port getPort();

}
