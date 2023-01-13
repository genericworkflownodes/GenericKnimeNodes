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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
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
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.util.FileUtil;
import org.knime.core.util.URIUtil;
import org.knime.filehandling.core.connections.DefaultFSConnectionFactory;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.connections.FSFileSystem;
import org.knime.filehandling.core.connections.FSFileSystemProvider;
import org.knime.filehandling.core.connections.FSFiles;
import org.knime.filehandling.core.connections.FSLocation;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.connections.RelativeTo;
import org.knime.filehandling.core.connections.meta.FSType;
import org.knime.filehandling.core.connections.uriexport.URIExporter;
import org.knime.filehandling.core.connections.uriexport.URIExporterConfig;
import org.knime.filehandling.core.connections.uriexport.URIExporterFactory;
import org.knime.filehandling.core.connections.uriexport.URIExporterIDs;
import org.knime.filehandling.core.connections.uriexport.noconfig.NoConfigURIExporterFactory;
import org.knime.filehandling.core.data.location.FSLocationValueMetaData;
import org.knime.filehandling.core.defaultnodesettings.FileSystemHelper;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.ReadPathAccessor;
import org.knime.filehandling.core.defaultnodesettings.status.NodeModelStatusConsumer;
import org.knime.filehandling.core.defaultnodesettings.status.StatusMessage.MessageType;

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
     * Data member.
     */
    private byte[] data;

    private final boolean m_hasInputPorts;
    
    private final NodeModelStatusConsumer m_statusConsumer;
    
    private final MimeFileImporterNodeConfiguration m_config;
    
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
    protected MimeFileImporterNodeModel(final PortsConfiguration portsConfig,
            final MimeFileImporterNodeConfiguration config) {
        //super(new PortType[] {}, new PortType[] { PortTypeRegistry.getInstance().getPortType(
        //        IURIPortObject.class) });
        super(portsConfig.getInputPorts(), portsConfig.getOutputPorts());
        m_hasInputPorts = portsConfig.getInputPorts().length > 0;
        m_config = config;
        m_statusConsumer = new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR, MessageType.WARNING));
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
        m_config.saveSettingsForModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_config.loadSettingsForModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_config.validateSettingsForModel(settings);
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
        m_config.getFileChooserSettings().configureInModel(inSpecs, m_statusConsumer);
        m_statusConsumer.setWarningsIfRequired(this::setWarningMessage);

        // Determine the file extension
        String fileExtension = "";
        
        if (m_config.overwriteFileExtension().isActive())
        {
            fileExtension = m_config.overwriteFileExtension().getStringValue();
            if (MIMETypeHelper.getMIMEtypeByExtension(fileExtension).orElse(null) == null)
            {
                this.getLogger().warn("Extension of unknown/unregistered MIME type overwritten: "
                        + fileExtension); 
            }
        }
        else
        {
            try {
                Path firstFilePath = m_config.getFileChooserSettings().createReadPathAccessor().getPaths(new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR, MessageType.WARNING))).get(0);
                fileExtension = MIMETypeHelper.getMIMEtypeExtension(firstFilePath.toString()).orElse(null);
                if (fileExtension == null)
                {
                    this.getLogger().warn("File of unknown/unregistered MIME type selected: "
                            + fileExtension);
                    fileExtension = FilenameUtils.getExtension(firstFilePath.toString());
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return new PortObjectSpec[] {
                //TODO if not local, it will be a FileStoreURIPortObject
                new URIPortObjectSpec(fileExtension)
        };
    }

    /*@Override
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
                        .getMIMEtypeExtension(file.getAbsolutePath()).orElse(FilenameUtils.getExtension(file.getAbsolutePath())))));

        data = Helper.readFileSummary(file, 50).getBytes();

        return new PortObject[] { new URIPortObject(uris) };
    }*/
    
    // TODO safe tgt as member and delete at reset
    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        
        List<URIContent> uris = new ArrayList<URIContent>();
        final FSLocation location = m_config.getFileChooserSettings().getLocation();
        final FSLocationValueMetaData metaData = new FSLocationValueMetaData(location.getFileSystemCategory(),
            location.getFileSystemSpecifier().orElse(null));
        
        try (final ReadPathAccessor accessor = m_config.getFileChooserSettings().createReadPathAccessor()) {
            final List<FSPath> fsPaths = accessor.getFSPaths(m_statusConsumer);
            for (final FSPath p : fsPaths) {
                //URI u = p.toUri();
                // TODO use if (p.toFSLocation().getFSType() == FSType.LOCAL_FS) -> p.toURI
                NoConfigURIExporterFactory factory = (NoConfigURIExporterFactory) m_config.getFileChooserSettings().getConnection().getURIExporterFactory(URIExporterIDs.DEFAULT);
                URI u = factory.getExporter().toUri(p);
                if (!((u.getScheme().equals("file") || u.getScheme().equals("knime")) && FileUtil.resolveToPath(u.toURL()) != null))
                {
                    // TODO try with resources
                    FSConnection fsc = DefaultFSConnectionFactory.createRelativeToConnection(RelativeTo.WORKFLOW_DATA);
                    //FileSystemHelper.retrieveFSConnection(null, new FSLocation(FSCategory.RELATIVE, RelativeTo.WORKFLOW_DATA.getSettingsValue(), "/"));
                    FSPath tgt = fsc.getFileSystem().getPath(p.getFileName().toString());
                    //TODO add suffix for possible duplicates
                    //TODO decide about replacing or use UID from the beginning
                    Files.copy(p, tgt, StandardCopyOption.REPLACE_EXISTING);
                    //TODO use NoConfig.. cast
                    final URIExporterFactory factory2 = fsc.getURIExporterFactory(URIExporterIDs.DEFAULT); // default exporter is fine, since we just need to handle KNIME relative paths.
                    final URIExporterConfig config = factory.initConfig();
                    u = factory.createExporter(config).toUri(tgt);
                }
                uris.add(new URIContent(u,
                        (m_config.overwriteFileExtension().isActive() ? 
                                m_config.overwriteFileExtension().getStringValue() :
                                MIMETypeHelper.getMIMEtypeExtension(p.toAbsolutePath().toString()).orElse(FilenameUtils.getExtension(p.toAbsolutePath().toString())))));

            }
        
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        

        //TODO URIContent could throw NUllPointerException if mimetype could not be looked up.
        // Since we check during configure, this is minor, but there should be a more general solution

        //data = Helper.readFileSummary(file, 50).getBytes();

        return new PortObject[] { new URIPortObject(uris) };
    }
}
