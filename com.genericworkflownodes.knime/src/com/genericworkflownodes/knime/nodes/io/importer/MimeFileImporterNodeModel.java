/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.nodes.io.importer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.uri.IURIPortObject;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.util.FileUtil;

import com.genericworkflownodes.util.Helper;
import com.genericworkflownodes.util.MIMETypeHelper;
import com.genericworkflownodes.util.ZipUtils;

/**
 * This is the model implementation of MimeFileImporter.
 *
 * @author roettig, aiche
 */
final class MimeFileImporterNodeModel extends NodeModel {

    /**
     * SettingsModel for the filename
     */
    static SettingsModelString filename() {
        return new SettingsModelString("FILENAME", "");
    }
    private final SettingsModelString m_filename = filename();

    /**
     * SettingsModel for potential file extension override.
     */
    static SettingsModelOptionalString fileExtension() {
        return new SettingsModelOptionalString("FILE_EXTENSION", "", false);
    }
    private final SettingsModelOptionalString m_file_extension = fileExtension();

    /**
     * Data member.
     */
    private byte[] data;

    /**
     * Getter for data member.
     *
     * @return The data member.
     */
    public byte[] getContent() {
        return data;
    }

    /**
     * Constructor for the node model.
     */
    protected MimeFileImporterNodeModel() {
        super(new PortType[] {}, new PortType[] { PortTypeRegistry.getInstance().getPortType(
                IURIPortObject.class) });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        this.m_filename.saveSettingsTo(settings);
        this.m_file_extension.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        this.m_filename.loadSettingsFrom(settings);
        this.m_file_extension.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        SettingsModelString tmp_filename = m_filename
                .createCloneWithValidatedValue(settings);

        if (tmp_filename.getStringValue().isEmpty()) {
            throw new InvalidSettingsException("No File selected.");
        }

        //convertToURL(tmp_filename.getStringValue());

        SettingsModelOptionalString tmp_file_extension = m_file_extension
                .createCloneWithValidatedValue(settings);

        if (tmp_file_extension.isActive()) {
            if (tmp_file_extension.getStringValue().equals("")) {
                throw new InvalidSettingsException(
                        "No File extension (override) provided.");
            } else if (MIMETypeHelper.getMIMEtypeByExtension(tmp_file_extension
                    .getStringValue()) == null) {
                throw new InvalidSettingsException(
                        "No MIME type registered for file extension: "
                                + tmp_file_extension.getStringValue());
            }
        } else if (MIMETypeHelper.getMIMEtype(tmp_filename.getStringValue()) == null) {
            throw new InvalidSettingsException(
                    "File of unknown MIME type selected: "
                            + tmp_filename.getStringValue());
        }
    }

    private static File getDataFile(final File internDir) {

        return new File(internDir, "loadeddata");
    }

    /**
     * Extract a URL from the given SettingsModelString, trying different
     * conversion approaches. Inspired by CSVReaderConfig#loadSettingsInModel().
     *
     * @param filename_settings
     *            The settings object containing the URL to convert.
     * @return A URL object.
     * @throws InvalidSettingsException
     *             If the string in the given settings object cannot be
     *             converted properly.
     */
    private URL convertToURL(String urlS) throws InvalidSettingsException {
        URL url;

        if (urlS == null) {
            throw new InvalidSettingsException("URL must not be null");
        }
        try {
            url = FileUtil.toURL(urlS);
        } catch (MalformedURLException e) {
            // might be a file, bug fix 3477
            File file = new File(urlS);
            try {
                url = file.toURI().toURL();
            } catch (Exception fileURLEx) {
                throw new InvalidSettingsException("Invalid URL: "
                        + e.getMessage(), e);
            }
        }

        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException, CanceledExecutionException {

        this.data = ZipUtils.read(getDataFile(internDir));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException, CanceledExecutionException {

        ZipUtils.write(this.data, getDataFile(internDir));
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {

        final String filenameValue = this.m_filename.getStringValue();

        /*
         * Upon inserting the node into a workflow, it gets configured, so at
         * least something fundamental like the file name should be checked
         */
        if (filenameValue.isEmpty()) {
            throw new InvalidSettingsException("No File selected.");
        }

        //TODO show warning on node if file does not exist
        // Determine the file extension
        final String fileExtension = this.m_file_extension.isActive() ? this.m_file_extension.getStringValue()
                : MIMETypeHelper.getMIMEtypeExtension(filenameValue).orElseThrow( () ->
                new InvalidSettingsException(
                        String.format("Could not determine file type for selected input file: %s", filenameValue)));

        return new PortObjectSpec[] {
                new URIPortObjectSpec(fileExtension)
        };
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        URL converted = convertToURL(m_filename.getStringValue());
        File file = FileUtil.getFileFromURL(converted);
        if (!file.exists()) {
            throw new Exception("File does not exist: "
                    + file.getAbsolutePath());
        }

        List<URIContent> uris = new ArrayList<URIContent>();

        //TODO URIContent could throw NUllPointerException if mimetype could not be looked up.
        // Since we check during configure, this is minor, but there should be a more general solution
        uris.add(new URIContent(FileUtil.toURL(m_filename.getStringValue()).toURI(),
                (m_file_extension.isActive() ? m_file_extension
                        .getStringValue() : MIMETypeHelper
                        .getMIMEtypeExtension(file.getAbsolutePath()).orElse(null))));

        data = Helper.readFileSummary(file, 50).getBytes();

        return new PortObject[] { new URIPortObject(uris) };
    }
}
