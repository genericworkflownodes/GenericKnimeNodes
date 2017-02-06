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

/**
 * Dialog Model for the {@link ParameterDialog} of the generic KNIME node.
 * 
 * @author roettig, aiche
 */

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.NodeConfigurationTree;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.ParameterNode;

public class ParameterDialogTreeModel implements TreeModel{

        /**
         * The node configuration represented by this dialog model.
         */
        private NodeConfigurationTree wrapper;

        /**
         * Should advanced parameters be visible.
         */
        private boolean showAdvanced = false;

        /**
         * The {@link ParamCellEditor} instance for this DialogModel.
         */
        private final ParamCellEditor paramCellEditor;
        
        /**
         * The {@link ParamCellRenderer} instance for this DialogModel.
         */
        private final ParamCellRenderer paramCellRenderer;

        /**
         * Create ParameterDialogModel from a given NodeConfiguration.
         * 
         * @param config
         *            The configuration that should be represented by the
         *            ParameterDialogModel.
         */
        public ParameterDialogTreeModel(INodeConfiguration config) {
            wrapper = new NodeConfigurationTree(config, false);
            paramCellEditor = new ParamCellEditor();
            paramCellRenderer = new ParamCellRenderer();
        }

        /**
         * Triggers a refresh of the gui.
         */
        public void refresh() {
            wrapper.setShowAdvanced(showAdvanced);
            wrapper.update();
        }

        /**
         * Set if advanced parameters should be shown or not.
         * 
         * @param newShowAdvanced
         *            The new state of showAdvanced.
         */
        public void setShowAdvanced(boolean newShowAdvanced) {
            showAdvanced = newShowAdvanced;
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
        public int getIndexOfChild(Object oParent, Object oChild) {
            ParameterNode parent = (ParameterNode) oParent;
            ParameterNode child = (ParameterNode) oChild;
            return parent.getChildIndex(child);
        }

        @Override
        public boolean isLeaf(Object oParent) {
            ParameterNode parent = (ParameterNode) oParent;
            return parent.isLeaf();
        }

        /**
         * Gives access to the underlying table cell editor.
         * 
         * @return The TableCellEditor.
         */
        public TableCellEditor getCellEditor() {
            return paramCellEditor;
        }
        /**
         * Gives access to the underlying table cell renderer.
         * 
         * @return The TableCellRenderer.
         */
        public TableCellRenderer getCellRenderer() {
            return paramCellRenderer;
        }


    @Override
    public Object getRoot() {
        return wrapper.getRoot();
    }


    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub
        
    }

}
