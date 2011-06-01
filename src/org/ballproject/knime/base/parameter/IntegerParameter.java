package org.ballproject.knime.base.parameter;

public class IntegerParameter extends NumberParameter<Integer>
{
	public IntegerParameter(String key, Integer value)
	{
		super(key, value);
		this.lowerBound = Integer.MIN_VALUE;
		this.upperBound = Integer.MAX_VALUE;
	}
	
	public IntegerParameter(String key, String value)
	{
		super(key, (value.equals("")?null:Integer.parseInt(value)));
		this.lowerBound = Integer.MIN_VALUE;
		this.upperBound = Integer.MAX_VALUE;
	}
	
	@Override
	public String toString()
	{
		if(value==null)
			return "";
		return String.format("%d",value);
	}
	
	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null||s.equals(""))
		{
			value = null;
			return;
		}
		try
		{
			value = Integer.parseInt(s);
		}
		catch(NumberFormatException e)
		{
			throw new InvalidParameterValueException("parameter "+this.getKey()+" value is not a double",e);
		}
		if(value<this.getLowerBound()||value>this.getUpperBound())
		{
			throw new InvalidParameterValueException("parameter "+this.getKey()+" value is out of bounds");
		}
	}
	
	@Override
	public boolean validate(Integer val)
	{
		if(isNull())
			return true;
		if(val>=this.lowerBound && val<=this.upperBound)
			return true;
		return false;
	}
}
