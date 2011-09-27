package org.ballproject.knime.base.flow.beanshell;

import java.util.ArrayList;
import java.util.List;

public class OutRow
{
	private List<Object> values = new ArrayList<Object>();
	private List<String> names  = new ArrayList<String>();
	private boolean      isNull = true;
	
	public void addCell(String name, Object value)
	{
		isNull = false;
		values.add(value);
		names.add(name);
	}
	
	public void addCell(Object value)
	{
		isNull = false;
		values.add(value);
		names.add(String.format("column %d",names.size()));
	}
	
	public List<Object> getValues()
	{
		return values;
	}
	
	public List<String> getNames()
	{
		return names;
	}
	
	public boolean isNull()
	{
		return isNull;
	}
	
	public List<Class<?>> getTypes()
	{
		List<Class<?>> ret = new ArrayList<Class<?>>();
		for(Object o: values)
		{
			ret.add(o.getClass());
		}
		return ret;
	}
}