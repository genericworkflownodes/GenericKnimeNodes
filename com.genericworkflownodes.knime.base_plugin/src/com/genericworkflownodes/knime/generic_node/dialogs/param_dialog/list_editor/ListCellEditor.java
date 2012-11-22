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
package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.list_editor;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleListParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;

/**
 * @author aiche
 * 
 */
public class ListCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	/**
	 * The abstracted parameter.
	 */
	private ListParameter parameter;

	/**
	 * C'tor.
	 * 
	 * @param parameter
	 *            The parameter to represent.
	 */
	public ListCellEditor(ListParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * The {@link JComboBox} used for {@link StringChoiceParameter} and
	 * {@link BoolParameter}.
	 */
	private JComboBox choiceComboBox;

	/**
	 * The {@link JTextField} used for {@link StringParameter},
	 * {@link DoubleParameter}, and {@link IntegerParameter}.
	 */
	private JTextField field;

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -3482419372241324327L;

	/**
	 * Check if the added value is valid w.r.t. to possible restrictions.
	 * 
	 * @return
	 */
	private boolean validate() {
		if (parameter instanceof DoubleListParameter) {
			return true; // TODO: Handle restrictions
		} else if (parameter instanceof IntegerListParameter) {
			return true; // TODO: Handle restrictions
		} else {
			return true;
		}
	}

	@Override
	public Object getCellEditorValue() {
		if (parameter instanceof StringListParameter) {
			StringListParameter slp = (StringListParameter) parameter;
			if (slp.getRestrictions() != null
					&& slp.getRestrictions().length > 0) {
				return choiceComboBox.getSelectedItem();
			} else {
				return field.getText();
			}
		} else {
			if (validate()) {
				return field.getText();
			} else {
				return null;
			}
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		if (parameter instanceof StringListParameter) {
			StringListParameter slp = (StringListParameter) parameter;
			if (slp.getRestrictions() != null
					&& slp.getRestrictions().length > 0) {
				choiceComboBox = new JComboBox(slp.getRestrictions());
				choiceComboBox.setSelectedItem(value);
				return choiceComboBox;
			} else {
				field = new JTextField(value.toString());
				return field;
			}
		} else {
			field = new JTextField(value.toString());
			return field;
		}
	}
}
