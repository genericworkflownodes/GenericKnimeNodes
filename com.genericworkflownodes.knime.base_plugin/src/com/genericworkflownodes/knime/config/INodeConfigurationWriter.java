/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.config;

import java.io.File;
import java.io.IOException;

/**
 * NodeConfigurationWriter interface.
 * 
 * @author roettig, aiche
 */
public interface INodeConfigurationWriter {

	/**
	 * Sets the given parameter to the value in the output file.
	 * 
	 * @param name
	 *            The parameter identifier.
	 * @param value
	 *            The new value of the parameter.
	 */
	void setParameterValue(String name, String value);

	/**
	 * Adds the given value to the given parameter.
	 * 
	 * @param name
	 *            The parameter identifier.
	 * @param value
	 *            The new value of the parameter.
	 */
	void setMultiParameterValue(String name, String value);

	/**
	 * Writes the node configuration to the given file.
	 * 
	 * @param file
	 *            File to store the node configuration.
	 * @throws IOException
	 *             The {@link IOException} will be thrown in case of io errors.
	 */
	void write(File file) throws IOException;
}
