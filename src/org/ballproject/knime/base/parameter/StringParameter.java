package org.ballproject.knime.base.parameter;

public class StringParameter extends Parameter<String>
{

	public StringParameter(String key, String value)
	{
		super(key, value);
	}

	@Override
	public String toString()
	{
		return value;
	}
	
	@Override
	public void fillFromString(String s)
	{
		if(s==null)
		{
			value = null;
			return;
		}
		value = s;
	}
	
	@Override
	public boolean validate(String val)
	{
		return true;
	}
}
