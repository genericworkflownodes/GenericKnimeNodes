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
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.genericworkflownodes.knime.config.NodeConfiguration;
import com.genericworkflownodes.knime.relocator.Relocator;

/**
 * SAX Handler for the relocator part of the CTD.
 * 
 * @author aiche
 */
public class RelocatorHandler extends DefaultHandler {

    private static final String ATTR_REFERENCE = "reference";
    private static final String ATTR_PATTERN = "pattern";

    private static final String ELEM_RELOCATORS = "relocators";

    /**
     * The parent handler that invoked this handler for a sub tree of the XML
     * document.
     */
    private CTDHandler m_parentHandler;

    /**
     * The {@link XMLReader} that processes the entire document.
     */
    private XMLReader m_xmlReader;

    /**
     * The {@link NodeConfiguration} that will be filled while parsing the
     * document.
     */
    private NodeConfiguration m_config;

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
    public RelocatorHandler(XMLReader xmlReader, CTDHandler parentHandler,
            NodeConfiguration config) {
        m_xmlReader = xmlReader;
        m_parentHandler = parentHandler;
        m_config = config;
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        // only relocator can occur
        String reference = attributes.getValue(ATTR_REFERENCE);
        String pattern = attributes.getValue(ATTR_PATTERN);

        m_config.getRelocators().add(new Relocator(reference, pattern));
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        // get back to the parent handler
        if (ELEM_RELOCATORS.equals(name)) {
            m_xmlReader.setContentHandler(m_parentHandler);
        }
    }
}
