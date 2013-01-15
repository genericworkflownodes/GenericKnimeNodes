/**
 * Copyright (c) 2013, Stephan Aiche.
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
package com.genericworkflownodes.knime.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;

/**
 * @author aiche
 * 
 */
public class GenericStartup implements IStartup {

	/**
	 * The central static logger.
	 */
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(GenericStartup.class);

	/**
	 * The name of the bundle that is initialized.
	 */
	private final String m_bundleName;

	/**
	 * The id of the preference page to open if there are missing binaries.
	 */
	private final String m_preferencePageId;

	/**
	 * The plugin name used to reference the executables.
	 */
	private final String m_pluginName;

	/**
	 * Create the GenericStartup with the name of the plugin. The name is used
	 * to build the dialogs and find the correct pref-page.
	 * 
	 * @param bundleName
	 *            The name of the bundle that is initialized.
	 * @param preferencePageId
	 *            The id of the preference page to open if there are missing
	 *            binaries.
	 */
	public GenericStartup(final String bundleName,
			final String preferencePageId, final String pluginName) {
		m_bundleName = bundleName;
		m_preferencePageId = preferencePageId;
		m_pluginName = pluginName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		try {
			if (findUnitializedBinaries().size() == 0) {
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								MissingBinariesDialog mbDialog = new MissingBinariesDialog(
										PlatformUI.getWorkbench().getDisplay()
												.getActiveShell(),
										m_bundleName, m_preferencePageId);
								mbDialog.create();
								mbDialog.open();
							}
						});
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	/**
	 * Returns a list containing all nodes that do not have a configured binary.
	 * If all binaries are correctly initialized the list is empty.
	 * 
	 * @return A list containing all binaries that were not correctly
	 *         initialized.
	 * @throws Exception
	 *             If the {@link IToolLocatorService} could not be initialized
	 *             correctly.
	 */
	public List<String> findUnitializedBinaries() throws Exception {
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator == null) {
			throw new Exception("Could not find matching ToolLocatorService.");
		}

		List<ExternalTool> tools = toolLocator.getToolsByPlugin().get(
				m_pluginName);

		List<String> uninitializedBinaries = new ArrayList<String>();
		for (ExternalTool tool : tools) {
			if (toolLocator.getConfiguredToolPathType(tool) == ToolPathType.UNKNOWN) {
				uninitializedBinaries.add(tool.getToolName());
			}
		}

		return uninitializedBinaries;
	}
}
