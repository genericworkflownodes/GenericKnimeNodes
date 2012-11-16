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

import java.io.FileNotFoundException;

import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * Dialog Model for the {@link ParameterDialog} of the generic KNIME node.
 * 
 * @author roettig, aiche
 */
public class ParameterDialogModel extends AbstractTreeTableModel implements
		TreeTableModel {

	/**
	 * Type information for the individual columns.
	 */
	private static Class<?>[] cTypes = { TreeTableModel.class, Parameter.class,
			String.class };

	/**
	 * Column name/header information.
	 */
	private static String[] cNames = { "Parameter", "Value", "Type" };

	/**
	 * The node configuration represented by this dialog model.
	 */
	private NodeConfigurationTree wrapper;
	private boolean showAdvanced = false;

	/**
	 * The {@link ParamCellEditor} instance for this DialogModel.
	 */
	private final ParamCellEditor paramCellEditor;

	public ParameterDialogModel(INodeConfiguration config)
			throws FileNotFoundException, Exception {
		NodeConfigurationTree wrapper = new NodeConfigurationTree(config, false);
		root = wrapper.getRoot();
		this.wrapper = wrapper;
		paramCellEditor = new ParamCellEditor();
	}

	public void refresh() {
		wrapper.setShowAdvanced(showAdvanced);
		wrapper.update();
		modelSupport.fireNewRoot();
	}

	public void setShowAdvanced(boolean showAdvanced) {
		this.showAdvanced = showAdvanced;
	}

	@Override
	public Object getChild(Object parent, int idx) {
		ParameterNode par = (ParameterNode) parent;
		return par.getChild(idx);
	}

	@Override
	public int getChildCount(Object parent) {
		ParameterNode par = (ParameterNode) parent;
		return par.getNumChildren();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child_) {
		ParameterNode par = (ParameterNode) parent;
		ParameterNode child = (ParameterNode) child_;
		return par.getChildIndex(child);
	}

	@Override
	public boolean isLeaf(Object parent) {
		ParameterNode par = (ParameterNode) parent;
		return par.isLeaf();
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return cTypes[column];
	}

	@Override
	public int getColumnCount() {
		return cNames.length;
	}

	@Override
	public String getColumnName(int idx) {
		return cNames[idx];
	}

	@Override
	public int getHierarchicalColumn() {
		return 0;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		ParameterNode n = (ParameterNode) node;

		if (column == -1) {
			return n.getName();
		} else if (column == 0) {
			if (n.getPayload() == null) {
				return n.getName();
			} else {
				return n.getPayload().getKey();
			}
		} else if (column == 1) {
			if (n.getPayload() == null) {
				return "";
			} else {
				return n.getPayload();
			}
		} else if (column == 2) {
			if (n.getPayload() == null) {
				return "";
			} else {
				return n.getPayload().getMnemonic();
			}
		} else if (column == 3) {
			return n.getDescription();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(Object value, int column) {
		ParameterNode n = (ParameterNode) value;
		if (column == 1) {
			if (n.isLeaf()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		ParameterNode n = (ParameterNode) node;
		n.setPayload((Parameter<?>) value);
	}

	public TableCellEditor getCellEditor() {
		return paramCellEditor;
	}

}
