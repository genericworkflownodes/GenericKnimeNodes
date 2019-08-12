package com.genericworkflownodes.knime.nodes.io.outputfolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.util.FileUtil;

/**
 * This is the model implementation of OutputFolder. Writes all the incoming
 * files to the given output folder.
 * 
 * @author The GKN Team
 */
public class OutputFolderNodeModel extends NodeModel {

    private static final String DEFAULT_FOLDER_NAME_VALUE = "";

    static final String CFG_FOLDER_NAME = "FOLDERNAME";

    static final String CFG_CREATE_FOLDER = "CREATE_IF_NOT_EXISTS";
    
    static final String CFG_OVERWRITE = "OVERWRITE";
    
    SettingsModelString m_foldername = new SettingsModelString(
            OutputFolderNodeModel.CFG_FOLDER_NAME, DEFAULT_FOLDER_NAME_VALUE);

    SettingsModelBoolean m_createIfNotExists = new SettingsModelBoolean(CFG_CREATE_FOLDER, false);
    
    SettingsModelBoolean m_overwrite = new SettingsModelBoolean(CFG_OVERWRITE, false);
    
    /**
     * Constructor for the node model.
     */
    protected OutputFolderNodeModel() {
        super(new PortType[] { PortTypeRegistry.getInstance().getPortType(IURIPortObject.class) },
                new PortType[] {});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {

        IURIPortObject obj = (IURIPortObject) inObjects[0];
        List<URIContent> uris = obj.getURIContents();

        if (uris.size() == 0) {
            throw new Exception(
                    "There were no URIs in the supplied URIPortObject");
        }
        File folder = FileUtil.getFileFromURL(FileUtil.toURL(m_foldername.getStringValue()));
        
        if (!folder.exists()) {
            if (m_createIfNotExists.getBooleanValue()) {
                folder.mkdirs();
            } else {
                throw new InvalidSettingsException("The selected folder does not exist. "
                        + "Check \"Create folder if it does not exist\" in the configuration "
                        + "or select a different one.");
            }
        }
        
              
        if (!folder.canWrite()) {
            throw new Exception("Cannot write to target directoy: " + folder.getAbsolutePath());
        }
        
        HashMap<String,Integer> basename_nr = new HashMap<String,Integer>();
        ArrayList<File> targets = new ArrayList<File>();
        // Check all files here first
        for (URIContent uri : uris) {
            File in = FileUtil.getFileFromURL(uri.getURI().toURL());
            Integer count = basename_nr.get(in.getName());
            File target;
            if (count == null)
            {
                count = 1;
                target = new File(folder, in.getName());
            }
            else
            {
                count++;
                //TODO check if this works with double extensions like .tar.gz
                // maybe we need to use the MimeType helper
                target = new File(folder, FilenameUtils.getBaseName(in.getName())+"_"+count+"."+FilenameUtils.getExtension(in.getName()));
            }
            basename_nr.put(in.getName(), count);
            
            checkBeforeCopy(in, target);
            targets.add(target);
        }
        
        // Now actually copy all the files
        double idx = 1.0;
        int i = 0;
        for (URIContent uri : uris) {
            File in = FileUtil.getFileFromURL(uri.getURI().toURL());
            File target = targets.get(i);
            FileUtils.copyFile(in, target);
            exec.setProgress(idx / uris.size());
            exec.checkCanceled();
            i++;
        }
        return null;
    }

    private void checkBeforeCopy(final File src, final File dest) throws IOException {
        if (!src.canRead()) {
            throw new IOException("Cannot read file to export: "
                    + src.getAbsolutePath());
        }
        if (dest.exists()) {
            if (!m_overwrite.getBooleanValue()) {
                throw new IOException("File " + dest.getAbsolutePath() + " exists and cannot be overwritten.");
            } else if (!dest.canWrite()) {                
                throw new IOException("Cannot write to file: " + dest.getAbsolutePath());
            }
        }
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
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        if (!(inSpecs[0] instanceof URIPortObjectSpec)) {
            throw new InvalidSettingsException(
                    "No URIPortObjectSpec compatible port object");
        }

        // check the selected file
        if ("".equals(m_foldername.getStringValue())) {
            throw new InvalidSettingsException(
                    "Please select a target folder for the Output Folder node.");
        }
        return new DataTableSpec[] {};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_foldername.saveSettingsTo(settings);
        m_createIfNotExists.saveSettingsTo(settings);
        m_overwrite.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_foldername.loadSettingsFrom(settings);
        if (settings.containsKey(CFG_CREATE_FOLDER)) {
            m_createIfNotExists.loadSettingsFrom(settings);
        }
        if (settings.containsKey(CFG_OVERWRITE)) {
            m_overwrite.loadSettingsFrom(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_foldername.validateSettings(settings);
        if (settings.containsKey(CFG_CREATE_FOLDER)) {
            m_createIfNotExists.validateSettings(settings);
        }
        if (settings.containsKey(CFG_OVERWRITE)) {
            m_overwrite.validateSettings(settings);
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

}
