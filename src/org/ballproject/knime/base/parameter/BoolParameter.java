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

/**
 * The BoolParameter class is used to store boolean values.
 * 
 * @author roettig
 *
 */
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

	@Override
	public String getStringRep()
	{
		return toString();
	}
}
