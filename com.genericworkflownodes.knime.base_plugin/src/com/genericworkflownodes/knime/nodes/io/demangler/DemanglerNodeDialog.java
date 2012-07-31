/*
 * Copyright (c) 2011, Marc Röttig.
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

package com.genericworkflownodes.knime.nodes.io.demangler;

import javax.swing.DefaultComboBoxModel;

import org.ballproject.knime.base.ui.choice.ChoiceDialog;
import org.ballproject.knime.base.ui.choice.ChoiceDialogListener;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

/**
 * <code>NodeDialog</code> for the "Demangler" Node.
 * 
 * 
 * @author roettig
 */
public class DemanglerNodeDialog extends NodeDialogPane implements
		ChoiceDialogListener {

	private ChoiceDialog choice;
	private DefaultComboBoxModel model = new DefaultComboBoxModel();

	protected DemanglerNodeDialog() {
		super();
		choice = new ChoiceDialog(model);
		choice.registerChoiceListener(this);
		this.addTab("Demanglers", choice);
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		settings.addInt("selected_index", idx);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			PortObjectSpec[] specs) throws NotConfigurableException {
		String[] demanglers = null;
		try {
			demanglers = settings.getStringArray("demanglers");
		} catch (InvalidSettingsException e) {
			e.printStackTrace();
		}

		model.removeAllElements();
		for (String d : demanglers) {
			model.addElement(d);
		}
	}

	private int idx = 0;

	@Override
	public void onChoice(int sel_idx) {
		idx = sel_idx;
	}
}