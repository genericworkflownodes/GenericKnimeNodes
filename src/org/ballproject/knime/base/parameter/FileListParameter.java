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

import java.util.List;

import org.ballproject.knime.base.port.Port;

/**
 * The FileListParameter class is used to store lists of filenames.
 * 
 * This is a convenience class to handle user supplied output filenames.
 * 
 * @author roettig
 *
 */
public class FileListParameter extends StringListParameter
{
	private Port port;
	
	public FileListParameter(String key, List<String> value)
	{
		super(key, value);
	}

	public void setPort(Port port)
	{
		this.port = port;
	}
	
	public Port getPort()
	{
		return port;
	}
}
