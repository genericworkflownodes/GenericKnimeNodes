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

import org.ballproject.knime.GenericNodesPlugin;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService.ToolPathType;

/**
 * This class bundles all GUI elements and there associated behavior to
 * visualize the combination of FileFieldEditor and check mark for using the
 * shipped binary.
 * 
 * @author aiche
 */
public class ToolFieldEditor extends FileFieldEditor {

	private static String CHECK_BOX_TEXT = "use embedded";
	private static String CHECK_BOX_UNAVAILABLE_EMBEDDED_TEXT = "(no executable contained in the plugin)";

	private ExternalTool tool;
	private Button checkBox;

	private Composite parent;

	public ToolFieldEditor(ExternalTool tool, Composite c) {
		super(tool.getKey() + IToolLocatorService.ToolPathType.USER_DEFINED,
				tool.getToolName(), c);
		this.tool = tool;
		this.parent = c;
	}

	@Override
	public void load() {
		loadUserDefinedExecutablePath();
		checkBox.setEnabled(hasShippedBinary());
		if (!hasShippedBinary()) {
			checkBox.setText(CHECK_BOX_UNAVAILABLE_EMBEDDED_TEXT);
		}
		checkBox.setSelection(isShippedSelected());
		this.setEnabled(!isShippedSelected(), parent);
	}

	/**
	 * Load the executable path from the {@link IToolLocatorService} into the
	 * FieldEditor.
	 */
	private void loadUserDefinedExecutablePath() {
		// we load the path from the IToolLocatorService
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {
			try {
				File executable = toolLocator.getToolPath(tool,
						ToolPathType.USER_DEFINED);
				if (executable != null && executable.exists()
						&& executable.isFile() && executable.canExecute()) {
					this.setStringValue(executable.getAbsolutePath());
				}
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

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);

		// add another boolean flag below the FileFieldEditor
		checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText(CHECK_BOX_TEXT);
		checkBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				numColumns, 1));
		checkBox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeToolType();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	}

	/**
	 * Returns true if the binary selected in the {@link IToolLocatorService} is
	 * the shipped one.
	 * 
	 * @return True if the shipped binary is selected, false if it is not
	 *         selected or no binary was shipped with the plugin.
	 */
	private boolean isShippedSelected() {
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		boolean isShippedSelected = false;

		if (toolLocator != null) {
			try {
				isShippedSelected = toolLocator.getConfiguredToolPathType(tool) == ToolPathType.SHIPPED;
			} catch (Exception e) {
				GenericNodesPlugin.log("Could not load tool status for tool: "
						+ tool);
				GenericNodesPlugin.log(e.getMessage());
			}
		} else {
			GenericNodesPlugin
					.log("Unable to get service: IToolLocatorService");
		}

		return isShippedSelected;
	}

	/**
	 * Checks if a binary was shipped in the plugin for the given tool.
	 * 
	 * @return
	 */
	private boolean hasShippedBinary() {
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {
			return toolLocator.hasValidToolPath(tool, ToolPathType.SHIPPED);
		} else {
			GenericNodesPlugin
					.log("Unable to get service: IToolLocatorService");
			return false;
		}
	}

	/**
	 * Changes the type of the tool to the selected value
	 */
	private void changeToolType() {
		// boolean useShipped = checkBox.getSelection();
		this.setEnabled(!checkBox.getSelection(), parent);
		/*
		 * IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
		 * .getWorkbench().getService(IToolLocatorService.class);
		 * 
		 * if (toolLocator != null) { toolLocator.updateToolPathType(tool,
		 * (useShipped ? ToolPathType.SHIPPED : ToolPathType.USER_DEFINED));
		 * this.setEnabled(!checkBox.getSelection(), parent); } else {
		 * GenericNodesPlugin
		 * .log("Unable to get service: IToolLocatorService"); }
		 */
	}

	/**
	 * Stores the current state of the {@link ToolFieldEditor} in the
	 * {@link IToolLocatorService}.
	 */
	@Override
	public void store() {
		updateUserDefinedExecutable();
		updateToolChoice();
	}

	/**
	 * Sets the choice value stored in the IToolLocatorService to the customized
	 * values.
	 */
	private void updateToolChoice() {
		boolean useShipped = checkBox.getSelection();
		boolean isValdidUserDefinedExecutable = isValidUserDefinedExecutable();

		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		if (toolLocator != null) {
			if (useShipped) {
				toolLocator.updateToolPathType(tool, ToolPathType.SHIPPED);
			} else if (isValdidUserDefinedExecutable) {
				toolLocator.updateToolPathType(tool, ToolPathType.USER_DEFINED);
			} else {
				toolLocator.updateToolPathType(tool, ToolPathType.UNKNOWN);
			}
		} else {
			GenericNodesPlugin
					.log("Unable to get service: IToolLocatorService");
		}

	}

	/**
	 * Checks if the executable selected by the user is valid (exists,
	 * canExecute, ..)
	 * 
	 * @return
	 */
	private boolean isValidUserDefinedExecutable() {
		File userDefinedExecutable = new File(this.getStringValue());
		return (userDefinedExecutable != null && userDefinedExecutable.exists() && userDefinedExecutable
				.canExecute());
	}

	/**
	 * Stores the value of the user-defined executable in the
	 * {@link IToolLocatorService}.
	 */
	private void updateUserDefinedExecutable() {
		File userDefinedExecutable = new File(this.getStringValue());
		if (isValidUserDefinedExecutable()) {
			IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
					.getWorkbench().getService(IToolLocatorService.class);

			if (toolLocator != null) {
				toolLocator.setToolPath(tool, userDefinedExecutable,
						ToolPathType.USER_DEFINED);
			} else {
				GenericNodesPlugin
						.log("Unable to get service: IToolLocatorService");
			}

		}
	}
}
