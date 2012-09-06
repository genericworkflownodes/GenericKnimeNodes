/**
 * Copyright (c) 2012, Stephan Aiche.
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

package com.genericworkflownodes.knime.config;

import java.util.List;

import com.genericworkflownodes.knime.cliwrapper.CLI;
import com.genericworkflownodes.knime.outputconverter.config.OutputConverters;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;

/**
 * Interface for the node configuration.
 * 
 * @author roettig, aiche
 */
public interface INodeConfiguration {
	/**
	 * The name of the tool.
	 * 
	 * @return The name of the tool.
	 */
	public String getName();

	public String getCommand();

	public String getDescription();

	public String getManual();

	public String getDocUrl();

	/**
	 * Returns the version of the node.
	 * 
	 * @return the version.
	 */
	public String getVersion();

	/**
	 * Returns the xml source of this configuration as {@link String}.
	 * 
	 * @return The xml document.
	 */
	public String getXML();

	/**
	 * Returns the category of the tool.
	 * 
	 * @return the category.
	 */
	public String getCategory();

	/**
	 * Returns the command line interface mapping of the tool.
	 * 
	 * @return The {@link CLI} mapping.
	 */
	public CLI getCLI();

	/**
	 * Returns the configured output converters.
	 * 
	 * @return the output converters.
	 */
	public OutputConverters getOutputConverters();

	public int getNumberOfOutputPorts();

	public int getNumberOfInputPorts();

	public Port[] getInputPorts();

	public Port[] getOutputPorts();

	public Parameter<?> getParameter(String key);

	public List<String> getParameterKeys();

	public List<Parameter<?>> getParameters();
}
