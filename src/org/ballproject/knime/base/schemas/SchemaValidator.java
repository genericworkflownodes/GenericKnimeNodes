package org.ballproject.knime.base.schemas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class SchemaValidator
{
	
	private List<InputStream> schemas = new ArrayList<InputStream>();
	
	public void addSchema(InputStream in)
	{
		schemas.add(in);
	}
	
	private Source[] getSchemaSources()
	{
		Source[] ret = new Source[schemas.size()];
		int idx = 0;
		for(InputStream in: schemas)
		{
			ret[idx++] = new StreamSource(in);
		}
		return ret;
	}

	public boolean validates(InputStream xmlstream)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		SimpleErrorHandler errorHandler = new SimpleErrorHandler();
		
		try
		{
			factory.setSchema(schemaFactory.newSchema(getSchemaSources()));

			SAXParser parser = factory.newSAXParser();

			SAXReader reader = new SAXReader(parser.getXMLReader());
			reader.setValidation(false);

			reader.setErrorHandler(errorHandler);

			reader.read(xmlstream);
		}
		catch(SAXException e)
		{
			throw new RuntimeException(e);
		} 
		catch (DocumentException e)
		{
			throw new RuntimeException(e);
		} 
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
		
		if (!errorHandler.isValid())
		{
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) throws FileNotFoundException, Exception
	{
		SchemaValidator val = new SchemaValidator();
		val.addSchema(SchemaProvider.class.getResourceAsStream("Param_1_3.xsd"));
		val.addSchema(SchemaProvider.class.getResourceAsStream("TTD.xsd"));
		
		val.validates(new FileInputStream("/tmp/descriptors/GridBuilder.ttd"));
		
		System.out.println("validates");
	}
}
