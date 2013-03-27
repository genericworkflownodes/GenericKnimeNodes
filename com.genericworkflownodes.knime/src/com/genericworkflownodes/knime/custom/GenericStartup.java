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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.knime.core.node.NodeLogger;

import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator;

/**
 * @author aiche
 * 
 */
public class GenericStartup implements IStartup {

	public static final String PREFERENCE_WARN_IF_BINARIES_ARE_MISSING = "WARN_IF_BINARIES_ARE_MISSING";

	/**
	 * The central static logger.
	 */
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(GenericStartup.class);

	/**
	 * The id of the preference page to open if there are missing binaries.
	 */
	private final String m_preferencePageId;

	/**
	 * The binaries manager of the plugin.
	 */
	private GenericActivator m_pluginActivator;

	/**
	 * Create the GenericStartup with the name of the plugin. The name is used
	 * to build the dialogs and find the correct pref-page.
	 * 
	 * @param preferencePageId
	 *            The id of the preference page to open if there are missing
	 *            binaries.
	 * @param preferenceStore
	 *            The preference store of the plugin.
	 */
	public GenericStartup(GenericActivator genericActivator,
			final String preferencePageId) {
		m_preferencePageId = preferencePageId;
		m_pluginActivator = genericActivator;
		m_pluginActivator.getPreferenceStore().setDefault(
				PREFERENCE_WARN_IF_BINARIES_ARE_MISSING, true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		try {
			// in case we have a payload, check if it is already extracted and
			// valid
			if (m_pluginActivator.getBinariesManager().hasPayload()) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							if (!m_pluginActivator.getBinariesManager()
									.hasValidPayload()) {
								new ProgressMonitorDialog(PlatformUI
										.getWorkbench().getDisplay()
										.getActiveShell()).run(true, false,
										m_pluginActivator.getBinariesManager());
							}

							// we should (in any case) have a valid payload now
							m_pluginActivator.getBinariesManager().register();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}

			// in any case; check if we have a binary for all nodes
			if (!findUnitializedBinaries().isEmpty()
					&& m_pluginActivator.getPreferenceStore().getBoolean(
							PREFERENCE_WARN_IF_BINARIES_ARE_MISSING)) {
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								MissingBinariesDialog mbDialog = new MissingBinariesDialog(
										PlatformUI.getWorkbench().getDisplay()
												.getActiveShell(),
										m_pluginActivator
												.getPluginConfiguration()
												.getPluginName(),
										m_preferencePageId, m_pluginActivator
												.getPreferenceStore());
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
		List<String> uninitializedBinaries = new ArrayList<String>();
		for (ExternalTool tool : m_pluginActivator.getTools()) {
			if (PluginPreferenceToolLocator.getToolLocatorService()
					.getConfiguredToolPathType(tool) == ToolPathType.UNKNOWN) {
				uninitializedBinaries.add(tool.getToolName());
			}
		}

		return uninitializedBinaries;
	}
}
