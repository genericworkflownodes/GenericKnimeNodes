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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class MissingBinariesDialog extends Dialog {

	/**
	 * The bundle with missing binaries.
	 */
	private final String m_bundleName;

	/**
	 * The id of the preference page to open if there are missing binaries.
	 */
	private final String m_preferencePageId;

	/**
	 * The checkbox to disable this warning.
	 */
	private Button btnDoNotShow;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public MissingBinariesDialog(Shell parentShell, final String bundleName,
			final String preferencePageId) {
		super(parentShell);
		m_bundleName = bundleName;
		m_preferencePageId = preferencePageId;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginWidth = 15;
		gl_container.marginHeight = 15;
		container.setLayout(gl_container);

		// The warning icon
		Label lblWarning = new Label(container, SWT.NONE);
		lblWarning.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1));
		lblWarning.setImage(Display.getCurrent().getSystemImage(
				SWT.ICON_WARNING));

		// The textfield containing the link to the preference dialog
		Link linkMissingBinariesPlease = new Link(container, SWT.NONE);
		linkMissingBinariesPlease.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, true, true, 1, 1));
		linkMissingBinariesPlease
				.setText(String
						.format("Not all nodes of the plugin %s are associated with a valid binary. Please check the <a>%s</a> preference page.",
								m_bundleName, m_bundleName));
		linkMissingBinariesPlease.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (m_bundleName.equals(event.text)) {
					PreferenceDialog prefDialog = PreferencesUtil
							.createPreferenceDialogOn(getShell(),
									m_preferencePageId, null, null);

					prefDialog.setErrorMessage(String
							.format("Not all nodes of the plugin %s are associated with a valid binary.",
									m_bundleName));

					// Open the Pref dialog
					prefDialog.open();

					// close this window
					MissingBinariesDialog.this.close();
				}
			}
		});
		new Label(container, SWT.NONE);

		// The check box for do not show again
		btnDoNotShow = new Button(container, SWT.CHECK);
		btnDoNotShow.setSelection(true);
		btnDoNotShow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnDoNotShow.getSelection())
					System.out.println("Show this warning again.");
				else
					System.out.println("Do not show this warning again.");
			}
		});
		btnDoNotShow.setText("Show this warning again.");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(478, 190);
	}

}
