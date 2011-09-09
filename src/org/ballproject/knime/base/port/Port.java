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

package org.ballproject.knime.base.port;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Port implements Serializable
{

	protected boolean  isOptional;
	protected String   name;
	protected String   description;
		
	protected List<MIMEtype> types = new ArrayList<MIMEtype>();
	
	public void addMimeType(MIMEtype type)
	{
		types.add(type);
	}
	
	public List<MIMEtype> getMimeTypes()
	{
		return types;
	}
	
	public boolean isOptional()
	{
		return isOptional;
	}

	public void setOptional(boolean isOptional)
	{
		this.isOptional = isOptional;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

}
