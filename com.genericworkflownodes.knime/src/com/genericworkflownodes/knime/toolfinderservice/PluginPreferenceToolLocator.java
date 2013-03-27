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
package com.genericworkflownodes.knime.toolfinderservice;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.genericworkflownodes.knime.GenericNodesPlugin;

/**
 * Implements the {@link IToolLocatorService} interface. The tool paths are
 * stored using the {@link IPreferenceStore} of the base plugin.
 * 
 * @author aiche
 */
public class PluginPreferenceToolLocator implements IToolLocatorService {
	IPreferenceStore pluginPreferenceStore;

	/**
	 * Suffix for stored properties. It can take three different values,
	 * shipped, user-defined, unknown.
	 * 
	 * If an executable path is requested and the property choice is set to
	 * unknown an exception will be thrown.
	 */
	private static String CHOICE_SUFFIX = "-used-tool";

	/**
	 * Static tool locator instance.
	 */
	private static PluginPreferenceToolLocator toolLocatorService;

	private Set<ExternalTool> tools;

	public PluginPreferenceToolLocator() {
		tools = new HashSet<ExternalTool>();
	}

	public void init(IPreferenceStore store) {
		pluginPreferenceStore = store;
	}

	@Override
	public void registerTool(ExternalTool tool) {
		tools.add(tool);
	}

	public Collection<ExternalTool> getTools() {
		return tools;
	}

	@Override
	public File getToolPath(ExternalTool tool) throws Exception {
		if (tools.contains(tool)) {
			// determine which tool to use
			return getToolPath(tool, getConfiguredToolPathType(tool));
		} else {
			return null;
		}
	}

	@Override
	public File getToolPath(ExternalTool tool, ToolPathType toolPathType)
			throws Exception {
		if (toolPathType == ToolPathType.UNKNOWN) {
			throw new Exception(
					"There is no path (shipped or user-defined) stored for the tool: "
							+ tool.getKey());
		} else {
			String path = pluginPreferenceStore.getString(preferenceKey(tool,
					toolPathType));
			return new File(path);
		}
	}

	/**
	 * Given the tool and the ToolPathType the method returns the preference key
	 * where the path should be stored.
	 * 
	 * @param tool
	 * @param type
	 * @return
	 */
	private String preferenceKey(ExternalTool tool, ToolPathType type) {
		return tool.getKey() + "-" + type.toString();
	}

	@Override
	public void setToolPath(ExternalTool tool, File path, ToolPathType type) {
		if (path.exists() && tools.contains(tool)
				&& type != ToolPathType.UNKNOWN) {
			pluginPreferenceStore.setValue(preferenceKey(tool, type),
					path.getAbsolutePath());
		}
	}

	@Override
	public Map<String, List<ExternalTool>> getToolsByPlugin() {
		Map<String, List<ExternalTool>> plugin2tools = new HashMap<String, List<ExternalTool>>();
		for (ExternalTool tool : tools) {
			if (!plugin2tools.containsKey(tool.getPluginName())) {
				plugin2tools.put(tool.getPluginName(),
						new ArrayList<ExternalTool>());
			}
			plugin2tools.get(tool.getPluginName()).add(tool);
		}
		return plugin2tools;
	}

	@Override
	public boolean isToolRegistered(ExternalTool tool) {
		return tools.contains(tool);
	}

	@Override
	public ToolPathType getConfiguredToolPathType(ExternalTool tool)
			throws Exception {
		if (pluginPreferenceStore.contains(tool.getKey() + CHOICE_SUFFIX)) {
			return ToolPathType.fromString(pluginPreferenceStore.getString(tool
					.getKey() + CHOICE_SUFFIX));
		} else {
			return ToolPathType.UNKNOWN;
		}
	}

	@Override
	public void updateToolPathType(ExternalTool tool, ToolPathType type) {
		pluginPreferenceStore.setValue(tool.getKey() + CHOICE_SUFFIX,
				type.toString());
	}

	@Override
	public boolean hasValidToolPath(ExternalTool tool, ToolPathType type) {
		try {
			File executable = getToolPath(tool, type);
			return (executable != null && executable.exists() && executable
					.canExecute());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Gives access to the instance specific tool locator service.
	 * 
	 * @return
	 */
	public static IToolLocatorService getToolLocatorService() {
		if (toolLocatorService == null) {
			toolLocatorService = new PluginPreferenceToolLocator();

			// configure the tool locator service using the base plugin
			// PreferenceStore
			IPreferenceStore store = GenericNodesPlugin.getDefault()
					.getPreferenceStore();
			toolLocatorService.init(store);
		}
		return toolLocatorService;
	}
}
