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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist.ListParameterModel;
import com.genericworkflownodes.knime.parameter.BoolParameter;
import com.genericworkflownodes.knime.parameter.DoubleParameter;
import com.genericworkflownodes.knime.parameter.IntegerParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.parameter.StringChoiceParameter;
import com.genericworkflownodes.knime.parameter.StringParameter;
import com.genericworkflownodes.util.StringUtils;

/**
 * The cell editor for the {@link ParameterDialog}.
 * 
 * @author roettig
 */
public class ParamCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private final class ChoiceParamActionListener<T extends Parameter<?>>
			implements ActionListener {

		/**
		 * The underlying parameter.
		 */
		private final T representedParametere;

		/**
		 * C'tor.
		 * 
		 * @param param
		 *            The underlying parameter.
		 */
		public ChoiceParamActionListener(T param) {
			representedParametere = param;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			String selectedParamValue = (String) cb.getSelectedItem();
			try {
				representedParametere.fillFromString(selectedParamValue);
			} catch (InvalidParameterValueException ex) {
				// cannot happen
				ex.printStackTrace();
			}
		}
	}

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -4994391905477312605L;

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
	 * The {@link JListEditorComponent} used for {@link ListParameter}.
	 */
	private JListEditorComponent listEditorComponent;

	/**
	 * The {@link Parameter} represented by this {@link ParamCellEditor}.
	 */
	private Parameter<?> param;

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
			String selectedValue = (String) choiceComboBox.getSelectedItem();
			scp.setValue(selectedValue);
		}
		if (param instanceof BoolParameter) {
			try {
				param.fillFromString(choiceComboBox.getSelectedItem()
						.toString());
			} catch (InvalidParameterValueException e) {
				e.printStackTrace();
			}
		}
		if (param instanceof ListParameter) {
			String workaround = StringUtils.join(Arrays
					.asList(listEditorComponent.getModel().getSelectedItems()),
					Parameter.SEPARATOR_TOKEN);
			try {
				param.fillFromString(workaround);
			} catch (InvalidParameterValueException e) {
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
			choiceComboBox = new JComboBox(values);

			// we need to make sure that we catch all edit operations.
			choiceComboBox
					.addActionListener(new ChoiceParamActionListener<StringChoiceParameter>(
							scp));
			choiceComboBox.setSelectedItem(scp.getValue());
			return choiceComboBox;
		}
		if (value instanceof StringParameter
				|| value instanceof DoubleParameter
				|| value instanceof IntegerParameter) {
			field = new JTextField(value.toString());
			return field;
		}
		if (value instanceof BoolParameter) {
			String[] values = new String[] { "true", "false" };
			choiceComboBox = new JComboBox(values);
			choiceComboBox
					.addActionListener(new ChoiceParamActionListener<BoolParameter>(
							(BoolParameter) param));
			return choiceComboBox;
		}
		if (value instanceof ListParameter) {
			ListParameterModel listParameterModel = new ListParameterModel(
					param);
			listParameterModel.setSetLike(true);

			this.listEditorComponent = new JListEditorComponent(
					listParameterModel);

			return this.listEditorComponent;
		}
		return null;
	}
}