/*
 * Copyright (c) 2011, Marc Röttig.
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

package org.ballproject.knime.base.schemas;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler {
	private boolean valid = true;
	private StringBuffer errors = new StringBuffer();

	public boolean isValid() {
		return valid;
	}

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