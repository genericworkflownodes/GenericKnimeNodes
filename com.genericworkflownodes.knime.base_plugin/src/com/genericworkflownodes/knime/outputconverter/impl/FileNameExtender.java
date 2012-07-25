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
package com.genericworkflownodes.knime.outputconverter.impl;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import com.genericworkflownodes.knime.outputconverter.IOutputConverter;

/**
 * {@link IOutputConverter} which adds a defined extension to the given file
 * name.
 * 
 * @author aiche
 */
public class FileNameExtender implements IOutputConverter {

	/**
	 * The actual extension to add to the file name.
	 */
	private String extensionToAdd;

	/**
	 * The property to check for the extension.
	 */
	private static String PROPERTY_NAME = "file-extension";

	/**
	 * C'tor which extracts the extension from the given properties.
	 * 
	 * @param props
	 *            Properties containing the extension to add to the file name.
	 */
	public FileNameExtender(final Properties props) {
		extensionToAdd = props.getProperty(PROPERTY_NAME, "");
	}

	@Override
	public URI convert(final URI uri) {
		URI finalUri = null;
		finalUri = new File(uri.getPath() + extensionToAdd).toURI();
		return finalUri;
	}
}
