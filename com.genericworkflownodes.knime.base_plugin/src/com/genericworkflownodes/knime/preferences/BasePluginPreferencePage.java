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

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;

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
	private final LinkedHashMap<String, ExternalToolSettings> toolSettings = new LinkedHashMap<String, ExternalToolSettings>();

	private final String pluginName;
	private Composite baseComposite;

	// private Table executableTable;
	private TableViewer executableViewer;

	private Button btnModify;
	private Button btnFindInPath;
	private Button btnFindInDirectory;
	private Button btnUse;

	private final static String[] TABLE_HEADERS = { "Toolname",
			"Local executable", "Use Local" };
	private final int COL_TOOLNAME = 0;
	private final int COL_LOCAL_EXECUTABLE = 1;
	private final int COL_USE_LOCAL = 2;

	public BasePluginPreferencePage(String pluginName) {
		super();
		IPreferenceStore store = GenericNodesPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(store);
		// we do not need the apply key and do not support the restore default
		// key
		noDefaultAndApplyButton();
		this.pluginName = pluginName;

		createToolList();
	}

	private void createToolList() {
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
			for (ExternalTool tool : tools) {
				toolSettings.put(tool.getToolName(), new ExternalToolSettings(
						tool));
			}
		}
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		baseComposite = new Composite(sc, SWT.NONE);

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.V_SCROLL;

		GridLayout gl_c = new GridLayout();
		baseComposite.setLayout(gl_c);

		sc.setContent(baseComposite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		// auto discover executables
		addTopLabel();

		Composite executablesComposite = createExecutablesComposite();

		addExecutableTable(executablesComposite);
		updateTableLayout();
		addButtons(executablesComposite);

		return sc;
	}

	private Composite createExecutablesComposite() {
		Composite executablesComposite = new Composite(baseComposite, SWT.NONE);
		executablesComposite.setLayout(new GridLayout(2, false));
		executablesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				false, false, 1, 1));
		return executablesComposite;
	}

	private void addButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		btnModify = new Button(buttonComposite, SWT.NONE);
		btnModify.setText("Change");
		btnModify.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				updateSelected();
			}
		});

		// we assume that there is no selection -> we cannot modify
		btnModify.setEnabled(false);

		btnFindInPath = new Button(buttonComposite, SWT.NONE);
		btnFindInPath.setText("Find in PATH");
		btnFindInPath.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (checkOverwrite()) {
					findInPath();
				}
			}
		});

		btnFindInDirectory = new Button(buttonComposite, SWT.NONE);
		btnFindInDirectory.setText("Find in directory");
		btnFindInDirectory.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (checkOverwrite()) {
					findInDirectory();
				}
			}
		});

		btnUse = new Button(buttonComposite, SWT.NONE);
		btnUse.setText("Use");
		btnUse.setEnabled(false);
		btnUse.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("Use");
			}
		});
	}

	private void updateSelected() {
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.OPEN);
		String selection = dialog.open();

		// check if selection was aborted
		if (selection != null) {
			// check if exists and executable
			File f = new File(selection);
			if (f.exists() && f.canExecute()) {
				ExternalToolSettings settings = currentSelection();

				settings.setLocalToolPath(f.getAbsolutePath());
				if (settings.getSelectedToolPathType() == ToolPathType.UNKNOWN) {
					settings.setSelectedToolPathType(ToolPathType.USER_DEFINED);
				}
				refresh();
			}
		}
	}

	private void findInPath() {
		System.out.println("Find in PATH");
	}

	private void findInDirectory() {
		DirectoryDialog dirDialog = new DirectoryDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);

		String directory = dirDialog.open();
		if (directory != null) {
			File executableDirectory = new File(directory);

			for (Map.Entry<String, ExternalToolSettings> settingsEntry : toolSettings
					.entrySet()) {
				File executable = new File(executableDirectory, settingsEntry
						.getValue().getTool().getExecutableName());
				if (executable.exists() && executable.canExecute()) {
					settingsEntry.getValue().setLocalToolPath(
							executable.getAbsolutePath());
					if (settingsEntry.getValue().getSelectedToolPathType() == ToolPathType.UNKNOWN) {
						settingsEntry.getValue().setSelectedToolPathType(
								ToolPathType.USER_DEFINED);
					}
				}
			}
		}
		refresh();
	}

	private void updateTableLayout() {
		Table tbl = executableViewer.getTable();
		for (int i = 0; i < tbl.getColumnCount(); i++) {
			tbl.getColumn(i).pack();
		}
	}

	private void addExecutableTable(Composite parent) {
		Composite tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 1, 1));

		executableViewer = new TableViewer(tableComposite);
		executableViewer
				.setContentProvider(new ExternalToolSettingsContentProvider());
		executableViewer
				.setLabelProvider(new ExternalToolSettingsLabelProvider());
		executableViewer.setInput(toolSettings);
		Table table = executableViewer.getTable();
		new TableColumn(table, SWT.LEFT).setText(TABLE_HEADERS[COL_TOOLNAME]);
		new TableColumn(table, SWT.LEFT)
				.setText(TABLE_HEADERS[COL_LOCAL_EXECUTABLE]);
		new TableColumn(table, SWT.LEFT).setText(TABLE_HEADERS[COL_USE_LOCAL]);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		refresh();

		executableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				updateSelected();
			}
		});

		executableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						btnModify.setEnabled(true);
						ExternalToolSettings settings = currentSelection();
						btnUse.setEnabled(settings.hasShippedBinary());
					}
				});
	}

	private void addTopLabel() {

		GridLayout groupCompositeGridLayout = new GridLayout(1, false);
		groupCompositeGridLayout.marginWidth = 0;
		groupCompositeGridLayout.marginHeight = 0;
		groupCompositeGridLayout.verticalSpacing = 0;
		groupCompositeGridLayout.horizontalSpacing = 0;

		Composite groupComposite = new Composite(baseComposite, SWT.NONE);
		groupComposite.setLayout(groupCompositeGridLayout);
		groupComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		Label lblDescription = new Label(groupComposite, SWT.NONE);
		lblDescription
				.setText("Set the paths to the executable tools shipped with the plugin. ");
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
		for (Map.Entry<String, ExternalToolSettings> settingsEntry : toolSettings
				.entrySet()) {
			settingsEntry.getValue().save();
		}
	}

	/**
	 * Extracts the currently selected toolsettings object.
	 * 
	 * @return
	 */
	private ExternalToolSettings currentSelection() {
		TableItem[] item = executableViewer.getTable().getSelection();

		if (item != null && item.length > 0)
			return toolSettings.get(item[0].getText());
		else
			return null;
	}

	/**
	 * Checks if at least one of the plugin executables has a local path.
	 * 
	 * @return True if a local path was configured for at least one executable,
	 *         false otherwise.
	 */
	private boolean hasLocalPaths() {
		for (Map.Entry<String, ExternalToolSettings> settingsEntry : toolSettings
				.entrySet()) {
			if (settingsEntry.getValue().getLocalToolPath() != "")
				return true;
		}
		return false;
	}

	/**
	 * If some of the settings would be overwritten by a full-search, the user
	 * gets a warning message box and can abort the full-search.
	 * 
	 * @return True if the user accepts overwrite (or no overwrite will happen),
	 *         false otherwise.
	 */
	private boolean checkOverwrite() {
		if (hasLocalPaths()) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION
					| SWT.YES | SWT.NO);

			messageBox.setText("Warning");
			messageBox.setMessage("You already have configured local paths. "
					+ "All existing settings will be overwritten! "
					+ "Proceed?");
			int buttonID = messageBox.open();
			return buttonID == SWT.YES;
		} else {
			return true;
		}
	}

	/**
	 * Refreshs the local table.
	 */
	private void refresh() {
		executableViewer.refresh();
		updateTableLayout();
	}
}
