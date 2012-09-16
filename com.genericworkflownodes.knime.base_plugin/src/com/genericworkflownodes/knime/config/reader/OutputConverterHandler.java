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
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.genericworkflownodes.knime.config.NodeConfiguration;
import com.genericworkflownodes.knime.outputconverter.config.Converter;
import com.genericworkflownodes.knime.outputconverter.config.OutputConverters;

/**
 * Content handler for the output mapping element.
 * 
 * @author aiche
 */
public class OutputConverterHandler extends DefaultHandler {

	private static String ATTR_CLASS = "class";
	private static String ATTR_REF = "ref";

	private static String ATTR_NAME = "name";
	private static String ATTR_VALUE = "value";

	private static String TAG_CONVERTER = "converter";
	private static String TAG_OUTPUT_CONVERTERS = "outputConverters";
	private static String TAG_CONVERTER_PROPERTY = "converterProperty";

	/**
	 * The parent handler that invoked this handler for a sub tree of the XML
	 * document.
	 */
	private CTDHandler parentHandler;

	/**
	 * The {@link XMLReader} that processes the entire document.
	 */
	private XMLReader xmlReader;

	/**
	 * The parsed converters.
	 */
	private OutputConverters converters;

	/**
	 * The currently processed converter.
	 */
	private Converter currentConverter;

	/**
	 * The {@link NodeConfiguration} that will be filled while parsing the
	 * document.
	 */
	private NodeConfiguration config;

	/**
	 * C'tor.
	 * 
	 * @param xmlReader
	 *            The xml reader of the global document.
	 * @param parentHandler
	 *            The parent handler for the global document.
	 * @param config
	 *            The {@link NodeConfiguration} that will be filled while
	 *            parsing the document.
	 */
	public OutputConverterHandler(XMLReader xmlReader,
			CTDHandler parentHandler, NodeConfiguration config) {
		this.xmlReader = xmlReader;
		this.parentHandler = parentHandler;
		converters = new OutputConverters();
		this.config = config;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (TAG_CONVERTER.equals(name)) {
			currentConverter = new Converter();
			currentConverter.setClazz(attributes.getValue(ATTR_CLASS));
			currentConverter.setRef(attributes.getValue(ATTR_REF));
		} else if (TAG_CONVERTER_PROPERTY.equals(name)) {
			String propertyName = attributes.getValue(ATTR_NAME);
			String propertyValue = attributes.getValue(ATTR_VALUE);
			currentConverter.getConverterProperties().setProperty(propertyName,
					propertyValue);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (TAG_OUTPUT_CONVERTERS.equals(name)) {
			// return to parent scope
			config.setOutputConverters(converters);
			xmlReader.setContentHandler(parentHandler);
		} else if (TAG_CONVERTER.equals(name)) {
			converters.addConverter(currentConverter);
		}
	}
}
