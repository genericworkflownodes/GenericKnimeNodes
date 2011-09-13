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

package org.ballproject.knime.base.config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class CTDNodeConfigurationWriter
{
	
	private Document doc;
	
	public CTDNodeConfigurationWriter(String xml)
	{
		SAXReader reader = new SAXReader();	
		try
		{
			doc = reader.read(new StringReader(xml) ) ;
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setParameterValue(String name, String value)
	{
		Node    node  = doc.selectSingleNode("//ITEM[@name='"+name+"']");
		if(node==null)
			return;
		Element elem  = (Element) node;
		elem.addAttribute("value", value);
	}
		
	public void setParameterValue2(String name, String value)
	{
		String[] toks = name.split("\\.");
		String query = "/tool/PARAMETERS/";
		for(int i=0;i<toks.length-1;i++)
		{
			query+="NODE[@name='"+toks[i]+"']/";
		}
		query+="ITEM[@name='"+toks[toks.length-1]+"']";
		
		Node    node  = doc.selectSingleNode(query);
		if(node==null)
			return;
		Element elem  = (Element) node;
		elem.addAttribute("value", value);
	}
	
	public void write(String filename) throws IOException
	{
		OutputFormat format = OutputFormat.createPrettyPrint();
		
		XMLWriter writer = new XMLWriter( new FileWriter(filename) , format );
        writer.write( doc );

		writer.close();
	}
}
