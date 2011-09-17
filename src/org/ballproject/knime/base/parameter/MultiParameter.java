package org.ballproject.knime.base.parameter;

import java.util.ArrayList;
import java.util.List;

public class MultiParameter<T extends Parameter<?>> extends Parameter<List<T>>
{

	public MultiParameter(String key, List<T> value)
	{
		super(key, value);
	}

	@Override
	public String getMnemonic()
	{
		return null;
	}

	@Override
	public void fillFromString(String s) throws InvalidParameterValueException
	{
		throw new RuntimeException("fillFromString is a currently not supported operation");
	}

	@Override
	public boolean validate(List<T> vals)
	{
		/*
		for(T t: vals)
		{
			t.validate(null);
		}
		*/
		throw new RuntimeException("validate is a currently not supported operation");
	}

}
