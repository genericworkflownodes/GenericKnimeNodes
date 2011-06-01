package org.ballproject.knime.base.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;
import org.knime.core.node.defaultnodesettings.SettingsModel;

public class DefaultNodeConfiguration implements NodeConfiguration, Serializable
{
	protected Map<String,SettingsModel> settings = new HashMap<String,SettingsModel>();
	protected Map<String,Parameter<?>>  params   = new HashMap<String,Parameter<?>>();
	
	protected int nInPorts;
	protected int nOutPorts;	
	
	protected String name;
	protected String version;
	protected String docurl="";
	protected String shortdescription="";
	protected String longdescription="";
	protected String xml="";
	protected String category="";
	
	public DefaultNodeConfiguration()
	{		
	}
	
	@Override
	public int getNumberOfOutputPorts()
	{
		return out_ports.length;
	}

	@Override
	public int getNumberOfInputPorts()
	{
		return in_ports.length;
	}
	
	@Override
	public Port[] getInputPorts()
	{
		return in_ports;
	}

	@Override
	public Port[] getOutputPorts()
	{
		return out_ports;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{		
		return shortdescription;
	}

	@Override
	public String getManual()
	{
		return longdescription;
	}

	@Override
	public String getDocUrl()
	{
		return docurl;
	}
	
	@Override
	public String getVersion()
	{
		return version;
	}
	
	@Override
	public String getXML()
	{
		return xml;
	}

	@Override
	public String getCategory()
	{
		return category;
	}
	
	/// protected setters
	
	protected void addModel(String key, SettingsModel mdl)
	{
		settings.put(key, mdl);
	}


	
	@Override
	public Parameter<?> getParameter(String key)
	{
		return params.get(key);
	}
	
	
	@Override
	public List<String> getParameterKeys()
	{
		List<String> ret = new ArrayList<String>();
		for(String key: params.keySet())
		{
			ret.add(key);
		}
		return ret;
	}

	protected String[] outputport_names;

	protected void addParameter(String key, Parameter<?> param)
	{
		this.params.put(key, param);
	}

	@Override
	public List<Parameter<?>> getParameters()
	{
		List<Parameter<?>> ret = new ArrayList<Parameter<?>>();
		for(Parameter<?> p: params.values())
		{
			ret.add(p);
		}
		return ret;
	}

	protected Port[] in_ports;
	protected Port[] out_ports;
	
	public void setInports(Port[] ports)
	{
		in_ports = ports;
	}
	
	public void setOutports(Port[] ports)
	{
		out_ports = ports;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDocUrl(String docurl)
	{
		this.docurl = docurl;
	}

	public void setDescription(String shortdescription)
	{
		this.shortdescription = shortdescription;
	}

	public void setManual(String longdescription)
	{
		this.longdescription = longdescription;
	}

	public void setXml(String xml)
	{
		this.xml = xml;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	
	
}
