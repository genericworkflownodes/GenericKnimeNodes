/**
 * Copyright (c) 2022, OpenMS Team.
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
package com.genericworkflownodes.knime.nodes.io.nioimporter;

/**
 * <code>NodeConfiguration</code> for the "MimeFileImporter" Node.
 *
 * @author jpfeuffer
 */
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.SettingsModelReaderFileChooser;

final class MimeFileNioImporterNodeConfiguration {

    private static final String CFG_OVERWRITE_EXT = "FILE_EXTENSION";

    private SettingsModelOptionalString m_overwriteFileExtension;
    
    private final SettingsModelReaderFileChooser m_fileChooserSettings;

    MimeFileNioImporterNodeConfiguration(final SettingsModelReaderFileChooser fileChooserSettings) {
        m_fileChooserSettings = fileChooserSettings;
        m_overwriteFileExtension = new SettingsModelOptionalString(CFG_OVERWRITE_EXT, "", false);
    }
    
    SettingsModelOptionalString overwriteFileExtension() {
        return m_overwriteFileExtension;
    }

    void overwriteFileExtension(final String overwriteFileExtension) {
        m_overwriteFileExtension.setStringValue(overwriteFileExtension);
    }

    SettingsModelReaderFileChooser getFileChooserSettings() {
        return m_fileChooserSettings;
    }

    void loadSettingsForDialog(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_fileChooserSettings.loadSettingsFrom(settings);
    }

    void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_fileChooserSettings.loadSettingsFrom(settings);
        loadSettingsForDialog(settings);
    }

    void saveSettingsForDialog(final NodeSettingsWO settings) {
        m_fileChooserSettings.saveSettingsTo(settings);
    }

    void saveSettingsForModel(final NodeSettingsWO settings) {
        saveSettingsForDialog(settings);
        m_fileChooserSettings.saveSettingsTo(settings);
    }

    void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_fileChooserSettings.validateSettings(settings);
        m_overwriteFileExtension.validateSettings(settings);
    }

}