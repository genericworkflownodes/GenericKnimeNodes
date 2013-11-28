/**
 * Copyright (c) 2012, Marc Röttig.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.genericworkflownodes.knime.cliwrapper.CLI;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.relocator.Relocator;

/**
 * Default implementation of {@link INodeConfiguration}
 * <p>
 * Note: Two {@link NodeConfiguration}s are equal iff their {@link #getName()
 * names} are equal.
 * 
 * @author roettig, bkahlert, aiche
 * 
 */
public class NodeConfiguration implements INodeConfiguration, Serializable {

    private static final long serialVersionUID = -5250528380628071121L;

    // The parameters.
    private Map<String, Parameter<?>> params;

    // The section descriptions for the sections of the paramters.
    private Map<String, String> sectionDescriptions;

    private Map<String, Port> inputPorts;
    private Map<String, Port> outputPorts;

    private String name;
    private String version;
    private String docurl = "";
    private String shortdescription = "";
    private String longdescription = "";
    private String xml = "";
    private String category = "";

    /**
     * Name of the executable.
     */
    private String executableName;

    /**
     * Path to the folder that contains the executable.
     */
    private String executablePath;

    /**
     * The CLI element stored in the CTD file.
     */
    private CLI cli;

    /**
     * The relocators for this tool.
     */
    private List<Relocator> relocators;

    /**
     * Creates a new, empty {@link NodeConfiguration}.
     */
    public NodeConfiguration() {
        cli = new CLI();

        params = new LinkedHashMap<String, Parameter<?>>();
        inputPorts = new LinkedHashMap<String, Port>();
        outputPorts = new LinkedHashMap<String, Port>();

        sectionDescriptions = new HashMap<String, String>();

        executableName = null;
        executablePath = "";

        relocators = new ArrayList<Relocator>();
    }

    public NodeConfiguration(INodeConfiguration config) {

    }

    @Override
    public int getNumberOfOutputPorts() {
        return outputPorts.size();
    }

    @Override
    public int getNumberOfInputPorts() {
        return inputPorts.size();
    }

    @Override
    public List<Port> getInputPorts() {
        return portsToList(inputPorts);
    }

    @Override
    public List<Port> getOutputPorts() {
        return portsToList(outputPorts);
    }

    /**
     * Utility function that converts the given map into an array of port
     * objects.
     * 
     * @param ports
     *            The port map to convert.
     * @return The port map as array of {@link Port}s.
     */
    private List<Port> portsToList(Map<String, Port> ports) {
        List<Port> portsAsList = new ArrayList<Port>(ports.size());
        for (Map.Entry<String, Port> entry : ports.entrySet()) {
            portsAsList.add(entry.getValue());
        }
        return portsAsList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return shortdescription;
    }

    @Override
    public String getManual() {
        return longdescription;
    }

    @Override
    public String getDocUrl() {
        return docurl;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public CLI getCLI() {
        return cli;
    }

    @Override
    public String getXML() {
        return xml;
    }

    @Override
    public String getCategory() {
        return category;
    }

    // / protected setters

    @Override
    public Parameter<?> getParameter(String key) {
        return params.get(key);
    }

    @Override
    public List<String> getParameterKeys() {
        List<String> ret = new ArrayList<String>();
        for (String key : params.keySet()) {
            ret.add(key);
        }
        return ret;
    }

    public void addParameter(String key, Parameter<?> param) {
        params.put(key, param);
    }

    @Override
    public List<Parameter<?>> getParameters() {
        List<Parameter<?>> ret = new ArrayList<Parameter<?>>();
        for (Parameter<?> p : params.values()) {
            ret.add(p);
        }
        return ret;
    }

    /**
     * Utility function to copy all elements of the portArray into the provided
     * portmap.
     * 
     * @param portMap
     *            {@link Map} of {@link Port}s.
     * @param portArray
     *            Array of {@link Port}s.
     */
    private void createPortList(Map<String, Port> portMap, Port[] portArray) {
        portMap.clear();
        for (Port p : portArray) {
            portMap.put(p.getName(), p);
        }
    }

    private void createPortList(Map<String, Port> portMap, List<Port> portArray) {
        portMap.clear();
        for (Port p : portArray) {
            portMap.put(p.getName(), p);
        }
    }

    public void setInports(Port[] ports) {
        createPortList(inputPorts, ports);
    }

    public void setInports(List<Port> ports) {
        createPortList(inputPorts, ports);
    }

    public void setOutports(Port[] ports) {
        createPortList(outputPorts, ports);
    }

    public void setOutports(List<Port> ports) {
        createPortList(outputPorts, ports);
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setDocUrl(String newDocurl) {
        docurl = newDocurl;
    }

    public void setDescription(String newShortdescription) {
        shortdescription = newShortdescription;
    }

    public void setManual(String newLongdescription) {
        longdescription = newLongdescription;
    }

    public void setXml(String newXml) {
        xml = newXml;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
    }

    public void setVersion(String newVersion) {
        version = newVersion;
    }

    public void setCLI(CLI newCli) {
        cli = newCli;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodeConfiguration other = (NodeConfiguration) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String getExecutableName() {
        if (executableName != null)
            return executableName;
        else
            return name;
    }

    public void setExecutableName(String executableName) {
        this.executableName = executableName;
    }

    @Override
    public String getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(String executablePath) {
        this.executablePath = executablePath;
    }

    @Override
    public List<Relocator> getRelocators() {
        return relocators;
    }

    @Override
    public String getSectionDescription(String section) {
        if (sectionDescriptions.containsKey(section)) {
            return sectionDescriptions.get(section);
        } else
            return null;
    }

    /**
     * Adds the given section and the corresponding description to the tool
     * configuration.
     * 
     * @param section
     *            The section.
     * @param description
     *            The description of the section.
     */
    public void setSectionDescription(String section, String description) {
        sectionDescriptions.put(section, description);
    }

    @Override
    public Port getInputPortByName(String portName) {
        return inputPorts.get(portName);
    }

    @Override
    public Port getOutputPortByName(String portName) {
        return outputPorts.get(portName);
    }
}
