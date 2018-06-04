package com.genericworkflownodes.knime.cluster.nodes.splittabletoport;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;

import com.genericworkflownodes.knime.base.data.port.FileStoreValue;

public class SplitTableToPortNodeDialog extends DefaultNodeSettingsPane {

    public SplitTableToPortNodeDialog() {
        DialogComponentColumnNameSelection fileCol = new DialogComponentColumnNameSelection(
                SplitTableToPortNodeModel.createFileColumnSettingsModel(), "File column", 0, FileStoreValue.class);
        addDialogComponent(fileCol);
    }    
}
