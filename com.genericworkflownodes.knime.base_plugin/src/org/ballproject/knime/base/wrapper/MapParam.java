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

package org.ballproject.knime.base.wrapper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MapParam extends Task
{
	private String CLISwitch;
	private String name;
	
	public void execute() throws BuildException
	{
		GenericToolWrapper at = (GenericToolWrapper) this.getProject();
		at.addSwitch(CLISwitch, at.getProperty(name));
	}

	public String getCLISwitch()
	{
		return CLISwitch;
	}

	public void setCLISwitch(String cLISwitch)
	{
		CLISwitch = cLISwitch;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	
}
