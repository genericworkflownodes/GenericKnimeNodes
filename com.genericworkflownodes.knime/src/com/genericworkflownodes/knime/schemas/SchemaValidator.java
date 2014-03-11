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
package com.genericworkflownodes.knime.schemas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * 
 * Helper class for xml schema validation.
 * 
 * @author roettig
 */
public class SchemaValidator {

    /**
     * List of schemas to validate.
     */
    private List<InputStream> schemas;

    /**
     * Member to store the error report.
     */
    private String errorReport;

    /**
     * C'tor.
     */
    public SchemaValidator() {
        schemas = new ArrayList<InputStream>();
        errorReport = "";
    }

    /**
     * Add a schema to validate.
     * 
     * @param in
     *            Input stream pointing to the schema.
     */
    public void addSchema(InputStream in) {
        schemas.add(in);
    }

    /**
     * Access all schema sources.
     * 
     * @return The schema sources.
     */
    private Source[] getSchemaSources() {
        Source[] ret = new Source[schemas.size()];
        int idx = 0;
        for (InputStream in : schemas) {
            ret[idx++] = new StreamSource(in);
        }
        return ret;
    }

    /**
     * Validate the given file against the stored schemata.
     * 
     * @param filename
     *            The file to validate.
     * @return True if the file is valid, false otherwise.
     */
    public boolean validates(String filename) {
        boolean ret = true;
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(filename);
            ret = validates(fin);
            fin.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * Validate the given xml stream against the stored schemata.
     * 
     * @param xmlstream
     *            The stream to validate.
     * @return True if the file is valid, false otherwise.
     */
    public boolean validates(InputStream xmlstream) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance("http://www.w3.org/2001/XMLSchema");

        SimpleErrorHandler errorHandler = new SimpleErrorHandler();

        try {
            factory.setSchema(schemaFactory.newSchema(getSchemaSources()));

            SAXParser parser = factory.newSAXParser();

            SAXReader reader = new SAXReader(parser.getXMLReader());
            reader.setValidation(false);

            reader.setErrorHandler(errorHandler);

            reader.read(xmlstream);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        if (!errorHandler.isValid()) {
            errorReport = errorHandler.getErrorReport();
            return false;
        }

        return true;
    }

    /**
     * Accessor for the error report.
     * 
     * @return The error report.
     */
    public String getErrorReport() {
        return errorReport;
    }
}
