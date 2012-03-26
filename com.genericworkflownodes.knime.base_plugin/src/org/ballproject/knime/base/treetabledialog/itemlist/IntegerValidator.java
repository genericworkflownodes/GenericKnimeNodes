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

package org.ballproject.knime.base.treetabledialog.itemlist;

/**
 * The IntegerValidator class checks whether supplied string values are valid ints.
 * 
 * @author roettig
 *
 */
public class IntegerValidator implements Validator
{
	private String reason = "N/A";
	private Integer UB = Integer.MAX_VALUE;
	private Integer LB = Integer.MIN_VALUE;
	
	@Override
	public boolean validate(String s)
	{
		Integer i = null;
		try
		{
			i = Integer.parseInt(s);	
		}
		catch(NumberFormatException e)
		{
			reason = "invalid number format";
			return false;	
		}
		
		if(i>UB)
		{
			reason = "higher than upper bound";
			return false;
		}
		if(i<LB)
		{
			reason = "lower than lower bound";
			return false;
		}
		
		return true;
	}

	@Override
	public String getName()
	{
		return "integer";
	}
	
	/**
	 * set the lower bound for any range validation.
	 * 
	 * @param i lower bound
	 */
	public void setLowerBound(int i)
	{
		LB = i;
	}
	
	/**
	 * set the upper bound for any range validation.
	 * 
	 * @param i upper bound
	 */
	public void setUpperBound(int i)
	{
		UB = i;
	}

	@Override
	public String getReason()
	{
		return reason;
	}
}
