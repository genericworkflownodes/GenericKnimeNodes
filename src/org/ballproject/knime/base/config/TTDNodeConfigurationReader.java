package org.ballproject.knime.base.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.ballproject.knime.base.parameter.BoolParameter;
import org.ballproject.knime.base.parameter.DoubleParameter;
import org.ballproject.knime.base.parameter.IntegerParameter;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.parameter.StringChoiceParameter;
import org.ballproject.knime.base.parameter.StringParameter;
import org.ballproject.knime.base.port.MIMEtype;
import org.ballproject.knime.base.port.Port;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class TTDNodeConfigurationReader implements NodeConfigurationReader
{
	
	private static Logger log = Logger.getLogger(TTDNodeConfigurationReader.class.getCanonicalName());
	
	private Document doc;
	private DefaultNodeConfiguration config = new DefaultNodeConfiguration();
	
	public TTDNodeConfigurationReader()
	{	
	}
	
	
	protected String SECTION_NODE_NAME = "NODE";
	protected String INPUTFILE_TAG     = "input file";
	protected String OUTPUTFILE_TAG    = "output file";
	
	protected Set<String> captured_ports = new HashSet<String>();
	
	private void readPorts() throws Exception
	{
		List<Node>    nodes     = doc.selectNodes("//ITEM");
		List<Port>    in_ports  = new ArrayList<Port>();
		List<Port>    out_ports = new ArrayList<Port>();
		
		for(Node node : nodes)
		{
			Element element = (Element) node;
			String  name    = element.valueOf("@name");
			String  tags    = element.valueOf("@tags");
			String  descr   = element.valueOf("@description");
			
			if(name.equals("write_ini")||name.equals("write_par")||name.equals("par")||name.equals("help"))
			{
				continue;
			}
			
			Port port = new Port();
			
			if(tags.contains(INPUTFILE_TAG)||tags.contains(OUTPUTFILE_TAG))
			{				
				String  formats  = element.valueOf("@supported_formats");
				
				MIMEtype mtype   = null;
				String[] toks    = tags.split(",");
				String[] toks2   = formats.split(",");

				port.setName(name);
				port.setDescription(descr);
				
				boolean optional = true;
				if(tags.contains("mandatory"))
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
				captured_ports.add(name);
			}
			if(tags.contains(INPUTFILE_TAG))
			{
				in_ports.add(port);
				captured_ports.add(name);
			}		
		}
		config.setInports((Port[]) in_ports.toArray(new Port[in_ports.size()]));
		config.setOutports((Port[]) out_ports.toArray(new Port[out_ports.size()]));
	}
	
	private void readParameters() throws Exception
	{
		Node node  = doc.selectSingleNode("/tool/PARAMETERS");
		Element root = (Element) node;
		processParameters(root);
	}
	
	public void processParameters(Element root) throws Exception
	{
		String prefix = "";
		for ( Iterator<Node> i = root.elementIterator(); i.hasNext(); ) 
        {
            Element elem = (Element) i.next();
            if(elem.getName().equals("NODE"))
            	iterNode(prefix, elem);
            else
            	if(elem.getName().equals("ITEM"))
            		processItem(prefix, elem);
        }
	}
	
	public void iterNode(String prefix, Element root) throws Exception
	{
		String pref = prefix + (prefix.equals("")?"":".") + root.attributeValue("name");
		//System.out.println("visiting "+root.attributeValue("name")+"  prefix="+prefix);
		for ( Iterator<Node> i = root.elementIterator(); i.hasNext(); ) 
        {
            Element elem = (Element) i.next();
            if(elem.getName().equals("NODE"))
            	iterNode(pref, elem);
            else
            	if(elem.getName().equals("ITEM"))
            		processItem(pref, elem);
        }
	}
	
	public void processItem(String prefix, Element elem) throws Exception
	{
		//System.out.println("	processing ITEM "+prefix+"."+elem.attributeValue("name"));
		String name = elem.attributeValue("name");
		
		if(captured_ports.contains(name))
			return;
		
		if(name.equals("write_ini")||name.equals("write_par")||name.equals("par")||name.equals("help"))
		{
			return;
		}
		
		Parameter<?> param = getParameterFromNode(elem);
		//System.out.println("adding parameter under key "+prefix+"."+name);
		config.addParameter(prefix+"."+name, param);
	}
	
	/*
	private void readParameters2() throws Exception
	{
		List<Node>    nodes  = doc.selectNodes("//ITEM");
		
		
		for(Node node : nodes)
		{
			Element element = (Element) node;
			String  name    = element.valueOf("@name");
			
			if(captured_ports.contains(name))
				continue;
			
			if(name.equals("write_ini")||name.equals("write_par")||name.equals("par")||name.equals("help"))
			{
				continue;
			}
			
			Parameter<?> param = getParameterFromNode(node);
			Node parent = node.selectSingleNode("..");
			if(parent.getName().equals(SECTION_NODE_NAME))
			{
				param.setSection(parent.valueOf("@name"));
			}
			config.addParameter(name, param);
		}
	}
	*/
	
	private void readDescription()
	{
		Node   node  = doc.selectSingleNode("/tool/name");
		String name  = node.valueOf("text()");  
		config.setName(name);
		
		node  = doc.selectSingleNode("/tool/description");
		String sdescr  = node.valueOf("text()");  
		config.setDescription(sdescr);
		
		node  = doc.selectSingleNode("/tool/manual");
		String ldescr  = node.valueOf("text()");
		config.setManual(ldescr);
		
		node  = doc.selectSingleNode("/tool/docurl");
		String docurl  = node.valueOf("text()");  
		config.setDocUrl(docurl);
		
		node  = doc.selectSingleNode("/tool/category");
		String cat  = node.valueOf("text()");  
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
		
		//name = name+prefix;
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
		
		if(tagset.contains("mandatory"))
			ret.setIsOptional(false);
		
		//config.addParameter(prefix+name, ret);
		return ret;
	}

	private Parameter<?> processDoubleParameter(String name, String value, String restrs, String tags) throws Exception
	{
		DoubleParameter retd = new DoubleParameter(name, value);

		if(restrs.equals(""))
			return retd;
		
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
					throw new Exception(e);
				}
				retd.setUpperBound(ub);
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
						throw new Exception(e);
					}
					retd.setLowerBound(lb);
					retd.setUpperBound(ub);
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
						throw new Exception(e);
					}
					retd.setLowerBound(lb);
				}
			}
		}
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

	private Parameter<?> processIntParameter(String name, String value, String restrs, String tags) throws Exception
	{
		IntegerParameter reti = new IntegerParameter(name, value);

		if(restrs.equals(""))
			return reti;
		
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
					throw new Exception(e);
				}
				reti.setUpperBound(ub);
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
						throw new Exception(e);
					}
					reti.setLowerBound(lb);
					reti.setUpperBound(ub);
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
						throw new Exception(e);
					}
					reti.setLowerBound(lb);
				}
			}
		}
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
		SAXReader reader = new SAXReader();
		
		try
		{
			doc = reader.read(xmlstream);
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
		
		
		readPorts();
		readParameters();
		readDescription();
		
		config.setXml(doc.asXML());
		
		return config;
	}

	
	
	
	public static void main(String[] args) throws FileNotFoundException, Exception
	{
	}

}
