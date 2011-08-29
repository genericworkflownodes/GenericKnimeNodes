package org.ballproject.knime.base.schemas;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler
{
	private boolean valid = true;
	private StringBuffer errors = new StringBuffer();
	
	public boolean isValid()
	{
		return valid;
	}
	
	public String getErrorReport()
	{
		return errors.toString();
	}
	
	@Override
	public void error(SAXParseException ex) throws SAXException
	{	
		errors.append("Line "+ex.getLineNumber()+" "+ex.getMessage()+System.getProperty("line.separator"));
		valid = false;
	}

	@Override
	public void fatalError(SAXParseException ex) throws SAXException
	{
		errors.append("Line "+ex.getLineNumber()+" "+ex.getMessage()+System.getProperty("line.separator"));
		valid = false;
	}

	@Override
	public void warning(SAXParseException ex) throws SAXException
	{
	}
}