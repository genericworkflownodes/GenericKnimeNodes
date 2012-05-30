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
import java.util.List;
import java.util.Map;

/**
 * Interface defining the tool finding service, which helps finding the
 * appropriate paths for a given tool.
 * 
 * @author aiche
 */
public interface IToolLocatorService {

	/**
	 * Defines if the referenced binary is part of the distributed node (i.e.,
	 * contained in the payload) or was configured by the user.
	 * 
	 * @author aiche
	 */
	public enum ToolPathType {
		SHIPPED("shipped"), USER_DEFINED("user-defined"), UNKNOWN("unknown");

		private final String asString;

		ToolPathType(final String s) {
			asString = s;
		}

		public String toString() {
			return asString;
		}

		/**
		 * Create a ToolPathType enum value based on the passed string. If it is
		 * not "shipped", "user-defined", or "unknown" an exception will be
		 * thrown.
		 * 
		 * @param choice
		 * @return
		 * @throws Exception
		 *             If the given choice is not a valid VALID_CHOICE.
		 */
		public static ToolPathType fromString(final String choice)
				throws Exception {
			if (SHIPPED.toString().equals(choice)) {
				return SHIPPED;
			} else if (USER_DEFINED.toString().equals(choice)) {
				return USER_DEFINED;
			} else if (UNKNOWN.toString().equals(choice)) {
				return UNKNOWN;
			} else {
				throw new Exception("The given choice is not a valid one.");
			}
		}
	}

	/**
	 * Returns the tool path of the given ExternalTool. If the tool is not
	 * registered the method will return null, if the tool is registered but no
	 * path is specified it will return new File("").
	 * 
	 * @param tool
	 * @return
	 * @throws Exception
	 *             Throws an exception if no valid configuration exists.
	 */
	public File getToolPath(ExternalTool tool) throws Exception;

	/**
	 * 
	 * @param tool
	 *            The external tool which we want to get a path for.
	 * @param toolPathType
	 *            The tool path type (shipped or user-defined).
	 * @return A {@link File} pointing to the executable of the tool.
	 * @throws Exception
	 *             An exception is thrown if there is no executable for the
	 *             tool.
	 */
	public File getToolPath(ExternalTool tool, ToolPathType toolPathType)
			throws Exception;

	/**
	 * Sets the tool path for the tool @p tool to the path specified by @p path.
	 * 
	 * @param tool
	 * @param path
	 */
	public void setToolPath(ExternalTool tool, File path, ToolPathType type);

	/**
	 * Adds a new tool to the underlying tool registry.
	 * 
	 * @param tool
	 */
	public void registerTool(ExternalTool tool);

	/**
	 * Checks if the given tool was already registered.
	 * 
	 * @param tool
	 *            The requested tool.
	 * @return True if the tool was registered before, false otherwise.
	 */
	public boolean isToolRegistered(ExternalTool tool);

	/**
	 * Returns the currently configured type of the tool path.
	 * 
	 * @param tool
	 *            The requested tool
	 * @return The ToolPathType configured by either the plugin or the user.
	 * @throws Exception
	 *             Throws an exception if no valid configuration exists.
	 */
	public ToolPathType getConfiguredToolPathType(ExternalTool tool)
			throws Exception;

	/**
	 * Sets the tool path type to the given @p type.
	 * 
	 * @param tool
	 *            The tool to update.
	 * @param type
	 *            The new type.
	 */
	public void updateToolPathType(ExternalTool tool, ToolPathType type);

	/**
	 * Returns a mapping between all available tools and the corresponding
	 * plugins.
	 * 
	 * @return
	 */
	public Map<String, List<ExternalTool>> getToolsByPlugin();

	/**
	 * Checks if the given tool has a valid path for the given
	 * {@link ToolPathType}.
	 * 
	 * @param tool
	 *            The tool to check.
	 * @param type
	 *            The {@link ToolPathType} to check.
	 * @return True if a valid executable is stored for the given tool and
	 *         {@link ToolPathType}, false otherwise.
	 */
	public boolean hasValidToolPath(ExternalTool tool, ToolPathType type);
}
