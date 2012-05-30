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
package com.genericworkflownodes.knime.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.GenericNodesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;

public class ExternalToolsPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private List<ToolFieldEditor> toolPathes = new ArrayList<ToolFieldEditor>();

	public ExternalToolsPage() {
		super();
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	public void init(IWorkbench wb) {
	}

	@Override
	protected Control createContents(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite c = new Composite(sc, SWT.NONE);
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		c.setLayout(fillLayout);

		sc.setContent(c);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {

			Map<String, List<ExternalTool>> plugin2tools = toolLocator
					.getToolsByPlugin();

			for (String pluginname : plugin2tools.keySet()) {

				Group group = new Group(c, SWT.SHADOW_ETCHED_IN);
				group.setText(pluginname);

				for (ExternalTool tool : plugin2tools.get(pluginname)) {
					ToolFieldEditor gToolEditor = new ToolFieldEditor(tool,
							group);
					gToolEditor.load();
					toolPathes.add(gToolEditor);
				}
			}
		}

		return sc;
	}

	@Override
	public boolean performOk() {
		saveToPreferenceStore();

		// Return true to allow dialog to close
		return true;
	}

	@Override
	protected void performApply() {
		saveToPreferenceStore();
	}

	/**
	 * Saves the entries of the FileFieldEditor.
	 */
	private void saveToPreferenceStore() {
		for (ToolFieldEditor gFieldEditor : toolPathes) {
			gFieldEditor.store();
		}
	}
}
