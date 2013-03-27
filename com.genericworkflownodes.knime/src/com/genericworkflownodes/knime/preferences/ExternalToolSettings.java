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
package com.genericworkflownodes.knime.preferences;

import java.io.File;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator;

/**
 * Abstraction of all settings related to single tool.
 * 
 * @author aiche
 */
public class ExternalToolSettings {

	private String localToolPath;
	private boolean hasShippedBinary;
	private IToolLocatorService.ToolPathType selectedToolPathType;

	/**
	 * The tool.
	 */
	private final ExternalTool tool;

	/**
	 * Constructor initializing the settings object.
	 * 
	 * @param tool
	 *            The tool.
	 */
	public ExternalToolSettings(ExternalTool tool) {
		this.tool = tool;

		localToolPath = "";
		hasShippedBinary = false;
		selectedToolPathType = ToolPathType.UNKNOWN;

		load();
	}

	/**
	 * Loads all related settings for the tool.
	 */
	public void load() {
		// we load the path from the IToolLocatorService
		try {
			selectedToolPathType = PluginPreferenceToolLocator
					.getToolLocatorService().getConfiguredToolPathType(tool);

			hasShippedBinary = PluginPreferenceToolLocator
					.getToolLocatorService().hasValidToolPath(tool,
							ToolPathType.SHIPPED);

			File lToolPath = PluginPreferenceToolLocator
					.getToolLocatorService().getToolPath(tool,
							IToolLocatorService.ToolPathType.USER_DEFINED);
			if (lToolPath != null && lToolPath.exists())
				localToolPath = lToolPath.getAbsolutePath();
		} catch (Exception e) {
			GenericNodesPlugin
					.log("Could not load user-defined tool path for tool: "
							+ tool);
			GenericNodesPlugin.log(e.getMessage());
		}
	}

	/**
	 * Saves all related settings for the tool.
	 */
	public void save() {
		try {
			PluginPreferenceToolLocator.getToolLocatorService().setToolPath(
					tool, new File(localToolPath), ToolPathType.USER_DEFINED);
			PluginPreferenceToolLocator.getToolLocatorService()
					.updateToolPathType(tool, selectedToolPathType);
		} catch (Exception e) {
			GenericNodesPlugin
					.log("Could not load user-defined tool path for tool: "
							+ tool);
			GenericNodesPlugin.log(e.getMessage());
		}
	}

	/**
	 * @return the localToolPath
	 */
	public String getLocalToolPath() {
		return localToolPath;
	}

	/**
	 * @param localToolPath
	 *            the localToolPath to set
	 */
	public void setLocalToolPath(String localToolPath) {
		this.localToolPath = localToolPath;
	}

	/**
	 * @return the hasShippedBinary
	 */
	public boolean hasShippedBinary() {
		return hasShippedBinary;
	}

	/**
	 * @param hasShippedBinary
	 *            the hasShippedBinary to set
	 */
	public void setHasShippedBinary(boolean hasShippedBinary) {
		this.hasShippedBinary = hasShippedBinary;
	}

	/**
	 * @return the selectedToolPathType
	 */
	public IToolLocatorService.ToolPathType getSelectedToolPathType() {
		return selectedToolPathType;
	}

	/**
	 * @param selectedToolPathType
	 *            the selectedToolPathType to set
	 */
	public void setSelectedToolPathType(
			IToolLocatorService.ToolPathType selectedToolPathType) {
		this.selectedToolPathType = selectedToolPathType;
	}

	/**
	 * Get the tool name.
	 * 
	 * @return The tool name.
	 */
	public ExternalTool getTool() {
		return tool;
	}
}
