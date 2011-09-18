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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.ballproject.knime.base.mime.MIMEtype;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleListParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerListParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringChoiceParameter;
import org.ballproject.knime.base.parameter.StringListParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.ballproject.knime.base.port.Port;
import org.ballproject.knime.base.schemas.SchemaProvider;
import org.ballproject.knime.base.schemas.SimpleErrorHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class CTDNodeConfigurationReader implements NodeConfigurationReader
{
	
	private static Logger log = Logger.getLogger(CTDNodeConfigurationReader.class.getCanonicalName());
	
	private Document doc;
	private DefaultNodeConfiguration config = new DefaultNodeConfiguration();
	
	public CTDNodeConfigurationReader()
	{	
	}
	
	
	protected String SECTION_NODE_NAME = "NODE";
	protected String INPUTFILE_TAG     = "input file";
	protected String OUTPUTFILE_TAG    = "output file";
	
	protected Set<String> captured_ports = new HashSet<String>();
	
	private static List<Port>    in_ports;
	private static List<Port>    out_ports;
	
	private void readPorts() throws Exception
	{
		in_ports  = new ArrayList<Port>();
		out_ports = new ArrayList<Port>();
		
		Node node  = doc.selectSingleNode("/tool/PARAMETERS");
		Element root = (Element) node;
		processIOPorts(root);
		
		config.setInports((Port[]) in_ports.toArray(new Port[in_ports.size()]));
		config.setOutports((Port[]) out_ports.toArray(new Port[out_ports.size()]));
	}
	
	
	@SuppressWarnings("unchecked")
	public void processIOPorts(Element root) throws Exception
	{
		List<Node> items = root.selectNodes("//ITEM[contains(@tags,'"+OUTPUTFILE_TAG+"')]");
		for(Node n: items)
		{
			createPortFromNode(n, false);
		}
		items = root.selectNodes("//ITEM[contains(@tags,'"+INPUTFILE_TAG+"')]");
		for(Node n: items)
		{
			createPortFromNode(n, false);
		}
		items = root.selectNodes("//ITEMLIST[contains(@tags,'"+INPUTFILE_TAG+"')]");
		for(Node n: items)
		{
			createPortFromNode(n, true);
		}

	}
	
	@SuppressWarnings("unchecked")
	public void readParameters() throws Exception
	{
		Node root  = doc.selectSingleNode("/tool/PARAMETERS");
		List<Node> items = root.selectNodes("//ITEM[not(contains(@tags,'"+OUTPUTFILE_TAG+"')) and not(contains(@tags,'"+INPUTFILE_TAG+"'))]");
		for(Node n: items)
		{
			processItem(n);
		}
		items = root.selectNodes("//ITEMLIST[not(contains(@tags,'"+OUTPUTFILE_TAG+"')) and not(contains(@tags,'"+INPUTFILE_TAG+"'))]");
		for(Node n: items)
		{
			processMultiItem(n);
		}
	}
	
	public String getPath(Node n)
	{
		
		List<String> path_nodes = new ArrayList<String>();
		while(n!=null&&!n.getName().equals("PARAMETERS"))
		{
			path_nodes.add(n.valueOf("@name"));
			n = n.getParent();
		}
		
		Collections.reverse(path_nodes);
		
		
		String ret = "";
		int N = path_nodes.size();
		for(int i=0;i<N;i++)
		{
			if(i==N-1)
				ret+=path_nodes.get(i);
			else
				ret+=path_nodes.get(i)+".";
		}
		return ret;
	}
			 		
	private void createPortFromNode(Node node, boolean multi) throws Exception
	{
		Element elem = (Element) node;
		
		String name   = node.valueOf("@name");
		String descr  = node.valueOf("@description");
		String tags   = node.valueOf("@tags");
		
		if(name.equals("write_ini")||name.equals("write_par")||name.equals("par")||name.equals("help"))
		{
			return;
		}
		
		Port port = new Port();
		
		
		port.setMultiFile(multi);
		
		if(tags.contains(INPUTFILE_TAG)||tags.contains(OUTPUTFILE_TAG))
		{
			if(elem.attributeValue("supported_formats")==null)
			{
				throw new Exception("i/o item with missing attribute supported_formats detected");
			}
			
			String  formats  = node.valueOf("@supported_formats");
			
			String[] toks2   = formats.split(",");

			String path = getPath(node); 
			port.setName(path);
			
			port.setDescription(descr);
			
			boolean optional = true;
			if(tags.contains("mandatory")||tags.contains("required"))
				optional = false;
			else
				optional = true;
			port.setOptional(optional);
			
			
			for(String mt : toks2)
			{
				port.addMimeType(new MIMEtype(mt.trim()));
			}
			
		}
		if(tags.contains(OUTPUTFILE_TAG))
		{
			out_ports.add(port);
			captured_ports.add(port.getName());
		}
		if(tags.contains(INPUTFILE_TAG))
		{
			in_ports.add(port);
			captured_ports.add(port.getName());
		}		
		
	}
	
	public void processItem(Node elem) throws Exception
	{
		String name = elem.valueOf("@name");
		
		String path = getPath(elem);
		
		if(captured_ports.contains(path))
			return;
		
		if(name.equals("write_ini")||name.equals("write_par")||name.equals("par")||name.equals("help"))
		{
			return;
		}
		
		Parameter<?> param = getParameterFromNode(elem);
		config.addParameter(path, param);
	}
	
	public void processMultiItem(Node elem) throws Exception
	{
		String name = elem.valueOf("@name");
		
		String path = getPath(elem);
		
		if(captured_ports.contains(path))
			return;
		
		if(name.equals("write_ini")||name.equals("write_par")||name.equals("par")||name.equals("help"))
		{
			return;
		}
		
		Parameter<?> param = getMultiParameterFromNode(elem);
		config.addParameter(path, param);
	}
	
	private void readDescription() throws Exception
	{
		Node   node  = doc.selectSingleNode("/tool");
		if(node==null)
			throw new Exception("CTD has no root named tool");
		String lstatus  = node.valueOf("@status");  
		if(lstatus!=null && lstatus.equals(""))
			throw new Exception("CTD has no status");
		config.setStatus(lstatus);
		
		node  = doc.selectSingleNode("/tool/name");
		if(node==null)
			throw new Exception("CTD has no tool name");
		String name  = node.valueOf("text()");  
		if(name.equals(""))
			throw new Exception("CTD has no tool name");
		config.setName(name);
		
		
		
		node  = doc.selectSingleNode("/tool/description");
		String sdescr  = "";
		if(node!=null)
			sdescr  = node.valueOf("text()");
		config.setDescription(sdescr);
		
		node  = doc.selectSingleNode("/tool/path");
		String spath  = "";
		if(node!=null)
			spath  = node.valueOf("text()");
		config.setCommand(spath);
		
		node  = doc.selectSingleNode("/tool/manual");
		String ldescr = "";
		if(node!=null)
			ldescr  = node.valueOf("text()");
		config.setManual(ldescr);
		
		node  = doc.selectSingleNode("/tool/version");
		String lversion = "";
		if(node!=null)
			lversion  = node.valueOf("text()");
		config.setVersion(lversion);
		
		node  = doc.selectSingleNode("/tool/docurl");
		String docurl  = "";
		if(node!=null)
			docurl = node.valueOf("text()");  
		config.setDocUrl(docurl);
		
		node  = doc.selectSingleNode("/tool/category");
		String cat = "";
		if(node!=null)
			cat = node.valueOf("text()");  
		config.setCategory(cat);
	}
	
	private Parameter<?> getParameterFromNode(Node node) throws Exception
	{
		Parameter<?> ret = null;
		String type   = node.valueOf("@type");
		String name   = node.valueOf("@name");
		String value  = node.valueOf("@value");
		String restrs = node.valueOf("@restrictions");
		String descr  = node.valueOf("@description");
		String tags   = node.valueOf("@tags");
		
		if (type.toLowerCase().equals("double")||type.toLowerCase().equals("float"))
		{
			ret = processDoubleParameter(name, value, restrs, tags);
		}
		else
		{
			if(type.toLowerCase().equals("int"))
			{
				ret = processIntParameter(name, value, restrs, tags);
			}
			else
			{
				if(type.toLowerCase().equals("string"))
				{
					ret = processStringParameter(name, value, restrs, tags);
				}
			}
		}
		
		ret.setDescription(descr);
		Set<String> tagset = tokenSet(tags);
		
		if(tagset.contains("mandatory")||tagset.contains("required"))
			ret.setIsOptional(false);
		
		return ret;
	}
	
	private Parameter<?> getMultiParameterFromNode(Node node) throws Exception
	{
		String type   = node.valueOf("@type");
		String name   = node.valueOf("@name");
		String value  = node.valueOf("@value");
		String restrs = node.valueOf("@restrictions");
		String descr  = node.valueOf("@description");
		String tags   = node.valueOf("@tags");
		
		Set<String> tagset = tokenSet(tags);
		
		@SuppressWarnings("unchecked")
		List<Node>   subnodes = node.selectNodes("LISTITEM");
		
		List<String> values   = new ArrayList<String>(); 
		for(Node n: subnodes)
		{
			values.add(n.valueOf("@value"));
		}
		
		Parameter<?> param = null;
		

		if (type.toLowerCase().equals("double")||type.toLowerCase().equals("float"))
		{
			param = processDoubleListParameter(name, values, restrs, tags);
		}
		else
		{
			if(type.toLowerCase().equals("int"))
			{
				param = processIntListParameter(name, values, restrs, tags);
			}
			else
			{
				if(type.toLowerCase().equals("string"))
				{
					param = processStringListParameter(name, values, restrs, tags);
				}
			}
		}
		
		param.setDescription(descr);
		
		if(tagset.contains("mandatory")||tagset.contains("required"))
			param.setIsOptional(false);
		
		return param;
	}

	private Parameter<?> processStringListParameter(String name, List<String> values, String restrs, String tags)
	{
		return new StringListParameter(name,values);
	}


	private Parameter<?> processIntListParameter(String name, List<String> values, String restrs, String tags)
	{
		List<Integer> vals = new ArrayList<Integer>();
		for(String cur_val: values)
		{
			vals.add(Integer.parseInt(cur_val));
		}
		
		IntegerListParameter ret = new IntegerListParameter(name,vals);
		
		Integer[] bounds = new Integer[2];
		getIntegerBoundsFromRestrictions( restrs, bounds);
		ret.setLowerBound(bounds[0]);
		ret.setUpperBound(bounds[1]);
		
		return ret;
	}


	private Parameter<?> processDoubleListParameter(String name, List<String> values, String restrs, String tags)
	{
		List<Double> vals = new ArrayList<Double>();
		for(String cur_val: values)
		{
			vals.add(Double.parseDouble(cur_val));
		}
		
		DoubleListParameter ret = new DoubleListParameter(name,vals);
		
		Double[] bounds = new Double[2];
		getDoubleBoundsFromRestrictions( restrs, bounds);
		ret.setLowerBound(bounds[0]);
		ret.setUpperBound(bounds[1]);
		
		return ret;
	}


	private void getDoubleBoundsFromRestrictions(String restrs, Double[] bounds)
	{
		Double UB = Double.POSITIVE_INFINITY;
		Double LB = Double.NEGATIVE_INFINITY;
		
		if(restrs.equals(""))
		{
			bounds[0] = LB;
			bounds[1] = UB;
			return;
		}
		
		String[] toks = restrs.split(":");
		if (toks.length != 0)
		{				
			if (toks[0].equals(""))
			{
				// upper bounded only
				double ub;
				try
				{
					ub = Double.parseDouble(toks[1]);
				}
				catch (NumberFormatException e)
				{
					throw new RuntimeException(e);
				}
				UB = ub;
			}
			else
			{
				// lower and upper bounded
				if (toks.length == 2)
				{
					double lb;
					double ub;
					try
					{
						lb = Double.parseDouble(toks[0]);
						ub = Double.parseDouble(toks[1]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException(e);
					}
					LB = lb;
					UB = ub;
				}
				else
				{
					// lower bounded only
					double lb;
					try
					{
						lb = Double.parseDouble(toks[0]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException(e);
					}
					LB = lb;
				}
			}
		}
		bounds[0] = LB;
		bounds[1] = UB;		
	}
	
	private Parameter<?> processDoubleParameter(String name, String value, String restrs, String tags) throws Exception
	{
		DoubleParameter retd = new DoubleParameter(name, value);
		Double[] bounds = new Double[2];
		getDoubleBoundsFromRestrictions( restrs, bounds);
		retd.setLowerBound(bounds[0]);
		retd.setUpperBound(bounds[1]);
		return retd;
	}
	
	private static Set<String> tokenSet(String s)
	{
		Set<String> ret = new HashSet<String>();
		String[] toks = s.split(",");
		for(String tok: toks)
			ret.add(tok);
		return ret;
	}

	private void getIntegerBoundsFromRestrictions(String restrs, Integer[] bounds)
	{
		Integer UB = Integer.MAX_VALUE;
		Integer LB = Integer.MIN_VALUE;
	
		if(restrs.equals(""))
		{
			bounds[0] = LB;
			bounds[1] = UB;
			return;
		}

		
		String[] toks = restrs.split(":");
		if (toks.length != 0)
		{				
			if (toks[0].equals(""))
			{
				// upper bounded only
				int ub;
				try
				{
					ub = Integer.parseInt(toks[1]);
				}
				catch (NumberFormatException e)
				{
					throw new RuntimeException(e);
				}
				UB = ub;
			}
			else
			{
				// lower and upper bounded
				if (toks.length == 2)
				{
					int lb;
					int ub;
					try
					{
						lb = Integer.parseInt(toks[0]);
						ub = Integer.parseInt(toks[1]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException(e);
					}
					LB = lb;
					UB = ub;
				}
				else
				{
					// lower bounded only
					int lb;
					try
					{
						lb = Integer.parseInt(toks[0]);
					}
					catch (NumberFormatException e)
					{
						throw new RuntimeException(e);
					}
					LB = lb;
				}
			}
		}
		bounds[0] = LB;
		bounds[1] = UB;
	}
	
	private Parameter<?> processIntParameter(String name, String value, String restrs, String tags) throws Exception
	{
		IntegerParameter reti = new IntegerParameter(name, value);
		Integer[] bounds = new Integer[2];
		getIntegerBoundsFromRestrictions(restrs, bounds);
		reti.setLowerBound(bounds[0]);
		reti.setUpperBound(bounds[1]);
		return reti;
	}

	private Parameter<?> processStringParameter(String name, String value, String restrs, String tags) throws Exception
	{
		Parameter<?> rets = null;

		String[] toks = restrs.split(",");

		if(restrs.length()>0)
		{
			if( (toks[0].equals("true")&&toks[1].equals("false")) || (toks[0].equals("false")&&toks[1].equals("true")) )
			{
				rets = new BoolParameter(name, value);
			}
			else
			{
				rets = new StringChoiceParameter(name, toks);
				((StringChoiceParameter) rets).setValue(value);
			}
		}
		else
		{
			rets = new StringParameter(name, value);
		}
		
		return rets;
	}

	@Override
	public NodeConfiguration read(InputStream xmlstream) throws Exception
	{	
		SAXParserFactory factory       = SAXParserFactory.newInstance();
		SchemaFactory    schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		
		factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource(SchemaProvider.class.getResourceAsStream("TTD.xsd")), new StreamSource(SchemaProvider.class.getResourceAsStream("Param_1_3.xsd"))}));
		
		SAXParser parser = factory.newSAXParser();

		SAXReader reader = new SAXReader(parser.getXMLReader());
		reader.setValidation(false);
		
		SimpleErrorHandler errorHandler = new SimpleErrorHandler();
					
		reader.setErrorHandler(errorHandler);
		
		doc = reader.read(xmlstream);
		
		if(!errorHandler.isValid())
		{
			System.err.println(errorHandler.getErrorReport());
			throw new Exception("TTD file is not valid !");
		}
		
		readPorts();
		readParameters();
		readDescription();
		
		config.setXml(doc.asXML());
		
		return config;
	}
}
