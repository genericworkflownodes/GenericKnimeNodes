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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.base.port.Port;

import com.genericworkflownodes.knime.cliwrapper.CLI;
import com.genericworkflownodes.knime.outputconverter.config.OutputConverters;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Default implementation of {@link INodeConfiguration}
 * <p>
 * Note: Two {@link NodeConfiguration}s are equal iff their {@link #getName()
 * names} are equal.
 * 
 * @author bkahlert
 * 
 */
public class NodeConfiguration implements INodeConfiguration, Serializable {

	private static final long serialVersionUID = -5250528380628071121L;

	protected Map<String, Parameter<?>> params = new LinkedHashMap<String, Parameter<?>>();

	protected Port[] in_ports;
	protected Port[] out_ports;

	protected String name;
	protected String version;
	protected String command = "";
	protected String docurl = "";
	protected String shortdescription = "";
	protected String longdescription = "";
	protected String xml = "";
	protected String category = "";

	/**
	 * The CLI element stored in the CTD file.
	 */
	private CLI cli;

	/**
	 * The output converters stored in the CTD file.
	 */
	private OutputConverters converters;

	public NodeConfiguration() {
		cli = new CLI();
		converters = new OutputConverters();
	}

	public NodeConfiguration(INodeConfiguration config) {

	}

	@Override
	public int getNumberOfOutputPorts() {
		return out_ports.length;
	}

	@Override
	public int getNumberOfInputPorts() {
		return in_ports.length;
	}

	@Override
	public Port[] getInputPorts() {
		return in_ports;
	}

	@Override
	public Port[] getOutputPorts() {
		return out_ports;
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
	public OutputConverters getOutputConverters() {
		return converters;
	}

	@Override
	public String getXML() {
		return xml;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getCommand() {
		return command;
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

	protected void addParameter(String key, Parameter<?> param) {
		this.params.put(key, param);
	}

	@Override
	public List<Parameter<?>> getParameters() {
		List<Parameter<?>> ret = new ArrayList<Parameter<?>>();
		for (Parameter<?> p : params.values()) {
			ret.add(p);
		}
		return ret;
	}

	public void setInports(Port[] ports) {
		in_ports = ports;
	}

	public void setOutports(Port[] ports) {
		out_ports = ports;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public void setDocUrl(String newDocurl) {
		this.docurl = newDocurl;
	}

	public void setDescription(String newShortdescription) {
		this.shortdescription = newShortdescription;
	}

	public void setManual(String newLongdescription) {
		this.longdescription = newLongdescription;
	}

	public void setXml(String newXml) {
		this.xml = newXml;
	}

	public void setCategory(String newCategory) {
		this.category = newCategory;
	}

	public void setVersion(String newVersion) {
		this.version = newVersion;
	}

	public void setCommand(String newCommand) {
		this.command = newCommand;
	}

	public void setCLI(CLI newCli) {
		this.cli = newCli;
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
}
