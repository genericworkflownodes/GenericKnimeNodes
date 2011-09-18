package org.ballproject.knime.base.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListParameter extends Parameter<List<String>> implements ListParameter
{
	public StringListParameter(String key, List<String> value)
	{
		super(key, value);
	}

	@Override
	public String getMnemonic()
	{
		return "string list";
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null||s.equals(""))
		{
			value = new ArrayList<String>();
			return;
		}
		this.value = new ArrayList<String>();
		String[] toks = s.split(SEPERATORTOKEN);
		for(int i=0;i<toks.length;i++)
		{
			this.value.add(toks[i]);
		}
	}
	
	@Override
	public String getStringRep()
	{
		if(value==null)
			return "";
		StringBuffer sb = new StringBuffer();
		for(String s: this.value)
		{
			sb.append(s+SEPERATORTOKEN);
		}
		return sb.toString();
	}

	@Override
	public boolean validate(List<String> val)
	{
		return true;
	}

	@Override
	public String toString()
	{
		if(value==null)
			return "[]";
		String[] values = value.toArray(new String[0]);
		return Arrays.toString(values);
	}

	@Override
	public List<String> getStrings()
	{
		List<String> ret = new ArrayList<String>();
		for(String s: this.value)
		{
			ret.add(s);
		}
		return ret;
	}

	@Override
	public void fillFromStrings(String[] values)
	{
		this.value = new ArrayList<String>();
		for(int i=0;i<values.length;i++)
		{
			this.value.add(values[i]);
		}
	}
}
