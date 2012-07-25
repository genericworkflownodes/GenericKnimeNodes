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

import java.util.Properties;

/**
 * Factory interface to ease instantiation of converters.
 * 
 * @see IOutputConverter
 * @author aiche
 */
public interface IOutputConverterFactory {

	/**
	 * Creates a new {@link IOutputConverter} using the given properties.
	 * 
	 * @param properties
	 *            {@link Properties} used to configure the
	 *            {@link IOutputConverter}.
	 * @return A configured {@link IOutputConverter}.
	 */
	IOutputConverter create(Properties properties);
}
