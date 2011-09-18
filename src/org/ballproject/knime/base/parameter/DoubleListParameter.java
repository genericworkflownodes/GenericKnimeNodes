package org.ballproject.knime.base.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoubleListParameter extends NumberListParameter<Double> implements ListParameter
{
	public DoubleListParameter(String key, List<Double> value)
	{
		super(key, value);
	}

	@Override
	public String getMnemonic()
	{
		String lb = (this.lowerBound==Double.NEGATIVE_INFINITY?"-inf":String.format("%e", this.lowerBound));
		String ub = (this.upperBound==Double.POSITIVE_INFINITY?"+inf":String.format("%e", this.upperBound));
		return String.format("double list [%s:%s]",lb,ub);
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null||s.equals(""))
		{
			value = new ArrayList<Double>();
			return;
		}
		this.value = new ArrayList<Double>();
		String[] toks = s.split(SEPERATORTOKEN);
		
		for(int i=0;i<toks.length;i++)
		{
			this.value.add(Double.parseDouble(toks[i]));
		}		
	}

	@Override
	public boolean validate(List<Double> val)
	{
		if(isNull())
			return true;
		
		boolean ok = true;
		
		for(Double v: val)
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
			return "";
		StringBuffer sb = new StringBuffer();
		for(Double d: this.value)
		{
			sb.append(String.format("%e",d)+SEPERATORTOKEN);
		}
		return sb.toString();
	}

	@Override
	public List<String> getStrings()
	{
		List<String> ret = new ArrayList<String>();
		for(Double d: this.value)
		{
			ret.add( d.toString() );
		}
		return ret;
	}

	@Override
	public void fillFromStrings(String[] values)
	{
		this.value = new ArrayList<Double>();
		for(int i=0;i<values.length;i++)
		{
			this.value.add(Double.parseDouble(values[i]));
		}
	}
	
	public String toString()
	{
		if(value==null)
			return "[]";
		String[] ret = new String[this.value.size()];
		int idx = 0;
		for(Double i: value)
			ret[idx++] = i.toString();
		return Arrays.toString(ret);
	}
}
