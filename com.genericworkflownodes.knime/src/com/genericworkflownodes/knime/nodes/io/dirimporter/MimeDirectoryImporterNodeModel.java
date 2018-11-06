/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   Sep 5, 2012 (Patrick Winter): created
 */
package com.genericworkflownodes.knime.nodes.io.dirimporter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.knime.base.filehandling.NodeUtils;
import org.knime.base.filehandling.remote.connectioninformation.port.ConnectionInformation;
import org.knime.base.filehandling.remote.connectioninformation.port.ConnectionInformationPortObject;
import org.knime.base.filehandling.remote.connectioninformation.port.ConnectionInformationPortObjectSpec;
import org.knime.base.filehandling.remote.files.Connection;
import org.knime.base.filehandling.remote.files.ConnectionMonitor;
import org.knime.base.filehandling.remote.files.RemoteFile;
import org.knime.base.filehandling.remote.files.RemoteFileFactory;
import org.knime.base.node.io.listfiles.ListFiles.Filter;
import org.knime.base.util.WildcardMatcher;
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
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.util.FileUtil;
import org.knime.core.util.MutableInteger;

/**
 * This is the model implementation.
 *
 *
 * @author Patrick Winter, KNIME.com, Zurich, Switzerland
 */
public class MimeDirectoryImporterNodeModel extends NodeModel {

    private ConnectionInformation m_connectionInformation;

    private ListDirectoryConfiguration m_configuration;

    private String m_extension;

    private Pattern m_regExpPattern;

    private int m_analyzedFiles;

    private int m_currentRowID;

    /**
     * Constructor for the node model.
     */
    public MimeDirectoryImporterNodeModel() {
        super(new PortType[]{ConnectionInformationPortObject.TYPE_OPTIONAL},
                new PortType[]{IURIPortObject.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        // Create connection monitor
        final ConnectionMonitor<? extends Connection> monitor = new ConnectionMonitor<>();
        // Create output URI container
        List<URIContent> uris = new ArrayList<URIContent>();
        String authority = null;
        try {
            URI directoryUri;
            if (m_connectionInformation != null) {
                exec.setProgress("Connecting to " + m_connectionInformation.toURI());
                // Generate URI to the directory
                directoryUri =
                        new URI(m_connectionInformation.toURI().toString()
                                + NodeUtils.encodePath(m_configuration.getDirectory()));
            } else {
                // Create local URI
                URL url = FileUtil.toURL(m_configuration.getDirectory());
                if (url.getProtocol().equals("knime")) {
                    authority = url.getAuthority();
                }
                directoryUri = FileUtil.getFileFromURL(url).toURI();
            }
            // Create remote file for directory selection
            final RemoteFile<? extends Connection> file =
                    RemoteFileFactory.createRemoteFile(directoryUri, m_connectionInformation, monitor);
            // List the selected directory
            exec.setProgress("Retrieving list of files");
            
            listDirectory(file, uris, true, exec, new MutableInteger(0), new MutableInteger(0));
            
            // If the user gave a KNIME URI in the input box, we resolve relative to that
            if (authority != null) {
                String prefix = "knime://" + authority + "/";
                URL url = FileUtil.toURL(prefix);
                Path localPath = FileUtil.resolveToPath(url);
                final List<URIContent> relUris = new ArrayList<URIContent>();
                for (URIContent uri : uris) {
                    Path relative = localPath.relativize(Paths.get(uri.getURI()));
                    relUris.add(new URIContent(new File(prefix + relative.toString()).toURI(), uri.getExtension()));
                }
                uris = relUris;
            }
        } finally {
            // Close connections
            monitor.closeAll();
        }
        return new PortObject[]{new URIPortObject(uris)};
    }

    /**
     * List a directory.
     *
     *
     * Writes the location of all files in a directory into the container. Files
     * will be listed recursively if the option is selected.
     *
     * @param file The file or directory to be listed
     * @param uris List of URIContents to write the reference of the files into
     * @param root If this directory is the root directory
     * @param exec Execution context to check if the execution has been canceled
     * @throws Exception If remote file operation did not succeed
     */
    private void listDirectory(final RemoteFile<? extends Connection> file,
            final List<URIContent> uris, final boolean root,
            final ExecutionContext exec, final MutableInteger processedEntries, final MutableInteger maxEntries) throws Exception {
        // Check if the user canceled
        exec.checkCanceled();
        if (!root) {
            final URI fileUri = file.getURI();
            // URI to the file
            final String extension = FilenameUtils.getExtension(fileUri.getPath());
            final URIContent content = new URIContent(fileUri, extension);
            // Add file information to the output
            if (!file.isDirectory()) {
                uris.add(content);
            }
        }
        // If the source is a directory list inner files
        if (file.isDirectory()) {
            if (root || m_configuration.getRecursive()) {
                final RemoteFile<? extends Connection>[] files = file.listFiles();
                Arrays.sort(files);
                final RemoteFile<? extends Connection>[] filteredFiles = filterFiles(files);
                maxEntries.setValue(maxEntries.intValue() + filteredFiles.length);
                exec.setMessage("Scanning " + file.getFullName());
                for (final RemoteFile<? extends Connection> file2 : filteredFiles) {
                    listDirectory(file2, uris, false, exec, processedEntries, maxEntries);
                    processedEntries.inc();
                    exec.setProgress(processedEntries.intValue() / maxEntries.doubleValue());
                }
            }
        }
    }

    /**
     * @param files
     * @return
     */
    private RemoteFile<? extends Connection>[] filterFiles(final RemoteFile<? extends Connection>[] files) {
        String extString = m_configuration.getExtensionsString();
        String expString = m_configuration.getExpressionsString();
        Filter filter = m_configuration.getFilter();
        m_extension = extString;
        switch (filter) {
        case None:
            break;
        case RegExp:
            // no break;
        case Wildcards:
            String patternS;
            if (filter.equals(Filter.Wildcards)) {
                patternS = WildcardMatcher.wildcardToRegex(expString);
            } else {
                patternS = expString;
            }
            if (m_configuration.isCaseSensitive()) {
                m_regExpPattern = Pattern.compile(patternS);
            } else {
                m_regExpPattern =
                    Pattern.compile(patternS, Pattern.CASE_INSENSITIVE);
            }
            break;
        default:
            throw new IllegalStateException("Unknown filter: " + filter);
            // transform wildcard to regExp.
        }
        m_analyzedFiles = 0;
        m_currentRowID = 0;
        List<RemoteFile<? extends Connection>> filteredFiles = new ArrayList<RemoteFile<? extends Connection>>();
        for (RemoteFile<?> f : files) {
            try {
                if (f.isDirectory() || satisfiesFilter(f.getName())) {
                    filteredFiles.add(f);
                }
            } catch (Exception e) {
                // catch or throw?
            }
        }
        return filteredFiles.toArray(new RemoteFile[filteredFiles.size()]);
    }

    /**
     * Checks if the given File name satisfies the selected filter requirements.
     *
     * @param name filename
     * @return True if satisfies the file else False
     */
    private boolean satisfiesFilter(final String name) {
        if (m_configuration.isCaseSensitive()) {
                if (!name.endsWith(m_extension)) {
                    return false;
                }
        } else {
            // case insensitive check on toLowerCase
            String lowname = name.toLowerCase();
                if (!lowname.endsWith(m_extension.toLowerCase())) {
                    return false;
                }
        }
        
        switch (m_configuration.getFilter()) {
        case None:
            return true;
        case RegExp:
            // no break;
        case Wildcards:
            Matcher matcher = m_regExpPattern.matcher(name);
            return matcher.matches();
        default:
            return false;
        }
    }

    /**
     * Factory method for the output table spec.
     *
     *
     * @return Output table spec
     */
    private URIPortObjectSpec createOutSpec() {        
        URIPortObjectSpec outSpec = new URIPortObjectSpec(
                m_extension);
        return outSpec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        // Check if a port object is available
        if (inSpecs[0] != null) {
            final ConnectionInformationPortObjectSpec object = (ConnectionInformationPortObjectSpec)inSpecs[0];
            m_connectionInformation = object.getConnectionInformation();
            // Check if the port object has connection information
            if (m_connectionInformation == null) {
                throw new InvalidSettingsException("No connection information available");
            }
        } else {
            m_connectionInformation = null;
        }
        // Check if configuration has been loaded
        if (m_configuration == null) {
            throw new InvalidSettingsException("No settings available");
        }
        m_configuration.validate("Directory", m_configuration.getDirectory());
        
        return new PortObjectSpec[]{createOutSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        if (m_configuration != null) {
            m_configuration.saveSettingsTo(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new ListDirectoryConfiguration().loadSettingsInModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        final ListDirectoryConfiguration config = new ListDirectoryConfiguration();
        config.loadSettingsInModel(settings);
        m_configuration = config;
        m_extension = config.getExtensionsString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // not used
    }

}
