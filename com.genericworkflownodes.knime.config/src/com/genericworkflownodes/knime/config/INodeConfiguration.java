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
import com.genericworkflownodes.knime.config.citation.Citation;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.relocator.Relocator;

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
    String getName();

    /**
     * A short description of the tool.
     * 
     * @return The description.
     */
    String getDescription();

    /**
     * The manual text that should be displayed in the GUI.
     * 
     * @return The manual text.
     */
    String getManual();

    /**
     * The link to the online documentation of this tool.
     * 
     * @return The link to the online documentation.
     */
    String getDocUrl();

    /**
     * Returns the name of the executable file as it was defined in the CTD. If
     * it was not set the tool name will be returned as a proxy.
     * 
     * @return The name of the tool executable.
     */
    String getExecutableName();

    /**
     * Returns the path to the executable as it was defined in the CTD. Note
     * that the path does not contain the actual executable. See
     * {@link #getExecutableName()} if you need access to the executable name.
     * 
     * @return The path to the executable.
     */
    String getExecutablePath();

    /**
     * Returns the version of the node.
     * 
     * @return the version.
     */
    String getVersion();

    /**
     * Returns the xml source of this configuration as {@link String}.
     * 
     * @return The xml document.
     * @deprecated
     */
    @Deprecated
    String getXML();

    /**
     * Returns the category of the tool.
     * 
     * @return the category.
     */
    String getCategory();

    /**
     * Returns the citation(s) for the tool.
     * 
     * @return the citation.
     */
    List<Citation> getCitations();

    /**
     * Returns the command line interface mapping of the tool.
     * 
     * @return The {@link CLI} mapping.
     */
    CLI getCLI();

    /**
     * Returns the list of all relocators used to find and move files after tool
     * execution. See {@link Relocator} for details.
     * 
     * @return The list of available relocators, if no relocators were set an
     *         empty list is returned.
     */
    List<Relocator> getRelocators();

    /**
     * Returns the number of output ports of this tool.
     * 
     * @return The number of output ports.
     */
    int getNumberOfOutputPorts();

    /**
     * Returns the number of input ports of this tool.
     * 
     * @return The number of input ports.
     */
    int getNumberOfInputPorts();

    /**
     * The input ports of this tool.
     * 
     * @return The input ports.
     */
    List<Port> getInputPorts();

    /**
     * The output ports of this tool.
     * 
     * @return The output ports.
     */
    List<Port> getOutputPorts();

    /**
     * Returns the specified port if it is a registered input port or null if
     * the port does not exist.
     * 
     * @param portName
     *            The name of the port that is searched.
     * 
     * @return The port object or null if no such port exists.
     */
    Port getInputPortByName(String portName);

    /**
     * Returns the specified port if it is a registered output port or null if
     * the port does not exist.
     * 
     * @param portName
     *            The name of the port that is searched.
     * 
     * @return The port object or null if no such port exists.
     */
    Port getOutputPortByName(String portName);

    /**
     * Returns the parameter of this tool having the given key.
     * 
     * @param key
     *            The key of the parameter that should be returned.
     * @return The parameter associated with the given key.
     */
    Parameter<?> getParameter(String key);

    /**
     * A list of all parameter names of this tool.
     * 
     * @return A list of parameter names.
     */
    List<String> getParameterKeys();

    /**
     * The list of all parameters of this tool.
     * 
     * @return The list of all parameters of this tool.
     */
    List<Parameter<?>> getParameters();

    /**
     * Returns the description of the given section or null if the section is
     * not known.
     * 
     * @param section
     *            The section name.
     * @return The section description.
     */
    String getSectionDescription(String section);
}
