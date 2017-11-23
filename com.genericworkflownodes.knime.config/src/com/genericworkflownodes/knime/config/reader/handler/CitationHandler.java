package com.genericworkflownodes.knime.config.reader.handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.genericworkflownodes.knime.config.NodeConfiguration;
import com.genericworkflownodes.knime.config.citation.Citation;

public class CitationHandler extends DefaultHandler {

	    /**
	     * The logger used to indicate problems.
	     */
	    private static final Logger LOG = Logger.getLogger(ParamHandler.class
	            .getCanonicalName());

	    
	    private static final String TAG_CITATIONS = "citations"; // includes single citations
	    private static final String TAG_CITATION = "citation"; // doi, url
	    private static final String ATTR_DOI = "doi";
	    private static final String ATTR_URL = "url";

		

	    /**
	     * Store the current list entries to finally add them to the created list
	     * parameter.
	     */
	    private List<Citation> m_listCitations;

	    /**
	     * The parent handler that invoked this handler for a sub tree of the XML
	     * document.
	     */
	    private CTDHandler m_parentHandler;

	    /**
	     * The XMLReader that processes the entire document.
	     */
	    private XMLReader m_xmlReader;

	    /**
	     * The NodeConfiguration that will be filled while parsing the document.
	     */
	    private NodeConfiguration m_config;

	    /**
	     * C'tor accepting the parent handler and the xml reader.
	     * 
	     * @param xmlReader
	     *            The xml reader of the global document.
	     * @param parentHandler
	     *            The parent handler for the global document.
	     * @param config
	     *            The NodeConfiguration that will be filled while parsing the
	     *            document.
	     */
	    public CitationHandler(XMLReader xmlReader, CTDHandler parentHandler,
	            NodeConfiguration config) {
	        m_xmlReader = xmlReader;
	        m_parentHandler = parentHandler;
	        m_config = config;
	        m_listCitations = new ArrayList<Citation>();
	    }

	    @Override
	    public void startElement(String uri, String localName, String name,
	            Attributes attributes) throws SAXException {
	        try {
	            if (TAG_CITATION.equals(name)) {
	                handleCitation(attributes);
	            } 
	        } catch (Exception e) {
	            LOG.log(Level.SEVERE, e.getMessage());
	        }
	    }

	    private void handleCitation(Attributes attributes) {
	    	try {
	    		String urlString = attributes.getValue(ATTR_URL);
	    		if (urlString == null || urlString.isEmpty()) {
	    			m_listCitations.add(new Citation(attributes.getValue(ATTR_DOI)));
	    		} else {
	    			m_listCitations.add(new Citation(attributes.getValue(ATTR_DOI), new URL(urlString)));
	    		}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    @Override
	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	    	if (TAG_CITATIONS.equals(name)) {
	    		m_config.setCitations(m_listCitations);
	            m_xmlReader.setContentHandler(m_parentHandler);
	        }
	    }
}
