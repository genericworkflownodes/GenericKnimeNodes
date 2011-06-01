package org.ballproject.knime.base.parameter;

import java.util.Arrays;
import java.util.List;

public class StringChoiceParameter extends Parameter<String>
{
	private List<String> values;
	
	public StringChoiceParameter(String key, String value)
	{
		super(key, value);
	}
	
	public StringChoiceParameter(String key, List<String> values)
	{
		super(key, values.get(0));
		this.values = values;
	}	
	
	public StringChoiceParameter(String key, String[] values)
	{
		super(key, values[0]);
		this.values = Arrays.asList(values);
	}
	
	@Override
	public void setValue(String value)
	{
		if(values.contains(value))
			super.setValue(value);
	}

	public List<String> getAllowedValues()
	{
		return values;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
	
	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		if(s==null)
		{
			value = null;
			return;
		}
		if(!this.getAllowedValues().contains(s))
			throw new InvalidParameterValueException("parameter "+this.getKey()+" value is invalid");
		value = s;
		
	}
	
	@Override
	public boolean validate(String val)
	{
		return true;
	}
}
