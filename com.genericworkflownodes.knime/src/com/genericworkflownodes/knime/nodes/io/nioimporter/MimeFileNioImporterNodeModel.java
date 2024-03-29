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
package com.genericworkflownodes.knime.nodes.io.nioimporter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.knime.core.data.filestore.FileStore;
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
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.util.pathresolve.ResolverUtil;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.connections.meta.FSType;
import org.knime.filehandling.core.connections.uriexport.URIExporterIDs;
import org.knime.filehandling.core.connections.uriexport.noconfig.NoConfigURIExporterFactory;
import org.knime.filehandling.core.defaultnodesettings.filechooser.reader.ReadPathAccessor;
import org.knime.filehandling.core.defaultnodesettings.status.NodeModelStatusConsumer;
import org.knime.filehandling.core.defaultnodesettings.status.StatusMessage.MessageType;

import com.genericworkflownodes.knime.generic_node.ExecutionFailedException;
import com.genericworkflownodes.util.MIMETypeHelper;
import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;

/**
 * This is the model implementation of MimeFileImporter.
 *
 * @author jpfeuffer
 */
final class MimeFileNioImporterNodeModel extends NodeModel {    
    
    /**
     * Data member.
     */
    private byte[] data;

    @SuppressWarnings("unused")
    private final boolean m_hasInputPorts;
    
    private final NodeModelStatusConsumer m_statusConsumer;
    
    private final MimeFileNioImporterNodeConfiguration m_config;
    
    private static final String CFG_IMPORTED_FILES = "IMPORTED_FILES";

    private SettingsModelStringArray m_importedFiles;
    
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
    protected MimeFileNioImporterNodeModel(final PortsConfiguration portsConfig,
            final MimeFileNioImporterNodeConfiguration config) {
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
        //m_importedFiles.saveSettingsTo(settings);
        m_config.saveSettingsForModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_config.loadSettingsForModel(settings);
        //m_importedFiles.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_config.validateSettingsForModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException, CanceledExecutionException {

        //this.data = ZipUtils.read(getDataFile(internDir));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException, CanceledExecutionException {

        //ZipUtils.write(this.data, getDataFile(internDir));
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
                FSPath firstFilePath = m_config.getFileChooserSettings().createReadPathAccessor().getFSPaths(new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR, MessageType.WARNING))).get(0);
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
                new URIPortObjectSpec(fileExtension)
        };
    }
    
    
    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        
        List<URIContent> uris = new ArrayList<URIContent>();
        
        try (final ReadPathAccessor accessor = m_config.getFileChooserSettings().createReadPathAccessor()) {
            final List<FSPath> fsPaths = accessor.getFSPaths(m_statusConsumer);
            List<String> impFiles = new ArrayList<String>();
            FileStore filestore = null;
            FileStoreURIPortObject po = null;
            for (final FSPath p : fsPaths) {
                FSType fs = p.toFSLocation().getFSType();
                // We let the URIExporter convert first because it nicely groups all KNIME file systems under the knime:// scheme
                NoConfigURIExporterFactory fs_urifactory = (NoConfigURIExporterFactory) m_config.getFileChooserSettings().getConnection().getURIExporterFactory(URIExporterIDs.DEFAULT);
                URI u = fs_urifactory.getExporter().toUri(p);
                
                if (fs == FSType.LOCAL_FS)
                {
                    if (!u.getScheme().equals("file"))
                    {
                        throw new ExecutionFailedException("Local filesystem Path was not translated to a file:// URL. Something is wrong.");
                    }
                }
                else 
                {
                    // We can then use the old (i.e., check for deprecation from time to time) FileUtil.resolveToPath to check if this KNIME URL
                    //  is convertible to a local file path (e.g., because the mountpoint and/or the workflow for which this URL stands for
                    //  is a local one. This will work nicely because we also use FileUtil.getFileFromURL in our GenericKnimeNodeModel.transferIncomingPorts2Config
                    //  If this is not null, and a local URL can be generated we just pass on the URL to the port. We actually could put the translated URL there
                    //  already I think.
                    // Checking for file:// should be unnecessary since we do it in the if-case above. Just in case.
                    if (!((u.getScheme().equals("knime") || u.getScheme().equals("file")) && ResolverUtil.resolveURItoLocalFile(u) != null))
                    {
                        String foldername = "FileImporter" + NodeContext.getContext().getNodeContainer().getID();
                        String oldFileName = p.getFileName().toString();
                        //TODO add suffix for possible duplicates? Could happen when you recurse into subfolders.
                        //TODO decide about replacing or use UID from the beginning
                        if (filestore == null)
                        {
                            filestore = exec.createFileStore(foldername);
                            filestore.getFile().mkdirs();
                            po = new FileStoreURIPortObject(filestore);
                        }
                        
                        Path tgt = Paths.get(filestore.getFile().toString(), oldFileName);
                        Files.copy(p, tgt, StandardCopyOption.REPLACE_EXISTING);
                        if (m_config.overwriteFileExtension().isActive()) {
                            po.registerFile(oldFileName, m_config.overwriteFileExtension().getStringValue());
                        } else {
                            po.registerFile(oldFileName);
                        }
                        
                        u = tgt.toUri();
                    }
                }
                uris.add(new URIContent(u,
                        (m_config.overwriteFileExtension().isActive() ? 
                                m_config.overwriteFileExtension().getStringValue() :
                                MIMETypeHelper.getMIMEtypeExtension(p.toAbsolutePath().toString()).orElse(FilenameUtils.getExtension(p.toAbsolutePath().toString())))));

            }
            
            if (filestore == null) {
                return new PortObject[] { new URIPortObject(uris) };
            } else {
                return new PortObject[] { po };
            }
        
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new PortObject[] { };
        }
    }
}
