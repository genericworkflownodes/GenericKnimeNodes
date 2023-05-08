package com.genericworkflownodes.knime.nodes.io.nioexporter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;

import org.knime.core.data.container.DataContainer;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.context.ports.PortsConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.filehandling.core.connections.DefaultFSConnectionFactory;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.connections.FSFiles;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.connections.meta.FSType;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.FileOverwritePolicy;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.SettingsModelWriterFileChooser;
import org.knime.filehandling.core.defaultnodesettings.filechooser.writer.WritePathAccessor;
import org.knime.filehandling.core.defaultnodesettings.status.NodeModelStatusConsumer;
import org.knime.filehandling.core.defaultnodesettings.status.StatusMessage.MessageType;

/**
 * The model of the File Exporter node.
 *
 * @author jpfeuffer
 */
final class FileExporterNodeModel extends NodeModel {

    /** The node logger for this class. */
    private static final NodeLogger LOGGER = NodeLogger.getLogger(FileExporterNodeModel.class);

    private final FileExporterSettings m_settings;

    private final NodeModelStatusConsumer m_statusConsumer;

    private final int m_dataPortIdx;

    FileExporterNodeModel(final PortsConfiguration portsConfig, final String connectionInputPortGrpName) {
        super(portsConfig.getInputPorts(), portsConfig.getOutputPorts());
        m_settings = new FileExporterSettings(portsConfig, connectionInputPortGrpName);
        m_settings.getWriterModel().setCreateMissingFolders(true);
        m_statusConsumer = new NodeModelStatusConsumer(EnumSet.of(MessageType.ERROR, MessageType.WARNING));
        m_dataPortIdx = portsConfig.getInputPortLocation().get(connectionInputPortGrpName) == null ? 0 : 1;
    }

    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        m_settings.getWriterModel().configureInModel(inSpecs, m_statusConsumer);
        m_statusConsumer.setWarningsIfRequired(this::setWarningMessage);
        return new PortObjectSpec[]{};
    }

    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final SettingsModelWriterFileChooser writerModel = m_settings.getWriterModel();
        final IURIPortObject uriPort = (IURIPortObject) inObjects[m_dataPortIdx];
        try (final WritePathAccessor accessor = writerModel.createWritePathAccessor()) {
            final FSPath outpath = accessor.getOutputPath(m_statusConsumer);
            m_statusConsumer.setWarningsIfRequired(this::setWarningMessage);
            // create directory?
            if (outpath != null && !FSFiles.exists(outpath)) {
                if (m_settings.getWriterModel().isCreateMissingFolders()) {
                    FSFiles.createDirectories(outpath);
                } else {
                    throw new IOException(String.format(
                        "The directory '%s' does not exist and must not be created due to user settings.", outpath));
                }
            }

            final FileOverwritePolicy fileOverwritePolicy = writerModel.getFileOverwritePolicy();
            
            // since the remainder is rather costly we do the overwrite check here, before copying anything
            for (URIContent uc : uriPort.getURIContents()) {
                //For now we assume that all URIs are local (usually in the KNIME tmp folder)
                Path source = Paths.get(uc.getURI());
                
                //We need to convert filename to String, otherwise they may come from different filesystems,
                // (e.g., if you want to save a local file to a KNIME server)
                Path target = outpath.resolve(source.getFileName().toString());
                
                if (fileOverwritePolicy == FileOverwritePolicy.FAIL && FSFiles.exists(target)) {
                    throw new IOException("Output file '" + target.toString()
                        + "' exists and must not be overwritten due to user settings.");
                }
            }

            int overwriteCounter = 1;
            for (URIContent uc : uriPort.getURIContents()) {
                //For now we assume that all URIs are local (usually in the KNIME tmp folder)
                Path source = Paths.get(uc.getURI());
                
                //We need to convert filename to String, otherwise they may come from different filesystems,
                // (e.g., if you want to save a local file to a KNIME server)
                Path target = outpath.resolve(source.getFileName().toString());
                
                // Since we checked already, that none of the currently existing files will be overwritten,
                //  we will add suffixes by default, in case filenames *in the port* overwrite each other.
                // TODO Warning, this will lead to unexpected behavior when files with the same name are 
                //  created by another process, after the first check has been performed.
                if (fileOverwritePolicy == FileOverwritePolicy.FAIL && FSFiles.exists(target)) {
                    LOGGER.warn("While trying to write to " + target.toString() + ": File suddenly exists. Either multiple files in your FilePort"
                            + " had the same filename or another process created the file after the initial existence check. File will be copied with a suffix.");
                    target = createReplacementFile(target, overwriteCounter);
                }
                try {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    LOGGER.error(e.toString());
                    throw e;
                }
                overwriteCounter += 1;
                
            }
        }
        return new PortObject[]{};
    }

    private static Path createReplacementFile(Path origOut, int count)
    {
        return (Path) origOut.resolveSibling(createReplacementFileName(origOut.getFileName().toString(), count));
    }
    
    private static String createReplacementFileName(String origOut, int count)
    {
        if (!origOut.contains("."))
        {
            return origOut + count;
        }
        return origOut.substring(0,origOut.indexOf(".")) + count + origOut.substring(origOut.indexOf("."));
    }
    
    /*private static void deleteFile(final FSPath outpath) {
        try {
            Files.delete(outpath);
            LOGGER.debug("File '" + outpath.toString() + "' deleted after node has been canceled.");
        } catch (final IOException ex) {
            LOGGER.warn("Unable to delete file '" + outpath.toString() + "' after cancellation: " + ex.getMessage(),
                ex);
        }
    }*/

    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) {
        // nothing to do
    }

    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) {
        // nothing to do
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_settings.saveSettingsInModel(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.validateSettings(settings);
    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.loadSettingsInModel(settings);
    }

    @Override
    protected void reset() {
        // nothing to do
    }
}