/*
 * Copyright (c) 2011, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ballproject.knime.base.parameter;

import java.io.Serializable;

public abstract class Parameter<T> implements Serializable
{
	protected String key;
	protected T      value;
	protected String description = "";
	protected String section     = "default";
	protected boolean isOptional = true;
	
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
		
	public boolean getIsOptional()
	{
		return isOptional;
	}

	public void setIsOptional(boolean isOptional)
	{
		this.isOptional = isOptional;
	}
	
	public abstract String getMnemonic();

	public abstract void fillFromString(String s) throws InvalidParameterValueException;
	
	public String getStringRep()
	{
		return toString();
	}
	
	public abstract boolean validate(T val);
	
	protected static String SEPERATORTOKEN = "@@@__@@@";
	
}
