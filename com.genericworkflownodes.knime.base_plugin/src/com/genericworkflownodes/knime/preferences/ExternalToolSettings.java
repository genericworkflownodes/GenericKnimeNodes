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

import org.eclipse.ui.PlatformUI;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;

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
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {
			try {
				selectedToolPathType = toolLocator
						.getConfiguredToolPathType(tool);

				hasShippedBinary = toolLocator.hasValidToolPath(tool,
						ToolPathType.SHIPPED);

				File lToolPath = toolLocator.getToolPath(tool,
						IToolLocatorService.ToolPathType.USER_DEFINED);
				if (lToolPath != null && lToolPath.exists())
					localToolPath = lToolPath.getAbsolutePath();
			} catch (Exception e) {
				GenericNodesPlugin
						.log("Could not load user-defined tool path for tool: "
								+ tool);
				GenericNodesPlugin.log(e.getMessage());
			}
		} else {
			GenericNodesPlugin
					.log("Unable to get service: IToolLocatorService");
		}

	}

	/**
	 * Saves all related settings for the tool.
	 */
	public void save() {
		// we load the path from the IToolLocatorService
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {
			try {
				toolLocator.setToolPath(tool, new File(localToolPath),
						ToolPathType.USER_DEFINED);
				toolLocator.updateToolPathType(tool, selectedToolPathType);
			} catch (Exception e) {
				GenericNodesPlugin
						.log("Could not load user-defined tool path for tool: "
								+ tool);
				GenericNodesPlugin.log(e.getMessage());
			}
		} else {
			GenericNodesPlugin
					.log("Unable to get service: IToolLocatorService");
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
	 * @throws Exception
	 *             If the given executable does not exist or cannot be executed.
	 */
	public void setLocalToolPath(String localToolPath) throws Exception {
		if ((new File(localToolPath)).exists()
				&& (new File(localToolPath)).canExecute())
			this.localToolPath = localToolPath;
		else
			throw new Exception(
					String.format(
							"The given executable %s either does not exist or is not executable.",
							localToolPath));
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
	public String getToolName() {
		return tool.getToolName();
	}
}
