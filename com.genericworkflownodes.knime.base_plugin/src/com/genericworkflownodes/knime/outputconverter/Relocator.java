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

import com.genericworkflownodes.knime.config.INodeConfiguration;

/**
 * Implements regular expression based conversion of output files. The given
 * pattern is used to find the parameter and moved to the place expected by the
 * NodeConfiguration.
 * 
 * Supported variables that are replaced during execution in the pattern
 * <ul>
 * <li>%TEMP% - The temporary dirctory obtained from the Java Runtime.</li>
 * <li>%PWD% - The working directory for the tool execution.</li>
 * <li>%BASE% - The base name of the original output file passed to the tool.</li>
 * </ul>
 * 
 * @author aiche
 */
public class Relocator {

	/**
	 * The referenced parameter that should be transformed.
	 */
	private String reference;

	/**
	 * The regular expression used to find the parameter.
	 */
	private String pattern;

	/**
	 * C'tor.
	 * 
	 * @param parameter
	 *            The name of the output parameter that should be transformed
	 * @param pattern
	 *            The pattern used to find the output file.
	 */
	public Relocator(String parameter, String pattern) {
		reference = parameter;
		this.pattern = pattern;
	}

	/**
	 * Find the output file based on the given pattern and moves it to the
	 * originally described location.
	 * 
	 * @param config
	 *            The node configuration of the tool at execution time.
	 */
	public void relocate(INodeConfiguration config) {

	}
}
