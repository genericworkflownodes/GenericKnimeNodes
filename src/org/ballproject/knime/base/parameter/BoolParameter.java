package org.ballproject.knime.base.parameter;


public class BoolParameter extends Parameter<Boolean>
{
	public BoolParameter(String key, Boolean value)
	{
		super(key, value);
	}
	
	public BoolParameter(String key, String value)
	{
		super(key, Boolean.valueOf(value.toLowerCase()));
	}
	
	@Override
	public String toString()
	{
		if(value==null)
			return null;
		return (value?"true":"false");
	}
	
	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null||s.equals(""))
		{
			value = null;
			return;
		}
		if( !(s.equalsIgnoreCase("true")||s.equalsIgnoreCase("false")) )
			throw new InvalidParameterValueException("");
		value = Boolean.parseBoolean(s);
	}

	@Override
	public boolean validate(Boolean val)
	{
		return true;
	}

	@Override
	public String getMnemonic()
	{
		return "bool";
	}
}
