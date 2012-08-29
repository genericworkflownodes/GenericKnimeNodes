/**
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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist.ItemListFillerDialog;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist.ListParameterModel;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;

/**
 * 
 * @author roettig
 */
public class ParamCellEditor extends AbstractCellEditor implements
		TableCellEditor {
	private static final long serialVersionUID = -4994391905477312605L;
	private JComboBox box;
	private JTextField field;
	private JLabel label = new JLabel("");

	private Parameter<?> param;
	private String rep;

	@Override
	public Object getCellEditorValue() {
		if (param instanceof StringParameter
				|| param instanceof DoubleParameter
				|| param instanceof IntegerParameter) {
			try {
				param.fillFromString(field.getText());
			} catch (InvalidParameterValueException e) {
				e.printStackTrace();
			}
		}
		if (param instanceof StringChoiceParameter) {
			StringChoiceParameter scp = (StringChoiceParameter) param;
			int idx = box.getSelectedIndex();
			scp.setValue(scp.getAllowedValues().get(idx));
		}
		if (param instanceof BoolParameter) {
			try {
				param.fillFromString(box.getSelectedItem().toString());
			} catch (InvalidParameterValueException e) {
				e.printStackTrace();
			}
		}
		if (param instanceof ListParameter) {
			try {
				param.fillFromString(rep);
			} catch (InvalidParameterValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return param;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		param = (Parameter<?>) value;

		if (value instanceof StringChoiceParameter) {
			StringChoiceParameter scp = (StringChoiceParameter) value;
			String[] values = new String[scp.getLabels().size()];
			int i = 0;
			for (String s : scp.getLabels()) {
				values[i++] = s;
			}
			box = new JComboBox(values);
			return box;
		}
		if (value instanceof StringParameter
				|| value instanceof DoubleParameter
				|| value instanceof IntegerParameter) {
			field = new JTextField(value.toString());
			return field;
		}
		if (value instanceof BoolParameter) {
			String[] values = new String[] { "true", "false" };
			box = new JComboBox(values);
			return box;
		}
		if (value instanceof ListParameter) {
			ListParameterModel mpm = new ListParameterModel(param);
			mpm.setSetLike(true);
			ItemListFillerDialog sd = new ItemListFillerDialog(mpm);
			sd.setVisible(true);
			String[] sel = mpm.getSelectedItems();
			ListParameter lp = (ListParameter) param;
			lp.fillFromStrings(sel);
			rep = param.getStringRep();
			return label;
		}
		return null;
	}
}