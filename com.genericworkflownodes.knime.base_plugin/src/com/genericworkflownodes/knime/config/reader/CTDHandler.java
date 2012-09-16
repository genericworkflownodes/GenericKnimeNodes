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
package com.genericworkflownodes.knime.config.reader;

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

	private static String TAG_NAME = "name";
	private static String TAG_VERSION = "version";
	private static String TAG_DESCRIPTION = "description";
	private static String TAG_MANUAL = "manual";
	private static String TAG_DOCURL = "docurl";
	private static String TAG_CATEGORY = "category";
	private static String TAG_CLI = "cli";
	private static String TAG_OUTPUT_CONVERTERS = "outputConverters";
	private static String TAG_PARAMETERS = "PARAMETERS";
	private static String TAG_EXECUTABLE_NAME = "executableName";
	private static String TAG_EXECUTABLE_PATH = "executablePath";

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
		} else if (TAG_OUTPUT_CONVERTERS.equals(name)) {
			xmlReader.setContentHandler(new OutputConverterHandler(xmlReader,
					this, config));
		} else if (TAG_CLI.equals(name)) {
			xmlReader.setContentHandler(new CLIElementHandler(xmlReader, this,
					config));
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (TAG_NAME.equals(name)) {
			config.setName(currentContent.toString());
		} else if (TAG_VERSION.equals(name)) {
			config.setVersion(currentContent.toString());
		} else if (TAG_DESCRIPTION.equals(name)) {
			config.setDescription(currentContent.toString());
		} else if (TAG_MANUAL.equals(name)) {
			config.setManual(currentContent.toString());
		} else if (TAG_DOCURL.equals(name)) {
			config.setDocUrl(currentContent.toString());
		} else if (TAG_CATEGORY.equals(name)) {
			config.setCategory(currentContent.toString());
		} else if (TAG_EXECUTABLE_PATH.equals(name)) {
			config.setExecutablePath(currentContent.toString());
		} else if (TAG_EXECUTABLE_NAME.equals(name)) {
			config.setExecutableName(currentContent.toString());
		} else if (TAG_OUTPUT_CONVERTERS.equals(name)) {
			// will not happen since we handle this element in sub handler
		} else if (TAG_PARAMETERS.equals(name)) {
			// will not happen since we handle this element in sub handler
		}
	}
}
