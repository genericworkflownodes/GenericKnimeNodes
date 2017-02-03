/**
 * Copyright (c) 2011, Marc Röttig.
 * Copyright (c) 2012-2014, Stephan Aiche.
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

import org.netbeans.swing.outline.RowModel;

import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.ParameterNode;
import com.genericworkflownodes.knime.parameter.Parameter;

public class ParameterDialogRowModel implements RowModel{

    /**
     * Type information for the individual columns.
     */
    private static Class<?>[] cTypes = {Parameter.class, String.class };
    /**
     * Column name/header information.
     */
    private static String[] cNames = {"Value", "Type" };
    
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
    public Object getValueFor(Object node, int column) {
        ParameterNode n = (ParameterNode) node;

        if (column == -1) {
            return n.getName();
        } else if (column == 0) {
            if (n.getPayload() == null) {
                return "";
            } else {
                return n.getPayload();
            }
        } else if (column == 1) {
            if (n.getPayload() == null) {
                return "";
            } else {
                return n.getPayload().getMnemonic();
            }
        // Hidden column storing the description.
        // Gets an own textbox below.
        } else if (column == 2) {
            return n.getDescription();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object value, int column) {
        ParameterNode n = (ParameterNode) value;
        // Only the first (= param value column) is editable
        // Not on parameter subsection descriptors
        return (column == 0 && n.isLeaf());
    }

    @Override
    public void setValueFor(Object node, int column, Object value) {
        ParameterNode n = (ParameterNode) node;
        n.setPayload((Parameter<?>) value);
    }

}
