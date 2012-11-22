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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.genericworkflownodes.knime.parameter.DoubleListParameter;
import com.genericworkflownodes.knime.parameter.IntegerListParameter;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.StringListParameter;

public class ListEditorDialogModel extends AbstractTableModel {

	List<String> values = new ArrayList<String>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 4088143466814653855L;

	/**
	 * 
	 */
	private ListParameter parameter;

	public ListEditorDialogModel(ListParameter p) {
		parameter = p;
		for (String s : parameter.getStrings())
			values.add(s);
	}

	@Override
	public int getRowCount() {
		return values.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return values.get(rowIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public int addValue() {
		// choose reasonable value to insert
		String newValue = "";
		if (parameter instanceof DoubleListParameter) {
			DoubleListParameter dlp = (DoubleListParameter) parameter;
			if (dlp.getLowerBound() != Double.NEGATIVE_INFINITY
					|| dlp.getUpperBound() != Double.POSITIVE_INFINITY) {
				newValue = (dlp.getLowerBound() != Double.NEGATIVE_INFINITY ? dlp
						.getLowerBound().toString() : dlp.getUpperBound()
						.toString());
			} else {
				newValue = "0.0";
			}
		} else if (parameter instanceof IntegerListParameter) {
			IntegerListParameter ilp = (IntegerListParameter) parameter;
			if (ilp.getLowerBound() != Integer.MIN_VALUE
					|| ilp.getUpperBound() != Integer.MAX_VALUE) {
				newValue = (ilp.getLowerBound() != Integer.MIN_VALUE ? ilp
						.getLowerBound().toString() : ilp.getUpperBound()
						.toString());
			} else {
				newValue = "0";
			}
		} else if (parameter instanceof StringListParameter
				&& ((StringListParameter) parameter).getRestrictions() != null) {
			String[] validValues = ((StringListParameter) parameter)
					.getRestrictions();
			int i = 0;
			while ("".equals(newValue) && i < validValues.length) {
				newValue = validValues[i];
				++i;
			}
		}
		values.add(newValue);
		fireTableRowsInserted(values.size(), values.size());
		return values.size() - 1;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		values.set(rowIndex, aValue.toString());
	}
}
