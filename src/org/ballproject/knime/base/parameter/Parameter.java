package org.ballproject.knime.base.parameter;

import java.io.Serializable;

public abstract class Parameter<T> implements Serializable
{
	protected String key;
	protected T      value;
	protected String description = "";
	protected String section     = "default";
	protected Boolean isOptional = true;
	
	
	public Parameter(String key, T value)
	{
		this.key   = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getSection()
	{
		return section;
	}

	public void setSection(String section)
	{
		this.section = section;
	}
	
	public boolean isNull()
	{
		if(value==null)
			return true;
		return false;
	}
	
	public Boolean getIsOptional()
	{
		return isOptional;
	}

	public void setIsOptional(Boolean isOptional)
	{
		this.isOptional = isOptional;
	}

	public abstract void fillFromString(String s) throws InvalidParameterValueException;
	
	public abstract boolean validate(T val);
}
