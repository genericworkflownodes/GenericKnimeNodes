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

package org.ballproject.knime.base.config;

import java.util.List;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;

public interface NodeConfiguration
{
	public String getName();
	
	public String getStatus();
	public String getDescription();
	public String getManual();
	public String getDocUrl();
	public String getVersion();
	public String getXML();
	public String getCategory();
	
	public int getNumberOfOutputPorts();
	public int getNumberOfInputPorts();
	
	public Port[] getInputPorts();
	public Port[] getOutputPorts();
		
	public Parameter<?> getParameter(String key);
	public List<String> getParameterKeys();
	public List<Parameter<?>> getParameters();
}
