/**
 * Copyright (c) 2012, Stephan Aiche, Björn Kahlert.
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;

/**
 * This class provides a base implementation of a plugin defaults preference
 * page for derived plugins. It gives the opportunity to define the paths to the
 * plugin's tool executables.
 * 
 * @author aiche, bkahlert
 */
public abstract class BasePluginPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * The list of all available tool pathes.
	 */
	private final List<ToolFieldEditor> toolPaths = new ArrayList<ToolFieldEditor>();

	private final String pluginName;
	private Composite c;

	public BasePluginPreferencePage(String pluginName) {
		super();
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(store);
		// we do not need the apply key and do not support the restore default
		// key
		noDefaultAndApplyButton();
		this.pluginName = pluginName;
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		c = new Composite(sc, SWT.NONE);

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.V_SCROLL;

		GridLayout gl_c = new GridLayout();
		c.setLayout(gl_c);

		sc.setContent(c);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		// auto discover executables
		addExecutablesButtons(c);

		addAllExecutables(c);

		return sc;
	}

	private void addAllExecutables(Composite parent) {

		Label horizontalBar = new Label(c, SWT.SEPARATOR | SWT.HORIZONTAL);
		horizontalBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		Composite executableComposite = new Composite(parent, NONE);
		executableComposite.setLayout(new RowLayout());

		// the tool dialogs
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {

			Map<String, List<ExternalTool>> plugin2tools = toolLocator
					.getToolsByPlugin();

			List<ExternalTool> tools = plugin2tools.get(pluginName);

			// sort each plugin by name
			Collections.sort(tools, new Comparator<ExternalTool>() {
				@Override
				public int compare(final ExternalTool o1, final ExternalTool o2) {
					return o1.getToolName().compareToIgnoreCase(
							o2.getToolName());
				}
			});

			// add each tool shipped with the current plugin to the GUI
			// group
			for (ExternalTool tool : tools) {
				ToolFieldEditor gToolEditor = new ToolFieldEditor(tool,
						executableComposite);
				gToolEditor.load();
				toolPaths.add(gToolEditor);
			}
		}
	}

	private void addExecutablesButtons(Composite parent) {

		GridLayout groupCompositeGridLayout = new GridLayout(3, false);
		groupCompositeGridLayout.marginWidth = 0;
		groupCompositeGridLayout.marginHeight = 0;
		groupCompositeGridLayout.verticalSpacing = 0;
		groupCompositeGridLayout.horizontalSpacing = 0;

		Composite groupComposite = new Composite(parent, SWT.NONE);
		groupComposite.setLayout(groupCompositeGridLayout);
		groupComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		Label lblNewLabel = new Label(groupComposite, SWT.NONE);
		lblNewLabel.setText("Find executables");
		new Label(groupComposite, SWT.NONE);

		Composite buttonComposite = new Composite(groupComposite, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
				false, 1, 1));
		buttonComposite.setLayout(new GridLayout(2, false));

		Button searchInPath = new Button(buttonComposite, SWT.PUSH);
		searchInPath
				.setToolTipText("Searchs the PATH variable for executables for the tools shipped with this plugin.");
		searchInPath.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
				false, 1, 1));
		searchInPath.setText("Search in PATH");
		searchInPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("searchInPath");
			}
		});

		Button searchInDirectory = new Button(buttonComposite, SWT.PUSH);
		searchInDirectory
				.setToolTipText("Lets you specify a directory where to search for the executables.");
		searchInDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL,
				false, false, 1, 1));
		searchInDirectory.setText("Search in Directory");
		searchInDirectory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("searchInDirectory");
			}
		});
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
		for (ToolFieldEditor gFieldEditor : toolPaths) {
			gFieldEditor.store();
		}
	}
}
