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
//import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.SettingsModelReaderFileChooser;

final class MimeFileNioImporterNodeConfiguration {

    private static final String CFG_OVERWRITE_EXT = "FILE_EXTENSION";

    private SettingsModelOptionalString m_overwriteFileExtension;
    
    private static final String CFG_OVERWRITE_LOCAL = "OVERWRITE_LOCAL";

    private SettingsModelString m_overwriteLocal;

    
    private final SettingsModelReaderFileChooser m_fileChooserSettings;

    MimeFileNioImporterNodeConfiguration(final SettingsModelReaderFileChooser fileChooserSettings) {
        m_fileChooserSettings = fileChooserSettings;
        m_overwriteFileExtension = new SettingsModelOptionalString(CFG_OVERWRITE_EXT, "", false);
        m_overwriteLocal = new SettingsModelString(CFG_OVERWRITE_LOCAL, "skip");
    }
    
    SettingsModelOptionalString overwriteFileExtension() {
        return m_overwriteFileExtension;
    }

    void overwriteFileExtension(final String overwriteFileExtension) {
        m_overwriteFileExtension.setStringValue(overwriteFileExtension);
    }
    
    /*SettingsModelBoolean overwriteLocalFiles() {
        return m_overwriteLocalFiles;
    }

    void overwriteLocalFiles(final boolean overwriteLocalFiles) {
        m_overwriteLocalFiles.setBooleanValue(overwriteLocalFiles);
    }*/
    
    /*SettingsModelStringArray importedFiles() {
        return m_importedFiles;
    }

    void importedFiles(final String[] importedFiles) {
        m_importedFiles.setStringArrayValue(importedFiles);
    }*/
    
    SettingsModelString overwriteLocal() {
        return m_overwriteLocal;
    }

    void overwriteLocal(final String overwriteLocal) {
        m_overwriteLocal.setStringValue(overwriteLocal);
    }
    

    SettingsModelReaderFileChooser getFileChooserSettings() {
        return m_fileChooserSettings;
    }

    void loadSettingsForDialog(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_fileChooserSettings.loadSettingsFrom(settings);
        m_overwriteFileExtension.loadSettingsFrom(settings);
        m_overwriteLocal.loadSettingsFrom(settings);
    }

    void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        loadSettingsForDialog(settings);
    }

    void saveSettingsForDialog(final NodeSettingsWO settings) {
        m_fileChooserSettings.saveSettingsTo(settings);
        m_overwriteFileExtension.saveSettingsTo(settings);
        m_overwriteLocal.saveSettingsTo(settings);
    }

    void saveSettingsForModel(final NodeSettingsWO settings) {
        
        saveSettingsForDialog(settings);
    }

    void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_fileChooserSettings.validateSettings(settings);
        m_overwriteFileExtension.validateSettings(settings);
       // m_overwriteLocalFiles.validateSettings(settings);
        //m_importedFiles.validateSettings(settings);
    }

}