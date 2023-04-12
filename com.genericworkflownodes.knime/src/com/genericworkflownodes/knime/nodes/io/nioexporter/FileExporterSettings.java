package com.genericworkflownodes.knime.nodes.io.nioexporter;

import java.util.EnumSet;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.defaultnodesettings.EnumConfig;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.FileOverwritePolicy;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.SettingsModelWriterFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filtermode.SettingsModelFilterMode.FilterMode;

/**
 * The settings of the File Exporter node.
 *
 * @author jpfeuffer
 */
final class FileExporterSettings {

    private static final String CFG_FILE_CHOOSER = "folder_selection";

    private final SettingsModelWriterFileChooser m_writer;

    FileExporterSettings(final PortsConfiguration portsConfig, final String fsPortIdentifier) {
        m_writer = new SettingsModelWriterFileChooser(CFG_FILE_CHOOSER, portsConfig, fsPortIdentifier,
            EnumConfig.create(FilterMode.FOLDER),
            EnumConfig.create(FileOverwritePolicy.FAIL, FileOverwritePolicy.OVERWRITE),
            EnumSet.of(FSCategory.LOCAL, FSCategory.MOUNTPOINT, FSCategory.RELATIVE));
        m_writer.setCreateMissingFolders(true);
    }

    SettingsModelWriterFileChooser getWriterModel() {
        return m_writer;
    }

    void saveSettingsInModel(final NodeSettingsWO settings) {
        m_writer.saveSettingsTo(settings);
    }

    void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_writer.validateSettings(settings);
    }

    void loadSettingsInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_writer.loadSettingsFrom(settings);
    }
}
