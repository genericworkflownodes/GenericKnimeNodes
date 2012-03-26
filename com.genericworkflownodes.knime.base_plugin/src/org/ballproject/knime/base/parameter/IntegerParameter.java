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
 * The IntegerParameter class is used to store int values.
 * 
 * @author roettig
 *
 */
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
	
	@Override
	public String getMnemonic()
	{
		String lb = (this.lowerBound==Integer.MIN_VALUE?"-inf":String.format("%d", this.lowerBound));
		String ub = (this.upperBound==Integer.MAX_VALUE?"+inf":String.format("%d", this.upperBound));
		return String.format("integer [%s:%s]",lb,ub);
	}
}
