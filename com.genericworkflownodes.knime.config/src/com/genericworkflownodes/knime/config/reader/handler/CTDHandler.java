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

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.NodeConfiguration;

/**
 * The main {@link ContentHandler} for the CTD.
 * 
 * @author aiche
 */
public class CTDHandler extends DefaultHandler {

    private static final String ATTR_NAME = "name";
    private static final String ATTR_VERSION = "version";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_MANUAL = "manual";
    private static final String ATTR_DOCURL = "docurl";
    private static final String ATTR_CATEGORY = "category";
    private static final String TAG_CLI = "cli";
    private static final String TAG_RELOCATORS = "relocators";
    private static final String TAG_PARAMETERS = "PARAMETERS";
    private static final String TAG_EXECUTABLE_NAME = "executableName";
    private static final String TAG_EXECUTABLE_PATH = "executablePath";
    private static final String TAG_TOOL = "tool";
    private static final String TAG_CITATIONS = "citations";

    /**
     * The {@link INodeConfiguration} generated while parsing the CTD document.
     */
    private NodeConfiguration m_config;

    /**
     * The content contained in the current xml tag.
     */
    private StringBuilder m_currentContent;

    /**
     * The {@link XMLReader} that uses this content handler.
     */
    private XMLReader m_xmlReader;

    /**
     * C'tor.
     * 
     * @param xmlReader
     *            The {@link XMLReader} that uses this content handler.
     */
    public CTDHandler(XMLReader xmlReader) {
        m_xmlReader = xmlReader;
        m_currentContent = new StringBuilder();
        m_config = new NodeConfiguration();
    }

    /**
     * Access the parsed node configuration.
     * 
     * @return The processed node configuration.
     */
    public INodeConfiguration getNodeConfiguration() {
        return m_config;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        m_currentContent.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        m_currentContent.setLength(0);
        if (TAG_PARAMETERS.equals(name)) {
            m_xmlReader.setContentHandler(new ParamHandler(m_xmlReader, this,
                    m_config));
        } else if (TAG_RELOCATORS.equals(name)) {
            m_xmlReader.setContentHandler(new RelocatorHandler(m_xmlReader,
                    this, m_config));
        } else if (TAG_CLI.equals(name)) {
            m_xmlReader.setContentHandler(new CLIElementHandler(m_xmlReader,
                    this, m_config));
        } else if (TAG_CITATIONS.equals(name)) {
            m_xmlReader.setContentHandler(new CitationHandler(m_xmlReader,
                    this, m_config));
        } else if (TAG_TOOL.equals(name)) {
            // root tag -> parse out the attribute values
            m_config.setName(attributes.getValue(ATTR_NAME));
            m_config.setVersion(attributes.getValue(ATTR_VERSION));
            if (attributes.getValue(ATTR_DOCURL) != null) {
                m_config.setDocUrl(attributes.getValue(ATTR_DOCURL));
            }
            if (attributes.getValue(ATTR_CATEGORY) != null) {
                m_config.setCategory(attributes.getValue(ATTR_CATEGORY));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (TAG_DESCRIPTION.equals(name)) {
            m_config.setDescription(m_currentContent.toString());
        } else if (TAG_MANUAL.equals(name)) {
            m_config.setManual(m_currentContent.toString());
        } else if (TAG_EXECUTABLE_PATH.equals(name)) {
            m_config.setExecutablePath(m_currentContent.toString());
        } else if (TAG_EXECUTABLE_NAME.equals(name)) {
            m_config.setExecutableName(m_currentContent.toString());
        }
    }
}
