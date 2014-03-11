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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Errrorhandler for the schema validator.
 * 
 * @author roettig
 */
public class SimpleErrorHandler implements ErrorHandler {

    /**
     * Indicates if the document is valid.
     */
    private boolean valid;

    /**
     * {@link StringBuffer} to store the reported errors.
     */
    private StringBuffer errors;

    /**
     * C'tor.
     */
    public SimpleErrorHandler() {
        valid = true;
        errors = new StringBuffer();
    }

    /**
     * Returns true if the document is valid, false otherwise.
     * 
     * @return Returns true if the document is valid, false otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns the complete error report of the validation process.
     * 
     * @return the error report.
     */
    public String getErrorReport() {
        return errors.toString();
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        errors.append("Line " + ex.getLineNumber() + " " + ex.getMessage()
                + System.getProperty("line.separator"));
        valid = false;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        errors.append("Line " + ex.getLineNumber() + " " + ex.getMessage()
                + System.getProperty("line.separator"));
        valid = false;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
    }
}