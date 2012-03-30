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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.port.Port;

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
	protected String mapping = "";

	public NodeConfiguration() {
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

	@Override
	public String getMapping() {
		return this.mapping;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setDocUrl(String docurl) {
		this.docurl = docurl;
	}

	public void setDescription(String shortdescription) {
		this.shortdescription = shortdescription;
	}

	public void setManual(String longdescription) {
		this.longdescription = longdescription;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeConfiguration other = (NodeConfiguration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
