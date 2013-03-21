/**
 * Copyright (c) 2012, Björn Kahlert.
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

import javax.swing.JLabel;
import javax.swing.table.TableCellEditor;

import org.apache.commons.lang.StringUtils;

import com.genericworkflownodes.knime.generic_node.dialogs.UIHelper;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.ParamCellEditor;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * This component allows to edit lists of values in a separate window.
 * 
 * @author bkahlert, aiche
 */
public class ListEditorComponent extends JLabel {

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -9039670994475022672L;

	/**
	 * The listparameter to represent.
	 */
	private final ListParameter parameter;

	/**
	 * C'tor
	 * 
	 * @param parameter
	 *            The parameter to represent in the ListEditor.
	 * @param parent
	 *            The parent cell editor. Used to trigger
	 *            {@link TableCellEditor#stopCellEditing()} after the editor was
	 *            closed.
	 */
	public ListEditorComponent(ListParameter parameter,
			final ParamCellEditor parent) {
		super("Editing...");
		this.parameter = parameter;
		ListEditorDialog led = new ListEditorDialog(parameter);
		led.setVisible(true);

		// Trigger closing of the editor and transfer of values to the
		// underlying model.
		UIHelper.invokeDelayed(50, new Runnable() {
			@Override
			public void run() {
				parent.stopCellEditing();
			}
		});
	}

	/**
	 * Returns the adjusted value of the parameter.
	 * 
	 * @return The new value of the parameter after editing.
	 */
	public String getParameterValue() {
		return StringUtils.join(parameter.getStrings(),
				Parameter.SEPARATOR_TOKEN);
	}
}
