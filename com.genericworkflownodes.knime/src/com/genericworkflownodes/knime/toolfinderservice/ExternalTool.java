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

/**
 * ExternalTool holds the information for each individual tool that is
 * encapsulated into the generic KNIME nodes.
 * 
 * @author aiche
 */
public final class ExternalTool {
	private String toolName;
	private String pluginName;
	private String executableName;

	public ExternalTool(String pluginName, String toolName,
			String executableName) {
		this.pluginName = pluginName;
		this.toolName = toolName;
		this.executableName = executableName;
	}

	public String getToolName() {
		return toolName;
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getExecutableName() {
		return executableName;
	}

	public String getKey() {
		return String.format("%s_%s", pluginName, toolName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || !(obj instanceof ExternalTool)) {
			return false;
		}
		ExternalTool eTool = (ExternalTool) obj;
		return (pluginName.equals(eTool.pluginName)
				&& toolName.equals(eTool.toolName) && executableName
					.equals(eTool.executableName));
	}

	@Override
	public int hashCode() {
		return pluginName.hashCode() ^ toolName.hashCode()
				^ executableName.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s_%s", pluginName, toolName);
	}
}