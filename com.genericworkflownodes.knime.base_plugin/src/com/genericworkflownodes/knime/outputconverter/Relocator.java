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

import java.io.File;
import java.net.URI;

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
 * <li>%BASE% - The name of the original output file passed to the tool without
 * extension</li>
 * </ul>
 * 
 * @author aiche
 */
public class Relocator {

	private static String TEMP = "%TEMP%";
	private static String PWD = "%PWD%";
	private static String BASENAME = "%BASENAME[.*]%";

	/**
	 * The referenced parameter that should be transformed.
	 */
	private String reference;

	/**
	 * The regular expression used to find the parameter.
	 */
	private String location;

	/**
	 * C'tor.
	 * 
	 * @param parameter
	 *            The name of the output parameter that should be transformed
	 * @param location
	 *            The pattern used to find the output file.
	 */
	public Relocator(String parameter, String location) {
		reference = parameter;
		this.location = location;
	}

	/**
	 * Find the output file based on the given pattern and moves it to the
	 * originally described location.
	 * 
	 * @param config
	 *            The node configuration of the tool at execution time.
	 * @param target
	 *            The original output target specified when calling the tool and
	 *            the place where the found output should be stored.
	 * @param workingDirectory
	 *            The directory where the tool was called.
	 * @throws RelocatorException
	 *             If the file could not be found.
	 */
	public void relocate(INodeConfiguration config, URI target,
			File workingDirectory) throws RelocatorException {

		String updatedLocation = location;

		// replace fixed variables in the pattern with
		updatedLocation.replace(TEMP, System.getProperty("temp.dir"));
		updatedLocation.replace(PWD, workingDirectory.getAbsolutePath());

		fixBaseNames(updatedLocation);

		File fileToRelocate = new File(updatedLocation);
		if (!fileToRelocate.exists()) {
			throw new RelocatorException(
					"Could not find file specified by this relocator: "
							+ location);
		}

		fileToRelocate.renameTo(new File(target.getPath()));
	}

	private void fixBaseNames(String updatedLocation) {
		// TODO implement base name updating
	}

	/**
	 * Returns the name of the parameter that should be relocated.
	 * 
	 * @return The parameter name.
	 */
	public String getReferencedParamter() {
		return reference;
	}

	/**
	 * Returns the location information of the relocator.
	 * 
	 * @return The location parameter.
	 */
	public String getLocation() {
		return location;
	}
}
