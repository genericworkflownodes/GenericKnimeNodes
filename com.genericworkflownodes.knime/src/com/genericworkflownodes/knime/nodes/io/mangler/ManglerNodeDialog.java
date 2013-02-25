/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.mangler;

import javax.swing.DefaultComboBoxModel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;

import com.genericworkflownodes.knime.mime.demangler.IDemangler;
import com.genericworkflownodes.util.ui.ChoiceDialog;
import com.genericworkflownodes.util.ui.ChoiceDialogListener;

/**
 * <code>NodeDialog</code> for the "Mangler" Node.
 * 
 * @author aiche, roettig
 */
public class ManglerNodeDialog extends NodeDialogPane implements
		ChoiceDialogListener {

	/**
	 * The ChoiceElement to select the correct {@link IDemangler}.
	 */
	private ChoiceDialog choice;

	/**
	 * The DataModel for the ChoiceDialog.
	 */
	private DefaultComboBoxModel model;

	/**
	 * The actual selected demangler.
	 */
	private String demanglerClassName;

	/**
	 * Available manglers.
	 */
	private String[] availableManglers;

	/**
	 * New pane for configuring Mangler node dialog. This is just a suggestion
	 * to demonstrate possible default dialog components.
	 */
	protected ManglerNodeDialog(Object obj) {
		super();

		model = new DefaultComboBoxModel();

		choice = new ChoiceDialog(model);
		choice.registerChoiceListener(this);

		addTab("Demanglers", choice);

		// we assume there is no demangler selected
		demanglerClassName = "";
		availableManglers = null;
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		settings.addString(ManglerNodeModel.SELECTED_DEMANGLER_SETTINGNAME,
				demanglerClassName);
		settings.addStringArray(
				ManglerNodeModel.AVAILABLE_MIMETYPE_SETTINGNAME,
				availableManglers);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		String demanglerClassName = settings.getString(
				ManglerNodeModel.SELECTED_DEMANGLER_SETTINGNAME, "");

		availableManglers = settings.getStringArray(
				ManglerNodeModel.AVAILABLE_MIMETYPE_SETTINGNAME,
				new String[] {});

		model.removeAllElements();
		for (String d : availableManglers) {
			model.addElement(d);
		}

		// select already configured demangler -> find by class name
		if (!"".equals(demanglerClassName)) {
			int indexToSelect = model.getIndexOf(demanglerClassName);
			if (indexToSelect != -1) {
				model.setSelectedItem(demanglerClassName);
			}
		} else {
			// there is no pre-selected demangler
			model.setSelectedItem(model.getElementAt(0));
		}
	}

	@Override
	public void onChoice(final int selectedIdx) {
		demanglerClassName = (String) model.getElementAt(selectedIdx);
	}
}