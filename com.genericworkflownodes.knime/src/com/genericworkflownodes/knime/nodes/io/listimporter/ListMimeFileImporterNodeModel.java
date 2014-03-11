/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.nodes.io.listimporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of ListMimeFileImporter.
 * 
 * @author roettig
 */
public class ListMimeFileImporterNodeModel extends NodeModel {

    /**
     * ID for the filename configuration.
     */
    static final String CFG_FILENAME = "FILENAME";
    /**
     * Config name for file extension.
     */
    static final String CFG_FILE_EXTENSION = "FILE_EXTENSION";

    /**
     * Model containing the file names and optional extension.
     */
    private SettingsModelStringArray m_filenames = new SettingsModelStringArray(
            ListMimeFileImporterNodeModel.CFG_FILENAME, new String[] {});
    private SettingsModelOptionalString m_file_extension = new SettingsModelOptionalString(
            CFG_FILE_EXTENSION, "", false);

    /**
     * Constructor for the node model.
     */
    protected ListMimeFileImporterNodeModel() {
        super(new PortType[] {}, new PortType[] { new PortType(
                URIPortObject.class) });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_filenames.saveSettingsTo(settings);
        m_file_extension.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_filenames.loadSettingsFrom(settings);
        m_file_extension.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        SettingsModelStringArray tmp_filenames = m_filenames
                .createCloneWithValidatedValue(settings);

        if (tmp_filenames == null
                || tmp_filenames.getStringArrayValue().length == 0) {
            throw new InvalidSettingsException("No Files selected.");
        }

        SettingsModelOptionalString tmp_file_extension = m_file_extension
                .createCloneWithValidatedValue(settings);

        if (tmp_file_extension.isActive()) {
            if (tmp_file_extension.getStringValue().equals("")) {
                throw new InvalidSettingsException(
                        "No File extension (override) provided.");
            } else if (MIMETypeHelper.getMIMEtypeByExtension(tmp_file_extension
                    .getStringValue()) == null) {
                throw new InvalidSettingsException(
                        "No MIMEtype registered for file extension: "
                                + tmp_file_extension.getStringValue());
            }
        } else {
            List<String> mts = new ArrayList<String>();
            String mt = null;
            for (String filename : tmp_filenames.getStringArrayValue()) {
                mt = MIMETypeHelper.getMIMEtype(filename);
                if (mt == null) {
                    throw new InvalidSettingsException(
                            "Files of unknown MIMEtype selected: " + filename);
                }
                mts.add(mt);
            }

            for (String mimeType : mts) {
                if (!mimeType.equals(mt)) {
                    throw new InvalidSettingsException(
                            "Files with mixed MIMEType loaded");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {

        /*
         * Upon inserting the node into a workflow, it gets configured, so at
         * least something fundamental like the file name should be checked
         */
        String[] filenames = m_filenames.getStringArrayValue();
        if (filenames == null || filenames.length == 0) {
            throw new InvalidSettingsException("No Files selected.");
        }

        URIPortObjectSpec uri_spec = null;

        if (m_file_extension.isActive()) {
            uri_spec = new URIPortObjectSpec(m_file_extension.getStringValue());
        } else {
            uri_spec = new URIPortObjectSpec(
                    MIMETypeHelper.getMIMEtypeExtension(m_filenames
                            .getStringArrayValue()[0]));
        }

        return new PortObjectSpec[] { uri_spec };
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        String[] filenames = m_filenames.getStringArrayValue();

        List<URIContent> uris = new ArrayList<URIContent>();
        for (String filename : filenames) {
            File in = new File(filename);

            if (!in.canRead()) {
                throw new Exception("cannot read from input file: "
                        + in.getAbsolutePath());
            }

            uris.add(new URIContent(new File(filename).toURI(),
                    (m_file_extension.isActive() ? m_file_extension
                            .getStringValue() : MIMETypeHelper
                            .getMIMEtypeExtension(filename))));
        }

        return new PortObject[] { new URIPortObject(uris) };
    }
}
