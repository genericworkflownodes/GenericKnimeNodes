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
import com.genericworkflownodes.knime.toolfinderservice.IToolLocator.ToolPathType;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator;

/**
 * This class provides a base implementation of a plugin defaults preference
 * page for derived plugins. It gives the opportunity to define the paths to the
 * plugin's tool executables.
 * 
 * @author aiche, bkahlert
 */
public abstract class BasePluginPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    private static final String TOOLTIP_TABLE = "List of binaries shipped with the %s plugin.";

    private static final String TOOLTIP_CHANGE_EXECUTABLE = "Change the executable associated to the selected node.";

    private static final String TOOLTIP_SEARCH_IN_PATH = "Searches the systems PATH for the executables.";

    private static final String TOOLTIP_SWITCH_TO_SHIPPED = "Switch to shipped binary.";

    private static final String TOOLTIP_SWITCH_TO_LOCAL = "Switch to local binary.";

    private static final String TOOLTIP_SEARCH_DIRECTORY = "Searches a user defined directory for the executables.";

    private static final String USE_SHIPPED_TEXT = "Use shipped";

    private static final String USE_LOCAL_TEXT = "Use local";

    /**
     * The list of all available tool pathes.
     */
    private final LinkedHashMap<String, ExternalToolSettings> toolSettings = new LinkedHashMap<String, ExternalToolSettings>();

    /**
     * Plugin name.
     */
    private final String m_pluginName;

    /**
     * The base composite holding all the content.
     */
    private Composite m_baseComposite;

    // private Table executableTable;
    /**
     * The executable viewer.
     */
    private TableViewer m_executableViewer;

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
        m_pluginName = pluginName;

        createToolList();
    }

    private void createToolList() {

        Map<String, List<ExternalTool>> plugin2tools = PluginPreferenceToolLocator
                .getToolLocatorService().getToolsByPlugin();

        List<ExternalTool> tools = plugin2tools.get(m_pluginName);

        // sort each plugin by name
        Collections.sort(tools, new Comparator<ExternalTool>() {
            @Override
            public int compare(final ExternalTool o1, final ExternalTool o2) {
                return o1.getToolName().compareToIgnoreCase(o2.getToolName());
            }
        });

        // add each tool shipped with the current plugin to the GUI
        for (ExternalTool tool : tools) {
            toolSettings
                    .put(tool.getToolName(), new ExternalToolSettings(tool));
        }
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
        sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        m_baseComposite = new Composite(sc, SWT.NONE);

        m_baseComposite.setLayout(new GridLayout(1, false));
        m_baseComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                false, 1, 1));

        sc.setContent(m_baseComposite);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        addTopLabel();

        Composite executablesComposite = createExecutablesComposite();

        addExecutableTable(executablesComposite);
        updateTableLayout();
        addButtons(executablesComposite);

        return sc;
    }

    private Composite createExecutablesComposite() {
        Composite executablesComposite = new Composite(m_baseComposite,
                SWT.NONE);
        executablesComposite.setLayout(new GridLayout(2, false));
        executablesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
                true, true, 1, 1));
        return executablesComposite;
    }

    private void addButtons(Composite parent) {
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
        buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
                false, 1, 1));
        btnModify = new Button(buttonComposite, SWT.NONE);
        btnModify.setToolTipText(TOOLTIP_CHANGE_EXECUTABLE);
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
        btnFindInPath.setToolTipText(TOOLTIP_SEARCH_IN_PATH);
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
        btnFindInDirectory.setToolTipText(TOOLTIP_SEARCH_DIRECTORY);
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
        btnUse.setToolTipText(TOOLTIP_SWITCH_TO_LOCAL);
        btnUse.setText(USE_LOCAL_TEXT);
        btnUse.setEnabled(false);
        btnUse.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                ExternalToolSettings settings = currentSelection();
                if (settings.getSelectedToolPathType() == ToolPathType.SHIPPED) {
                    settings.setSelectedToolPathType(ToolPathType.USER_DEFINED);
                    btnUse.setText(USE_SHIPPED_TEXT);
                    btnUse.setToolTipText(TOOLTIP_SWITCH_TO_SHIPPED);
                } else {
                    settings.setSelectedToolPathType(ToolPathType.SHIPPED);
                    btnUse.setText(USE_LOCAL_TEXT);
                    btnUse.setToolTipText(TOOLTIP_SWITCH_TO_LOCAL);
                }
                refresh();
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
                settings.setSelectedToolPathType(ToolPathType.USER_DEFINED);
                refresh();
            }
        }
    }

    /**
     * Iterate through all entries in the path to find the executables.
     */
    private void findInPath() {
        String systemPath = System.getenv("PATH");
        String[] pathEntries = systemPath.split(File.pathSeparator);

        for (Map.Entry<String, ExternalToolSettings> settingsEntry : toolSettings
                .entrySet()) {
            for (String path : pathEntries) {
                boolean found = updateToolWithDirectory(new File(path),
                        settingsEntry.getValue());
                if (found)
                    break;
            }
        }
        refresh();
    }

    /**
     * Ask the user for a directory where to search for the executables.
     */
    private void findInDirectory() {
        DirectoryDialog dirDialog = new DirectoryDialog(PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);

        String directory = dirDialog.open();
        if (directory != null) {
            File executableDirectory = new File(directory);

            for (Map.Entry<String, ExternalToolSettings> settingsEntry : toolSettings
                    .entrySet()) {
                updateToolWithDirectory(executableDirectory,
                        settingsEntry.getValue());
            }
        }
        refresh();
    }

    /**
     * Searches the tool described by setting in the given directory and returns
     * true if it found the executable, false otherwise.
     * 
     * @param executableDirectory
     *            The directory where to search for the tool.
     * @param setting
     *            The settings object describing the tool to search for.
     * @return True if it found the executable, false otherwise.
     */
    private boolean updateToolWithDirectory(File executableDirectory,
            ExternalToolSettings setting) {
        File executable = new File(executableDirectory, setting.getTool()
                .getExecutableName());
        if (executable.exists() && executable.canExecute()) {
            setting.setLocalToolPath(executable.getAbsolutePath());
            setting.setSelectedToolPathType(ToolPathType.USER_DEFINED);
            return true;
        }
        return false;
    }

    private void addExecutableTable(Composite parent) {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayout(new GridLayout(1, false));
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                false, 1, 1));

        m_executableViewer = new TableViewer(
                tableComposite,
                (SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
        m_executableViewer
                .setContentProvider(new ExternalToolSettingsContentProvider());
        m_executableViewer
                .setLabelProvider(new ExternalToolSettingsLabelProvider());
        m_executableViewer.setInput(toolSettings);
        Table table = m_executableViewer.getTable();
        table.setToolTipText(String.format(TOOLTIP_TABLE, m_pluginName));
        new TableColumn(table, SWT.LEFT).setText(TABLE_HEADERS[COL_TOOLNAME]);
        new TableColumn(table, SWT.LEFT)
                .setText(TABLE_HEADERS[COL_LOCAL_EXECUTABLE]);
        new TableColumn(table, SWT.LEFT).setText(TABLE_HEADERS[COL_USE_LOCAL]);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        refresh();

        m_executableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                updateSelected();
            }
        });

        m_executableViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        btnModify.setEnabled(true);
                        ExternalToolSettings settings = currentSelection();
                        if (settings != null) {
                            btnUse.setEnabled(settings.hasShippedBinary());

                            if (settings.getSelectedToolPathType() == ToolPathType.SHIPPED) {
                                btnUse.setText(USE_LOCAL_TEXT);
                            } else {
                                btnUse.setText(USE_SHIPPED_TEXT);
                            }
                        } else {
                            // if we have no selection we disable the selection
                            // specific buttons again
                            btnUse.setEnabled(false);
                            btnModify.setEnabled(false);
                        }
                    }
                });

        // we want some min/max height here
        int minHeight = 20 * table.getItemHeight();

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = minHeight;
        m_executableViewer.getControl().setLayoutData(gridData);
    }

    private void addTopLabel() {

        Composite groupComposite = new Composite(m_baseComposite, SWT.NONE);
        groupComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

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
        TableItem[] item = m_executableViewer.getTable().getSelection();

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
            if (!"".equals(settingsEntry.getValue().getLocalToolPath()))
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
        m_executableViewer.refresh();
        updateTableLayout();
    }

    private void updateTableLayout() {
        Table tbl = m_executableViewer.getTable();
        for (int i = 0; i < tbl.getColumnCount(); i++) {
            tbl.getColumn(i).pack();
        }
    }
}
