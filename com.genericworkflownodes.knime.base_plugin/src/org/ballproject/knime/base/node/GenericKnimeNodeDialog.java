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

package org.ballproject.knime.base.node;

import java.io.FileNotFoundException;

import org.ballproject.knime.base.config.INodeConfiguration;
import org.ballproject.knime.base.parameter.InvalidParameterValueException;
import org.ballproject.knime.base.parameter.Parameter;
import org.ballproject.knime.base.treetabledialog.MimeTypeChooserDialog;
import org.ballproject.knime.base.treetabledialog.ParameterDialog;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

public class GenericKnimeNodeDialog extends NodeDialogPane {
	private INodeConfiguration config;
	private ParameterDialog dialog;
	private MimeTypeChooserDialog mtc;

	public GenericKnimeNodeDialog(INodeConfiguration config) {
		this.config = config;
		try {
			dialog = new ParameterDialog(config);
			this.addTab("Parameters", dialog);
			mtc = new MimeTypeChooserDialog(config);
			this.addTab("OutputTypes", mtc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		for (String key : this.config.getParameterKeys()) {
			Parameter<?> param = config.getParameter(key);
			settings.addString(key, param.getStringRep());
		}

		int[] sel_ports = mtc.getSelectedTypes();

		for (int i = 0; i < this.config.getNumberOfOutputPorts(); i++) {
			settings.addInt("GENERIC_KNIME_NODES_outtype#" + i, sel_ports[i]);
		}
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			PortObjectSpec[] specs) throws NotConfigurableException {
		for (String key : this.config.getParameterKeys()) {
			Parameter<?> param = config.getParameter(key);
			String value = null;
			try {
				value = settings.getString(key);
			} catch (InvalidSettingsException e) {
				e.printStackTrace();
			}
			try {
				param.fillFromString(value);
			} catch (InvalidParameterValueException e) {
				e.printStackTrace();
				throw new NotConfigurableException(e.getMessage());
			}
		}

		int nP = this.config.getNumberOfOutputPorts();
		int[] sel_ports = new int[nP];

		for (int i = 0; i < nP; i++) {
			try {
				int idx = settings.getInt("GENERIC_KNIME_NODES_outtype#" + i);
				sel_ports[i] = idx;
			} catch (InvalidSettingsException e) {
				e.printStackTrace();
				throw new NotConfigurableException(e.getMessage());
			}
		}
		mtc.setSelectedTypes(sel_ports);
	}
}
