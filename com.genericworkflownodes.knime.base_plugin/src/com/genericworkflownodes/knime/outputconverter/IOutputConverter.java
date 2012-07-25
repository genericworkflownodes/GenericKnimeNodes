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
package com.genericworkflownodes.knime.outputconverter;

import java.net.URI;

/**
 * Interface for the abstraction of output port mapping.
 * 
 * <p>
 * Some tools require a specific mapping of provided output ports to real output
 * names. For instance EMBOSS automatically appends file endings to the provided
 * file name.
 * 
 * @author aiche
 */
public interface IOutputConverter {

	/**
	 * Converts the provided {@link URI} to a new {@link URI}.
	 * 
	 * @param uri
	 *            The {@link URI} to convert.
	 * @return The converted {@link URI}.
	 */
	URI convert(URI uri);
}
