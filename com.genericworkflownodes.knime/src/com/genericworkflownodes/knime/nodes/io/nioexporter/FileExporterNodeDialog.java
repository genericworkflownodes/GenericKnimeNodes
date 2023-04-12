package com.genericworkflownodes.knime.nodes.io.nioexporter;

import java.awt.Component;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.filehandling.core.data.location.variable.FSLocationVariableType;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.DialogComponentWriterFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.SettingsModelWriterFileChooser;
import org.knime.filehandling.core.util.GBCBuilder;

final class FileExporterNodeDialog extends NodeDialogPane {

    private final DialogComponentWriterFileChooser m_writer;

    FileExporterNodeDialog(final PortsConfiguration portsConfig, final String connectionInputPortGrpName) {
        final FileExporterSettings settings = new FileExporterSettings(portsConfig, connectionInputPortGrpName);
        final SettingsModelWriterFileChooser writerModel = settings.getWriterModel();
        writerModel.setCreateMissingFolders(true);
        final FlowVariableModel fvm =
            createFlowVariableModel(writerModel.getKeysForFSLocation(), FSLocationVariableType.INSTANCE);
        m_writer = new DialogComponentWriterFileChooser(writerModel, connectionInputPortGrpName, fvm);
        addTab("Settings", createPanel());
    }

    private Component createPanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final GBCBuilder gbc = new GBCBuilder().weight(1, 0).anchorFirstLineStart().fillHorizontal();
        p.add(m_writer.getComponentPanel(), gbc.build());
        p.add(new JPanel(), gbc.incY().weight(0, 1).fillVertical().build());
        return p;
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_writer.saveSettingsTo(settings);
    }

    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_writer.loadSettingsFrom(settings, specs);
    }

    @Override
    public void onClose() {
        m_writer.onClose();
        super.onClose();
    }

}