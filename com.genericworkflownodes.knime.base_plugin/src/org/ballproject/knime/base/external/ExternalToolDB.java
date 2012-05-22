/**
 * Copyright (c) 2012, Marc Röttig, Stephan Aiche.
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
package org.ballproject.knime.base.external;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Basically a list of available tools.
 * 
 * @author aiche
 */
public class ExternalToolDB {
	IPreferenceStore pluginPreferenceStore;

	private Set<ExternalTool> tools;

	private static ExternalToolDB instance = null;

	public static ExternalToolDB getInstance() {
		if (instance == null) {
			instance = new ExternalToolDB();
		}
		return instance;
	}

	private ExternalToolDB() {
		tools = new HashSet<ExternalTool>();
	}

	public void init(IPreferenceStore store) {
		pluginPreferenceStore = store;
	}

	public void registerTool(ExternalTool tool) {
		tools.add(tool);
	}

	public Collection<ExternalTool> getTools() {
		return tools;
	}

	public File getToolPath(ExternalTool tool) {
		String path = pluginPreferenceStore.getString(tool.getKey());
		return new File(path);
	}

	public Map<String, List<ExternalTool>> getToolsByPlugin() {
		Map<String, List<ExternalTool>> plugin2tools = new HashMap<String, List<ExternalTool>>();
		for (ExternalTool tool : tools) {
			if (!plugin2tools.containsKey(tool.getPluginName()))
				plugin2tools.put(tool.getPluginName(),
						new ArrayList<ExternalTool>());
			plugin2tools.get(tool.getPluginName()).add(tool);
		}
		return plugin2tools;
	}
}
