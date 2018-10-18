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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.util.FileUtil;

import com.genericworkflownodes.knime.base.data.port.AbstractFileStoreURIPortObject;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of ListMimeFileImporter.
 *
 * @author roettig, aiche
 */
public class ListMimeFileImporterNodeModel extends NodeModel {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ListMimeFileImporterNodeModel.class);
    
    /**
     * ID for the filename configuration.
     */
    static final String CFG_FILENAME = "FILENAME";
    /**
     * Config name for file extension.
     */
    static final String CFG_FILE_EXTENSION = "FILE_EXTENSION";

    /**
     * Config name for the option to resolve all paths relative to the workflow.
     */
    static final String CFG_RESOLVE_WORKFLOW_RELATIVE = "RESOLVE_WORKFLOW";

    /**
     * Model containing the file names and optional extension.
     */
    private SettingsModelStringArray m_filenames = new SettingsModelStringArray(
            ListMimeFileImporterNodeModel.CFG_FILENAME, new String[] {});
    /**
     * SettingsModel for potential file extension override.
     */
    private SettingsModelOptionalString m_file_extension = new SettingsModelOptionalString(
            CFG_FILE_EXTENSION, "", false);

    private SettingsModelBoolean m_resolveWorkflowRel = new SettingsModelBoolean(CFG_RESOLVE_WORKFLOW_RELATIVE, false);

    /**
     * Constructor for the node model.
     */
    protected ListMimeFileImporterNodeModel() {
        super(new PortType[] {}, new PortType[] { IURIPortObject.TYPE });
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
        m_filenames.saveSettingsTo(settings);
        m_file_extension.saveSettingsTo(settings);
        m_resolveWorkflowRel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_filenames.loadSettingsFrom(settings);
        m_file_extension.loadSettingsFrom(settings);

        // This is a new feature so we have to check if the key exists
        if (settings.containsKey(CFG_RESOLVE_WORKFLOW_RELATIVE)) {
            m_resolveWorkflowRel.loadSettingsFrom(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        // This is a new feature so we have to check if the key exists
        if (settings.containsKey(CFG_RESOLVE_WORKFLOW_RELATIVE)) {
            m_resolveWorkflowRel.validateSettings(settings);
        }

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
                mt = MIMETypeHelper.getMIMEtype(filename).orElse(null);
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

        if (m_resolveWorkflowRel.getBooleanValue()) {
            String[] newFilenames = new String[filenames.length];
            Path localPath;
            try {
                URL url = FileUtil.toURL("knime://knime.workflow/");
                localPath = FileUtil.resolveToPath(url);
            } catch (IOException | InvalidPathException | URISyntaxException e) {
                throw new InvalidSettingsException("Cannot resolve KNIME workflow URL", e);
            }
            for (int i = 0; i < filenames.length; i++) {
                Path relative = localPath.relativize(Paths.get(filenames[i]));
                newFilenames[i] = "knime://knime.workflow/" + relative.toString();
            }
            m_filenames.setStringArrayValue(newFilenames);
        }

        URIPortObjectSpec uri_spec = null;

        if (m_file_extension.isActive()) {
            uri_spec = new URIPortObjectSpec(m_file_extension.getStringValue());
        } else {
            String ref_filename = m_filenames
                    .getStringArrayValue()[0];
            String ext = MIMETypeHelper.getMIMEtypeExtension(ref_filename).orElse(null);
            if (ext == null){
                ext = ref_filename.substring(ref_filename.indexOf('.'),ref_filename.length());
                LOGGER.warn("MIMEType not registered for extension '" + ext + "'. Proceeding, but this might lead to problems connecting to the affected FileStoreURIPort.");
                
            }
            uri_spec = new URIPortObjectSpec(ref_filename, ext);
                    
        }

        return new PortObjectSpec[] { uri_spec };
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        String[] filenames = m_filenames.getStringArrayValue();

        List<URIContent> uris = new ArrayList<URIContent>();
        for (String filename : filenames) {
            File in = new File(convertToURL(filename).toURI());

            if (!in.canRead()) {
                throw new Exception("Cannot read from input file: "
                        + in.getAbsolutePath());
            }

            //TODO URIContent could throw NUllPointerException if mimetype could not be looked up.
            // Since we check during configure, this is minor, but there should be a more general solution
            
            // FileUtil.toURL(filename) should not throw anymore because it was already called in convertToURL(filename)
            uris.add(new URIContent(FileUtil.toURL(filename).toURI(),
                    (m_file_extension.isActive() ? m_file_extension
                            .getStringValue() : MIMETypeHelper
                            .getMIMEtypeExtension(filename).orElse(null))));
        }

        return new PortObject[] { new URIPortObject(uris) };
    }

    /**
     * Extract a URL from the given String, trying different conversion
     * approaches. Inspired by CSVReaderConfig#loadSettingsInModel().
     *
     * @param urlS
     *            The string containing the URL.
     * @return A URL object.
     * @throws InvalidSettingsException
     *             If the given string cannot be converted properly.
     */
    private URL convertToURL(String urlS) throws InvalidSettingsException {
        URL url;

        if (urlS == null) {
            throw new InvalidSettingsException("URL must not be null");
        }
        try {
            url = FileUtil.resolveToPath(FileUtil.toURL(urlS)).toUri().toURL();
        } catch (MalformedURLException e) {
            // might be a file, bug fix 3477
            File file = new File(urlS);
            try {
                url = file.toURI().toURL();
            } catch (Exception fileURLEx) {
                throw new InvalidSettingsException("Invalid URL: "
                        + e.getMessage(), e);
            }
        } catch (IOException | URISyntaxException e) {
            throw new InvalidSettingsException("Invalid URL: "
                    + e.getMessage(), e);
        }

        return url;
    }
}
