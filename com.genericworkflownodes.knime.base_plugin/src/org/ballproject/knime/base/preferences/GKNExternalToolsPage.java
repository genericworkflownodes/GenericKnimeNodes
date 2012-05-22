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
package org.ballproject.knime.base.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.external.ExternalTool;
import org.ballproject.knime.base.external.ExternalToolDB;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GKNExternalToolsPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private List<FileFieldEditor> toolPathes = new ArrayList<FileFieldEditor>();
	private List<ExternalTool> externalTools = new ArrayList<ExternalTool>();

	public GKNExternalToolsPage() {
		super();
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(store);
		setDescription("KNIME GKN external tools DB");
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

		IPreferenceStore preferenceStore = getPreferenceStore();

		Map<String, List<ExternalTool>> plugin2tools = ExternalToolDB
				.getInstance().getToolsByPlugin();

		for (String pluginname : plugin2tools.keySet()) {
			for (ExternalTool tool : plugin2tools.get(pluginname)) {
				String[] toks = pluginname.split("\\.");

				String name = tool.getToolName();
				if (toks != null)
					name = toks[toks.length - 1] + " - " + tool.getToolName();

				FileFieldEditor toolpath = new FileFieldEditor(tool.getKey(),
						name, c);

				toolpath.setPreferenceStore(getPreferenceStore());
				toolpath.load();
				String val = preferenceStore.getString(toolpath
						.getPreferenceName());
				toolpath.setStringValue((val == null ? "" : val));
				toolPathes.add(toolpath);
				externalTools.add(tool);
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
	 * Saves the entries of the FileFieldEditor to the associated
	 * PreferenceStore.
	 */
	private void saveToPreferenceStore() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		int idx = 0;
		for (FileFieldEditor fe : toolPathes) {
			GenericNodesPlugin
					.log("[saveToPreferenceStore] setting toolpath to "
							+ fe.getStringValue() + " for tool "
							+ externalTools.get(idx));
			preferenceStore.setValue(fe.getPreferenceName(),
					fe.getStringValue());
			idx++;
		}
	}

}
