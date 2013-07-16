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
package com.genericworkflownodes.knime.config.reader.handler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.NodeConfiguration;

/**
 * The main {@link ContentHandler} for the CTD.
 * 
 * @author aiche
 */
public class CTDHandler extends DefaultHandler {

	private static String ATTR_NAME = "name";
	private static String ATTR_VERSION = "version";
	private static String TAG_DESCRIPTION = "description";
	private static String TAG_MANUAL = "manual";
	private static String ATTR_DOCURL = "docurl";
	private static String ATTR_CATEGORY = "category";
	private static String TAG_CLI = "cli";
	private static String TAG_RELOCATORS = "relocators";
	private static String TAG_PARAMETERS = "PARAMETERS";
	private static String TAG_EXECUTABLE_NAME = "executableName";
	private static String TAG_EXECUTABLE_PATH = "executablePath";
	private static String TAG_TOOL = "tool";

	/**
	 * The {@link INodeConfiguration} generated while parsing the CTD document.
	 */
	private NodeConfiguration config;

	/**
	 * The content contained in the current xml tag.
	 */
	private StringBuilder currentContent;

	/**
	 * The {@link XMLReader} that uses this content handler.
	 */
	private XMLReader xmlReader;

	/**
	 * C'tor.
	 * 
	 * @param xmlReader
	 *            The {@link XMLReader} that uses this content handler.
	 */
	public CTDHandler(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
		currentContent = new StringBuilder();
		config = new NodeConfiguration();
	}

	/**
	 * Access the parsed node configuration.
	 * 
	 * @return The processed node configuration.
	 */
	public INodeConfiguration getNodeConfiguration() {
		return config;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		currentContent.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		currentContent.setLength(0);
		if (TAG_PARAMETERS.equals(name)) {
			xmlReader.setContentHandler(new ParamHandler(xmlReader, this,
					config));
		} else if (TAG_RELOCATORS.equals(name)) {
			xmlReader.setContentHandler(new RelocatorHandler(xmlReader, this,
					config));
		} else if (TAG_CLI.equals(name)) {
			xmlReader.setContentHandler(new CLIElementHandler(xmlReader, this,
					config));
		} else if (TAG_TOOL.equals(name)) {
			// root tag -> parse out the attribute values
			config.setName(attributes.getValue(ATTR_NAME));
			config.setVersion(attributes.getValue(ATTR_VERSION));
			if (attributes.getValue(ATTR_DOCURL) != null) {
				config.setDocUrl(attributes.getValue(ATTR_DOCURL));
			}
			if (attributes.getValue(ATTR_CATEGORY) != null) {
				config.setCategory(attributes.getValue(ATTR_CATEGORY));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (TAG_DESCRIPTION.equals(name)) {
			config.setDescription(currentContent.toString());
		} else if (TAG_MANUAL.equals(name)) {
			config.setManual(currentContent.toString());
		} else if (TAG_EXECUTABLE_PATH.equals(name)) {
			config.setExecutablePath(currentContent.toString());
		} else if (TAG_EXECUTABLE_NAME.equals(name)) {
			config.setExecutableName(currentContent.toString());
		}
	}
}
