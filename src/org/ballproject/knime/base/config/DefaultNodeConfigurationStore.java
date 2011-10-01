package org.ballproject.knime.base.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultNodeConfigurationStore implements NodeConfigurationStore
{
	protected Map<String,List<String>> data = new HashMap<String,List<String>>();
	
	@Override
	public void setParameterValue(String name, String value)
	{
		if(!data.containsKey(name))
			data.put(name, new ArrayList<String>());
		data.get(name).add(value);
	}

	@Override
	public void setMultiParameterValue(String name, String value)
	{
		setParameterValue(name, value);
	}

	@Override
	public String getParameterValue(String name)
	{
		if(!data.containsKey(name))
			return null;
		return data.get(name).get(0);
	}

	@Override
	public List<String> getMultiParameterValue(String name)
	{
		if(!data.containsKey(name))
			return null;
		return data.get(name);
	}

	@Override
	public Set<String> getParameterKeys()
	{
		return data.keySet();
	}

}
