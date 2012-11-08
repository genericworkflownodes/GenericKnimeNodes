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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of {@link INodeConfigurationStore}.
 * 
 * @author roettig, aiche
 */
@Deprecated
public class NodeConfigurationStore implements INodeConfigurationStore {

	/**
	 * Map to store the parameter values.
	 */
	private Map<String, List<String>> data;

	/**
	 * Default c'tor.
	 */
	public NodeConfigurationStore() {
		data = new LinkedHashMap<String, List<String>>();
	}

	@Override
	public void setParameterValue(String name, String value) {
		if (!data.containsKey(name)) {
			data.put(name, new ArrayList<String>());
		}
		data.get(name).add(value);
	}

	@Override
	public void setMultiParameterValue(String name, String value) {
		setParameterValue(name, value);
	}

	@Override
	public String getParameterValue(String name) {
		if (!data.containsKey(name)) {
			return null;
		}
		return data.get(name).get(0);
	}

	@Override
	public List<String> getMultiParameterValue(String name) {
		if (!data.containsKey(name)) {
			return null;
		}
		return data.get(name);
	}

	@Override
	public Set<String> getParameterKeys() {
		return data.keySet();
	}

}
