package org.ballproject.knime.base.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegerListParameter extends NumberListParameter<Integer> implements ListParameter
{
	public IntegerListParameter(String key, List<Integer> value)
	{
		super(key, value);
	}

	@Override
	public String getMnemonic()
	{
		String lb = (this.lowerBound==Integer.MIN_VALUE?"-inf":String.format("%d", this.lowerBound));
		String ub = (this.upperBound==Integer.MAX_VALUE?"+inf":String.format("%d", this.upperBound));
		return String.format("integer list [%s:%s]",lb,ub);
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null||s.equals(""))
		{
			value = new ArrayList<Integer>();
			return;
		}
		this.value = new ArrayList<Integer>();
		String[] toks = s.split(SEPERATORTOKEN);
		
		for(int i=0;i<toks.length;i++)
		{
			this.value.add(Integer.parseInt(toks[i]));
		}
	}

	@Override
	public boolean validate(List<Integer> val)
	{
		if(isNull())
			return true;
		
		boolean ok = true;
		
		for(Integer v: val)
		{
			if(v<this.lowerBound || v>this.upperBound)
				ok = false;				
		}
		return ok;
	}

	@Override
	public String getStringRep()
	{
		if(value==null)
		{
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(Integer d: this.value)
		{
			sb.append(String.format("%d",d)+SEPERATORTOKEN);
		}
		return sb.toString();
	}

	@Override
	public List<String> getStrings()
	{
		List<String> ret = new ArrayList<String>();
		for(Integer i: this.value)
		{
			ret.add( i.toString() );
		}
		return ret;
	}
	
	public String toString()
	{
		if(value==null)
			return "";
		String[] ret = new String[this.value.size()];
		int idx = 0;
		for(Integer i: value)
			ret[idx++] = i.toString();
		return Arrays.toString(ret);
	}
	
	@Override
	public void fillFromStrings(String[] values)
	{
		this.value = new ArrayList<Integer>();
		for(int i=0;i<values.length;i++)
		{
			this.value.add(Integer.parseInt(values[i]));
		}
	}
}
