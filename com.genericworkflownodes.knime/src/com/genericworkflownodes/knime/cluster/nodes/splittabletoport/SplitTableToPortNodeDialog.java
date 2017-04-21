package com.genericworkflownodes.knime.cluster.nodes.splittabletoport;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.util.ColumnFilter;

import com.genericworkflownodes.knime.base.data.port.PortObjectHandlerCell;

public class SplitTableToPortNodeDialog extends DefaultNodeSettingsPane {

    public SplitTableToPortNodeDialog() {
        DialogComponentColumnNameSelection fileCol = new DialogComponentColumnNameSelection(
                SplitTableToPortNodeModel.createFileColumnSettingsModel(), "File column", 0, new ColumnFilter() {
            
            @Override
            public boolean includeColumn(DataColumnSpec colSpec) {
                return colSpec.getType().equals(PortObjectHandlerCell.TYPE);
            }
            
            @Override
            public String allFilteredMsg() {
                return "No file column is available";
            }
        });
        addDialogComponent(fileCol);
    }
    
}
