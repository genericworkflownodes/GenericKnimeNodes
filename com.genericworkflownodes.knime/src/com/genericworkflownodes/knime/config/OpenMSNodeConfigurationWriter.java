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
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * NodeConfigurationWriter for OpenMS-ini files.
 * 
 * @author aiche
 */
public class OpenMSNodeConfigurationWriter extends CTDNodeConfigurationWriter
		implements INodeConfigurationWriter {

	/**
	 * Constructor using the original CTD xml as input.
	 * 
	 * @param xml
	 *            The original ctd file as single string.
	 */
	public OpenMSNodeConfigurationWriter(final String xml) {
		super(xml);
	}

	@Override
	public void write(File file) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();

		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		writer.write(getDocument().selectSingleNode("//PARAMETERS"));

		writer.close();
	}
}
