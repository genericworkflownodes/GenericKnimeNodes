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

import java.util.List;
import java.util.Set;

/**
 * Interface for storing and retrieving parameter values.
 * 
 * @author roettig, aiche
 */
public interface INodeConfigurationStore {

	/**
	 * Sets the value of the parameter with the given identifier to the given
	 * value.
	 * 
	 * @param name
	 *            Identifier for the parameter.
	 * @param value
	 *            The new value of the parameter.
	 */
	void setParameterValue(String name, String value);

	/**
	 * Adds the given value to the parameter with the given identifier.
	 * 
	 * @param name
	 *            Identifier for the parameter.
	 * @param value
	 *            The added value of the parameter.
	 */
	void setMultiParameterValue(String name, String value);

	/**
	 * Returns the value of the parameter with the given identifier.
	 * 
	 * @param name
	 *            Identifier for the parameter.
	 * @return The value of the parameter.
	 */
	String getParameterValue(String name);

	/**
	 * Returns all stored values of the parameter with the given identifier.
	 * 
	 * @param name
	 *            Identifier for the parameter.
	 * @return The values of the parameter.
	 */
	List<String> getMultiParameterValue(String name);

	/**
	 * Get a list of all parameter identifiers stored in the
	 * {@link INodeConfigurationStore}.
	 * 
	 * @return A list of all available parameter identifier.
	 */
	Set<String> getParameterKeys();
}
