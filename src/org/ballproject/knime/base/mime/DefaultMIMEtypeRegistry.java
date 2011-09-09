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

package org.ballproject.knime.base.mime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMIMEtypeRegistry implements MIMEtypeRegistry
{
	protected List<MIMEtypeRegistry>   resolvers = new ArrayList<MIMEtypeRegistry>();
	
	@Override
	public MIMEFileCell getCell(String name)
	{
		MIMEFileCell cell = null;
		for(MIMEtypeRegistry resolver: resolvers)
		{
			try
			{
				cell = resolver.getCell(name);
			} 
			catch (Exception e)
			{
				cell = null;
			}
		}
		return cell;
	}

	@Override
	public void addResolver(MIMEtypeRegistry resolver)
	{
		resolvers.add(resolver);
	}

}
