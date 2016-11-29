package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import org.netbeans.swing.outline.RowModel;

import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.ParameterNode;
import com.genericworkflownodes.knime.parameter.Parameter;

public class ParameterDialogRowModel implements RowModel{

    /**
     * Type information for the individual columns.
     */
    private static Class<?>[] cTypes = { /*ParameterDialogTreeModel.class,*/ Parameter.class,
            String.class };
    /**
     * Column name/header information.
     */
    private static String[] cNames = { /*"Parameter",*/ "Value", "Type" };
    
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
        /*} else if (column == 0) {
            if (n.getPayload() == null) {
                return n.getName();
            } else {
                return n.getPayload().getKey();
            }*/
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
        } else if (column == 2) {
            return n.getDescription();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object value, int column) {
        ParameterNode n = (ParameterNode) value;
        return (column == 1 && n.isLeaf());
    }

    @Override
    public void setValueFor(Object node, int column, Object value) {
        ParameterNode n = (ParameterNode) node;
        n.setPayload((Parameter<?>) value);
    }

}
