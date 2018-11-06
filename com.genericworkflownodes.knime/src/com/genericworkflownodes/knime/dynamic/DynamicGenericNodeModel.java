/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.dynamic;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.knime.base.filehandling.mime.MIMEMap;
import org.knime.base.node.util.exttool.ExtToolOutputNodeModel;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.util.FileUtil;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.base.data.port.FileStorePrefixURIPortObject;
import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.IPrefixURIPortObject;
import com.genericworkflownodes.knime.commandline.CommandLineElement;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.custom.config.IPluginConfiguration;
import com.genericworkflownodes.knime.custom.config.NoBinaryAvailableException;
import com.genericworkflownodes.knime.execution.AsynchronousToolExecutor;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutorFactory;
import com.genericworkflownodes.knime.execution.UnknownCommandGeneratorException;
import com.genericworkflownodes.knime.execution.UnknownToolExecutorException;
import com.genericworkflownodes.knime.execution.impl.CancelMonitorThread;
import com.genericworkflownodes.knime.generic_node.ExecutionFailedException;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.util.Helper;
import com.genericworkflownodes.util.MIMETypeHelper;

public class DynamicGenericNodeModel extends ExtToolOutputNodeModel {
    static final String GENERIC_KNIME_NODES_OUT_TYPE = "GENERIC_KNIME_NODES_outtype#";
    static final String GENERIC_KNIME_NODES_OUT_ACTIVE = "GENERIC_KNIME_NODES_active#";
    static final String GENERIC_KNIME_NODES_OUT_LINKEDINPUT = "GENERIC_KNIME_NODES_linkedinput#";
    static final String GENERIC_KNIME_NODES_OUT_CUSTOMBASENAME = "GENERIC_KNIME_NODES_custombasename#";
    
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(DynamicGenericNodeModel.class);

    /**
     * Short-cut for optional ports.
     */
    public static final PortType OPTIONAL_PORT_TYPE = PortTypeRegistry.getInstance().getPortType(IURIPortObject.class, true);

    /**
     * Contains information on which of the available output types is selected
     * for each output port.
     */
    protected int[] m_selectedOutPortTypes;
    
    /**
     * Contains information on which output ports are active.
     */
    protected boolean[] m_activeOutPorts;

    /**
     * Contains information on what input to use for
     * outputname inference at the outports
     */
    protected int[] m_linkedInPorts;
    
    /**
     * Contains information on which custom basename
     * was set by a user for the outports
     */
    protected String[] m_customBasenames; 

    /**
     * stores the node configuration (i.e. parameters, ports, ..)
     */
    private final INodeConfiguration m_nodeConfig;

    /**
     * The configuration of the encapsulating plugin.
     */
    private final IPluginConfiguration m_pluginConfig;

    /**
     * The file endings supported by the input ports.
     */
    private final String[][] m_fileEndingsInPorts;

    /**
     * The file endings supported by the output ports.
     */
    private final String[][] m_fileEndingsOutPorts;

    /**
     * The actual m_executor used to run the tool.
     */
    IToolExecutor m_executor;

    /**
     * Constructor for the node model.
     * 
     * @param nodeConfig
     *            The node configuration.
     * @param pluginConfig
     *            The plugin configuration
     * @param fileEndingsInPorts
     *            The supported file endings of all incoming ports.
     * @param fileEndingsOutPorts
     *            The supported file endings of all outgoing ports.
     */
    public DynamicGenericNodeModel(INodeConfiguration nodeConfig,
            IPluginConfiguration pluginConfig, String[][] fileEndingsInPorts,
            String[][] fileEndingsOutPorts) {
        super(createOPOs(nodeConfig.getInputPorts()), createOPOs(nodeConfig
                .getOutputPorts()));
        m_nodeConfig = nodeConfig;
        m_pluginConfig = pluginConfig;

        m_fileEndingsInPorts = new String[fileEndingsInPorts.length][];
        Helper.array2dcopy(fileEndingsInPorts, m_fileEndingsInPorts);

        m_fileEndingsOutPorts = new String[fileEndingsOutPorts.length][];
        Helper.array2dcopy(fileEndingsOutPorts, m_fileEndingsOutPorts);

        m_selectedOutPortTypes = new int[m_nodeConfig.getNumberOfOutputPorts()];
        m_linkedInPorts = new int[m_nodeConfig.getNumberOfOutputPorts()];
        m_customBasenames = new String[m_nodeConfig.getNumberOfOutputPorts()];
        m_activeOutPorts = new boolean[m_nodeConfig.getNumberOfOutputPorts()];
        for (int i = 0; i < m_activeOutPorts.length; i++){
            m_activeOutPorts[i] = m_nodeConfig.getOutputPorts().get(i).isActive();
        }
    }

    /**
     * Returns the selected output type of the given port.
     * 
     * @param idx
     *            The port number for which the output type should be returned.
     * @return The selected output type.
     */
    protected String getOutputType(int idx) {
        return m_nodeConfig.getOutputPorts().get(idx).getMimeTypes()
                .get(m_selectedOutPortTypes[idx]);
    }

    /**
     * Returns the selected output type index of the given port.
     * 
     * @param idx
     *            The port number for which the output type index should be
     *            returned.
     * @return The selected output type index.
     */
    protected int getOutputTypeIndex(int idx) {
        return m_selectedOutPortTypes[idx];
    }
    
    /**
     * Returns if the port with the given index has its selected MimeType set to the "inactive" type.
     * 
     * @param idx
     *            The port number for which the activeness should be checked.
     * @return If the port is inactive.
     */
    protected boolean isInactive(int idx) {
        return !m_activeOutPorts[idx];
    }

    /**
     * Creates a list of output port types for the nodes.
     * 
     * @param ports
     *            The port list from which the output ports should be generated.
     * @return A list of output port types for the nodes.
     */
    private static PortType[] createOPOs(List<Port> ports) {
        PortType[] portTypes = new PortType[ports.size()];
        Arrays.fill(portTypes, IURIPortObject.TYPE);
        for (int i = 0; i < ports.size(); i++) {
            if (ports.get(i).isOptional()) {
                portTypes[i] = OPTIONAL_PORT_TYPE;
                if (!ports.get(i).isActive()) {
                    portTypes[i] = InactiveBranchPortObject.TYPE;
                }
            }
        }
        return portTypes;
    }

    /**
     * Executes the tool underlying this node.
     * 
     * @param executor
     *            The fully configured {@link IToolExecutor}.
     * 
     * @param execContext
     *            The {@link ExecutionContext} of the node.
     * 
     * @throws Exception
     */
    private void executeTool(IToolExecutor executor,
            final ExecutionContext execContext) throws ExecutionFailedException {

        final AsynchronousToolExecutor asyncExecutor = new AsynchronousToolExecutor(
                executor);

        asyncExecutor.invoke();

        // create one thread that will periodically check if the user has
        // cancelled the execution of the node
        // if this monitor thread detects that a cancel was requested, then it
        // will invoke the kill method
        // of the asyncExecutor
        final CancelMonitorThread monitorThread = new CancelMonitorThread(
                asyncExecutor, execContext);
        monitorThread.start();

        // wait until the execution completes
        asyncExecutor.waitUntilFinished();
        // also wait for the monitor thread to die
        monitorThread.waitUntilFinished();

        int retcode = -1;
        try {
            retcode = asyncExecutor.getReturnCode();
        } catch (ExecutionException ex) {
            // it means that the task threw an exception, assume retcode == -1
            throw new ExecutionFailedException(m_nodeConfig.getName(), ex);
        } catch (InterruptedException iex) {
            throw new ExecutionFailedException(m_nodeConfig.getName(), iex);
        }

        LOGGER.debug("COMMAND:  " + executor.getCommand());
        LOGGER.debug("STDOUT:  " + executor.getToolOutput());
        LOGGER.debug("STDERR:  " + executor.getToolErrorOutput());
        LOGGER.debug("RETCODE: " + retcode);

        if (retcode != 0) {
            LOGGER.error("Failing process stdout: " + executor.getToolOutput());
            LOGGER.error("Failing process stderr: "
                    + executor.getToolErrorOutput());

            // process failed, so we will send the stdout/stderr messages into
            // the dialogs
            setFailedExternalOutput(executor.getToolOutput());
            setFailedExternalErrorOutput(executor.getToolErrorOutput());

            throw new ExecutionFailedException(m_nodeConfig.getName());
        }

        // finally fill the stdout/stderr messages into the dialogs
        setExternalOutput(executor.getToolOutput());
        setExternalErrorOutput(executor.getToolErrorOutput());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        super.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        for (String key : m_nodeConfig.getParameterKeys()) {
            Parameter<?> param = m_nodeConfig.getParameter(key);
            // skip file parameters
            if (param instanceof IFileParameter) {
                continue;
            }
            settings.addString(key, param.getStringRep());
        }

        for (int i = 0; i < m_nodeConfig.getNumberOfOutputPorts(); i++) {
            settings.addInt(GENERIC_KNIME_NODES_OUT_TYPE
                            + i, m_selectedOutPortTypes[i]);
            settings.addBoolean(GENERIC_KNIME_NODES_OUT_ACTIVE
                            + i, m_activeOutPorts[i]);
            settings.addInt(GENERIC_KNIME_NODES_OUT_LINKEDINPUT
                            + i, m_linkedInPorts[i]);
            settings.addString(GENERIC_KNIME_NODES_OUT_CUSTOMBASENAME
                            + i, m_customBasenames[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // - we know that values are validated and thus are valid
        // - we transfer the values into the corresponding model objects
        for (String key : m_nodeConfig.getParameterKeys()) {
            // FileParameters are not set by the UI
            if (m_nodeConfig.getParameter(key) instanceof IFileParameter)
                continue;

            String value = settings.getString(key);
            try {
                m_nodeConfig.getParameter(key).fillFromString(value);
            } catch (InvalidParameterValueException e) {
                LOGGER.warn(
                        "Caught InvalidParameterValueException in loadValidatedSettingsFrom()",
                        e);
            }
        }

        //TODO this is fully  copied from the load settings in the Dialog
        // there has to be sth that we can do about that duplications
        //TODO Shouldn't this also be done in the validateSettings method?
        int nP = m_nodeConfig.getNumberOfOutputPorts();
        m_selectedOutPortTypes = new int[nP];
        m_activeOutPorts = new boolean[nP];
        m_linkedInPorts = new int[nP];
        m_customBasenames = new String[nP];
        
        for (int i = 0; i < nP; i++) {
            Port p = m_nodeConfig.getOutputPorts().get(i);
            int idx = 0; // default mimetype is the first
            try{
                idx = settings.getInt(GENERIC_KNIME_NODES_OUT_TYPE + i);
                m_selectedOutPortTypes[i] = idx;
            } catch (InvalidSettingsException e) {
                //TODO Warning that we fell back to defaults?
            }

            boolean idxOOR = (idx < 0 || idx >= p.getMimeTypes().size());
            if (idxOOR && !p.isOptional()) // invalid required port setting read
            {
                idx = 0;
                //TODO Best would be to also deactivate this port to show the user that they have to reconfigure.
                // But currently I disabled the editing of the activeness checkbox for required ports, so it would get
                // initialized with an unchecked checkbox and you could not reactivate.
                // You could do a function that, when the OutputTypes tab is clicked, that it resets to active
                // for all required ports, because we assume the user has seen/thought about the new settings.
                // But then, anyways a loaded unchanged workflow would fail due to inactiveness of the port, so no general best solution
                // for it.
                // Maybe provide better defaults (e.g. by looking at a mapping from old to new versions, if available). But
                // that is a lot of work and does not work if the invalidness didnt come from a version change.
                LOGGER.warn("Invalid mime-type index in settings.xml for required port #" + i + ". Using default (first).");
            }
            
            try{
                // A found activeness setting always takes precedence
                boolean active = settings
                        .getBoolean(GENERIC_KNIME_NODES_OUT_ACTIVE
                                + i);
                m_activeOutPorts[i] = active;
            } catch (InvalidSettingsException e) {
                // else check if index is invalid otherwise default to active. This is also to cope
                // with old versions that encoded invalidness in an additional mimetype that is either present as inactive
                // in old generated NodeFactories or out of range in newer ones/dynamic factories.
                m_activeOutPorts[i] = !(idxOOR || p.getMimeTypes().get(m_selectedOutPortTypes[i]).toLowerCase() == "inactive");
            }
            
            try{ //get linked inport
                int linked = settings
                        .getInt(GENERIC_KNIME_NODES_OUT_LINKEDINPUT
                                + i);
                m_linkedInPorts[i] = linked;
            } catch (InvalidSettingsException e) {
                // probably an older version then. Index 0 is auto.
                m_linkedInPorts[i] = 0;
            }
            
            try{ //get custom basename
                String bn = settings
                        .getString(GENERIC_KNIME_NODES_OUT_CUSTOMBASENAME
                                + i);
                m_customBasenames[i] = bn;
            } catch (InvalidSettingsException e) {
                // probably an older version then.
                m_customBasenames[i] = "";
            }
            
            p.setActive(m_activeOutPorts[i]);
            p.setLinkedPortIndex(m_linkedInPorts[i]);
            p.setUserBasename(m_customBasenames[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // - we check if params are present with settings.getString
        // - we validate incoming settings values basically with param.fillFromString
        // - we do not transfer values to member variables yet
        // - we throw an exception if something is invalid

        String errorsFound = "";
        for (String key : m_nodeConfig.getParameterKeys()) {
            Parameter<?> param = m_nodeConfig.getParameter(key);
            // FileParameters are not set by the UI
            if (param instanceof IFileParameter) {
                continue;
            }

            try {
                String value = settings.getString(key);
                param.fillFromString(value);
            } catch (InvalidParameterValueException e) {
                errorsFound += "\t - Invalid value for parameter " + key + " in settings.xml.\n";
            } catch (InvalidSettingsException e) {
                errorsFound += "\t - Entry for parameter " + key + " not found in settings.xml.\n";
            }
        }
        // Accumulate errors otherwise only the first will be thrown.
        if (!errorsFound.isEmpty())
        {
            throw new InvalidSettingsException( "\n\tGenericKNIMENodes:\n\t Maybe you are loading node settings (or a complete workflow) generated with an older version of the tool.\n\t If you do not reconfigure the node (marked with an exclamation mark),\n\t the current defaults will be loaded instead. \n" + errorsFound );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {

        // Test if the named tool exists in the tool-db, if not throws an
        // exception to tell the user that the executable is missing.
        checkIfToolExists();

        for (Parameter<?> param : m_nodeConfig.getParameters()) {
            if (!param.isOptional() && param.getValue() != null
                    && "".equals(param.getStringRep())
                    && !(param instanceof IFileParameter)) {
                setWarningMessage("Some mandatory parameters might not be set.");
            }
        }

        int nIn = m_fileEndingsInPorts.length;

        for (int i = 0; i < nIn; i++) {
            // not connected input ports have nulls in inSpec
            if (inSpecs[i] == null) {
                // .. if port is optional everything is fine
                if (m_nodeConfig.getInputPorts().get(i).isOptional()) {
                    continue;
                } else {
                    throw new InvalidSettingsException(
                            "Non-optional input port is not connected.");
                }
            }

            URIPortObjectSpec spec = (URIPortObjectSpec) inSpecs[i];
            String firstExt = spec.getFileExtensions().get(0);
            
            // get MIMEType from incoming port
            // TODO: we should check all file extensions, if its more than one (e.g. last node outputs mixed list of txt and jpg)
            String mt = MIMETypeHelper.getMIMEtypeByExtension(firstExt).orElse(firstExt);

            // check whether input MIMEType is in list of allowed MIMETypes
            boolean ok = false;
            if (m_fileEndingsInPorts[i].length > 0) {
                for (int j = 0; j < m_fileEndingsInPorts[i].length && !ok; j++) {
                    if (mt.equals(MIMETypeHelper.getMIMEtypeByExtension(m_fileEndingsInPorts[i][j]).orElse(m_fileEndingsInPorts[i][j]))){
                        ok = true;
                    }
                }
            } else {
                // we accept all incoming data if the node does not restrict the
                // file endings
                ok = true;
            }

            // we require consistent file endings for non prefix ports
            if (!ok && !m_nodeConfig.getInputPorts().get(i).isPrefix()) {
                String mismatch = String.format(
                        "has extension: '%s'; expected one of: %s", firstExt,
                        Arrays.toString(m_fileEndingsInPorts[i]));
                throw new InvalidSettingsException(
                        "Invalid MIMEtype at port number " + i + " : "
                                + mismatch);
            }
        }

        return createOutSpec();
    }

    protected void checkIfToolExists() throws InvalidSettingsException {
        try {
            m_pluginConfig.getBinaryManager().findBinary(
                    m_nodeConfig.getExecutableName());
        } catch (NoBinaryAvailableException e) {
            LOGGER.warn("Failed to find matching binary.", e);
            throw new InvalidSettingsException(
                    "Failed to find matching binary.", e);
        }
    }

    protected PortObjectSpec[] createOutSpec() {
        int nOut = m_fileEndingsOutPorts.length;
        PortObjectSpec[] out_spec = new PortObjectSpec[nOut];

        // set selected MIMEURIPortObjectSpecs at output ports
        for (int i = 0; i < nOut; i++) {
            if (isInactive(i))
            {
                out_spec[i] = InactiveBranchPortObjectSpec.INSTANCE;
                continue;
            }
            // selected output MIMEType
            int selectedMIMETypeIndex = getOutputTypeIndex(i);
            
            // should never happen. Currently an empty file ending restriction in CTD
            // will be read as a list of length 1 with "" as entry.
            if (selectedMIMETypeIndex >= m_fileEndingsOutPorts[i].length)
            {
                out_spec[i] = new URIPortObjectSpec(FilenameUtils.getExtension(m_customBasenames[i]));
                continue;
            }
            String mt = m_fileEndingsOutPorts[i][selectedMIMETypeIndex];
            if (mt.isEmpty()) // this is the case in empty restrictions
            {
                //TODO: we could make the OutType dropdown menu to a text box in that case.
                //but requires some changes, since we assume all rows have this dropdown
                //so for now, you can only go for the custom basename (where you can now include
                //an extension, because only the empty string will be added
                out_spec[i] = new URIPortObjectSpec(FilenameUtils.getExtension(m_customBasenames[i]));
                continue;
            }
            out_spec[i] = new URIPortObjectSpec(mt);
        }

        return out_spec;
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects,
            ExecutionContext execContext) throws Exception {
        // create job directory
        File jobdir = Helper.getTempDir(m_nodeConfig.getName(),
                !GenericNodesPlugin.isDebug());

        // transfer the incoming files into the nodeConfiguration
        transferIncomingPorts2Config(inObjects);

        // prepare input data and parameter values
        List<PortObject> outPorts = transferOutgoingPorts2Config(jobdir,
                inObjects, execContext);

        // prepare the executor
        m_executor = prepareExecutor(jobdir);

        // launch executable
        executeTool(m_executor, execContext);

        // process result files
        // PortObject[] outports = processOutput(outputFiles, exec);

        if (!GenericNodesPlugin.isDebug()) {
            FileUtils.deleteDirectory(jobdir);
        }

        PortObject[] outports = new PortObject[outPorts.size()];
        for (int i = 0; i < outPorts.size(); ++i) {
            outports[i] = outPorts.get(i);
            // if we have an prefix port we need to trigger reindexing
            if (outports[i] instanceof FileStorePrefixURIPortObject) {
                ((FileStorePrefixURIPortObject) outports[i]).collectFiles();
            }
        }

        return outports;
    }

    /**
     * Instantiates a new {@link IToolExecutor} for this tool according to the
     * plug-in settings.
     * 
     * @param jobdir
     *            The directory assigned to the node by the
     *            {@link ExecutionContext}.
     * @throws UnknownToolExecutorException
     *             Thrown if the requested {@link IToolExecutor} is unknown.
     * @throws UnknownCommandGeneratorException
     *             Thrown if the requested {@link ICommandGenerator} is unknown.
     * @throws IOException
     * @throws Exception
     */
    private IToolExecutor prepareExecutor(File jobdir)
            throws UnknownToolExecutorException,
            UnknownCommandGeneratorException, IOException, Exception {
        IToolExecutor executor = ToolExecutorFactory.createToolExecutor(
                m_pluginConfig.getPluginProperties().getProperty("executor"),
                m_pluginConfig.getPluginProperties().getProperty(
                        "commandGenerator"));

        executor.setWorkingDirectory(jobdir);
        executor.prepareExecution(m_nodeConfig, m_pluginConfig);

        return executor;
    }

    /**
     * Creates a list of lists of output files (as {@link URI}s) pointing to the
     * files that will be generated by the executed tool.
     * 
     * @param jobdir
     *            The working directory of the executable.
     * @param inData
     *            The input data as {@link PortObject} array
     * @return A list of lists of output files
     * @throws Exception
     *             If the input has an invalid configuration.
     */
    private List<PortObject> transferOutgoingPorts2Config(final File jobdir,
            PortObject[] inData, ExecutionContext exec) throws Exception {

        final int nOut = m_nodeConfig.getOutputPorts().size();
        List<PortObject> outPorts = new ArrayList<PortObject>(nOut);

        int nrListPortsBefore = 0;
        for (int i = 0; i < nOut; i++) {
            Port port = m_nodeConfig.getOutputPorts().get(i);

            if (port.isActive())
            {
                String portname = port.getName();
                String ext = "";
                boolean isPrefix = port.isPrefix();
                // Create a folder/filestore generated by KNIME in its temp folder
                FileStore filestore = exec.createFileStore(m_nodeConfig.getName() + "_" + i);
                Parameter<?> p = m_nodeConfig.getParameter(portname);
                // filenames on the filesystem in the filestore (to save)
                List<String> filenames = new ArrayList<String>();

                // The object to add later
                PortObject po;

                if (p instanceof FileListParameter && port.isMultiFile()) {
                    // we currently do not support lists of prefixes
                    if (isPrefix) {
                        throw new InvalidSettingsException(
                                "GKN currently does not support lists of prefixes as output.");
                    }
                    FileListParameter flp = (FileListParameter) p;
                    
                    // basenames and number of output files guessed from input
                    List<String> basenames = getOutputBaseNameList(nrListPortsBefore);

                    // Create the folder (i.e. file store) in KNIME's temp dir
                    po = new FileStoreURIPortObject(filestore);
                    ext = getOutputType(i);
                    for (int f = 0; f < basenames.size(); ++f) {
                        File file = ((FileStoreURIPortObject) po).registerFile(basenames.get(f) + "." + ext);
                        filenames.add(file.getAbsolutePath());
                    }
                    
                    // overwrite existing settings with new values generated by the
                    // stash
                    flp.setValue(filenames);
                    
                    ++nrListPortsBefore;
                }
                else if (p instanceof FileParameter && !port.isMultiFile())
                {
                    // Now it is either a single output file or a file prefix
                    String basename = getOutputBaseName(i);
                    if (isPrefix) {
                        // we do not append the file extension if we have a prefix
                        po = new FileStorePrefixURIPortObject(filestore, basename);
                        ((FileParameter) p).setValue(((FileStorePrefixURIPortObject) po).getPrefix());
                        LOGGER.debug("> setting param " + portname + "->"
                                + ((FileStorePrefixURIPortObject) po).getPrefix());
                    } else {
                        ext = getOutputType(i);
                        basename += '.' + ext;
                        po = new FileStoreURIPortObject(filestore);
                        File file = ((FileStoreURIPortObject)po).registerFile(basename);
                        ((FileParameter) p).setValue(file.getAbsolutePath());
                        LOGGER.debug("> setting param " + portname + "->" + file);
                    }
                } else {
                    //TODO better message?
                    throw new Exception("Invalid connection between ports and parameters.");
                }
                // add filled portobject with registered files
                outPorts.add(po);
            }
            else
            {
                outPorts.add(InactiveBranchPortObject.INSTANCE);
            }
        }
        return outPorts;
    }

    /**
     * Gets output name for Single output and output prefix (keep in mind that suffixes are up to the tool itself)
     * @param outputIndex index of the output port to generate a name for
     *  (based on the input files in the input ports)
     * @return outputname to use for registering an output file
     */
    private String getOutputBaseName(int outputIndex) throws InvalidSettingsException{
        if (m_customBasenames[outputIndex] != null && !m_customBasenames[outputIndex].isEmpty())
        {
            //TODO replace variables in that string
            return m_customBasenames[outputIndex];
        }
        String iterationSuffix = "";
        try {
          iterationSuffix = "_iter" + Integer.toString(peekFlowVariableInt("currentIteration"));
        } catch (NoSuchElementException e) {}
        
        // if it is not set to "auto"
        if (m_linkedInPorts[outputIndex] != 0)
        {
            Port linked = m_nodeConfig.getInputPorts().get(m_linkedInPorts[outputIndex]-1);
            Object portVal = m_nodeConfig.getParameter(linked.getName()).getValue();
            if (portVal != null)
            {
                if (!linked.isMultiFile())
                {
                    return FilenameUtils.getBaseName((String) portVal)
                            + "_out" + outputIndex + iterationSuffix;
                }
                else
                {
                    LOGGER.warn("Linked input to single ouput is a list. Taking first element.");
                    return FilenameUtils.getBaseName(((List<String>) portVal).get(0))
                            + "_out" + outputIndex + iterationSuffix;
                }
            }
            else
            {
                throw new InvalidSettingsException("Linked input port " + linked.getName() + " for output port " + m_nodeConfig.getOutputPorts().get(outputIndex).getName() + " is unconnected.");
            }
        }
        else
        {
            // look for first connected! non multifile inport
            for (Port p : m_nodeConfig.getInputPorts())
            {
                if (!p.isMultiFile() && m_nodeConfig.getParameter(p.getName()).getValue() != null)
                {
                    return FilenameUtils.getBaseName((String) m_nodeConfig.getParameter(p.getName()).getValue())
                            + "_out" + outputIndex + iterationSuffix;
                }
            }
            // If everything fails, use the nodename
            return m_nodeConfig.getName() + "_out" + outputIndex;
        }
    }
    
    /**
     * Gets output basename for a multifile output (keep in mind that suffixes are up to the tool itself)
     * @param outputIndex index of the output port to generate a name for
     *  (based on the input files in the input ports)
     * @return outputname to use for registering an output file
     */
    private List<String> getOutputBaseNameList(int outputIndex) throws InvalidSettingsException {
        if (m_customBasenames[outputIndex] != null && !m_customBasenames[outputIndex].isEmpty())
        {
            //TODO replace variables in that string
            return Arrays.asList(m_customBasenames[outputIndex].split(","));
        }
        
        // See if we are in an obvious loop context
        String iterationSuffix = "";
        try {
          iterationSuffix = "_iter" + Integer.toString(peekFlowVariableInt("currentIteration"));
        } catch (NoSuchElementException e) {}
        
        // if it is not set to "auto"
        if (m_linkedInPorts[outputIndex] != 0)
        {
            Port linked = m_nodeConfig.getInputPorts().get(m_linkedInPorts[outputIndex]-1);
            Object portVal = m_nodeConfig.getParameter(linked.getName()).getValue();
            if (portVal != null)
            {
                if (linked.isMultiFile())
                {
                    List<String> inputnames = (List<String>) portVal;
                    List<String> basenames = new ArrayList<String>();
                    for (String inputname : inputnames)
                    {
                        basenames.add(FilenameUtils.getBaseName(inputname) + "_out" + outputIndex + iterationSuffix);
                    }
                    return basenames;
                }
                else
                {
                    throw new InvalidSettingsException("Linked input port " + linked.getName() + " for output port " + m_nodeConfig.getOutputPorts().get(outputIndex).getName() + " is not a multi-file input port.");
                }
            }
            else
            {
                throw new InvalidSettingsException("Linked input port " + linked.getName() + " for output port " + m_nodeConfig.getOutputPorts().get(outputIndex).getName() + " is unconnected.");
            }
        }
        else
        {
            // look for first connected! multifile inport
            for (Port p : m_nodeConfig.getInputPorts())
            {
                if (p.isMultiFile() && m_nodeConfig.getParameter(p.getName()).getValue() != null)
                {
                    List<String> inputnames = (List<String>) m_nodeConfig.getParameter(p.getName()).getValue();
                    List<String> basenames = new ArrayList<String>();
                    for (String inputname : inputnames)
                    {
                        basenames.add(FilenameUtils.getBaseName(inputname) + "_out" + outputIndex + iterationSuffix);
                    }
                }
            }
            throw new InvalidSettingsException("For multifile outport " + m_nodeConfig.getOutputPorts().get(outputIndex).getName() + " no linkable multifile input could be found. Please specify own output basenames as a comma seperated list.");
        }
    }

    /**
     * Transfers the incoming ports into the config, that it can be written out
     * into a config file or can be transferred to the command line.
     * 
     * @param inData
     *            The incoming port objects.
     * @throws Exception
     */
    private void transferIncomingPorts2Config(PortObject[] inData)
            throws Exception {
        // Transfer settings from the input ports into the configuration object
        for (int i = 0; i < inData.length; i++) {
            // find the internal port for this PortObject
            Port port = m_nodeConfig.getInputPorts().get(i);

            IURIPortObject po = (IURIPortObject) inData[i];
            
            String name = port.getName();
            // find the associated parameter in the configuration
            Parameter<?> p = m_nodeConfig.getParameter(name);
            
            boolean isMultiFile = port.isMultiFile();
            boolean isPrefix = port.isPrefix();
            
            // skip optional and unconnected inport ports
            if (inData[i] == null) {
                ((FileParameter) p).setValue(null);
                continue;
            }
            
            // connected: check contents
            List<URIContent> uris = po.getURIContents();

            // check validity of subtypes with actual inputs
            if (uris.size() > 1 && (!isMultiFile && !isPrefix)) {
                throw new Exception(
                        "IURIPortObject with multiple URIs supplied at single URI port #"
                                + i + ". Use Loops/FileSplitter to branch/iterate or somehow merge the files.");
            }

            // check that we are actually referencing a file parameter from this
            // port
            if (!(p instanceof IFileParameter)) {
                throw new Exception(
                        "Invalid reference from port to non-file parameter. URI port #" + i);
            }

            if (isPrefix) {
                // we pass only the prefix to the tool
                IPrefixURIPortObject puri = (IPrefixURIPortObject) inData[i];
                ((FileParameter) p).setValue(puri.getPrefix());
            } else if (isMultiFile) {
                // we need to collect all filenames and then set them as a batch
                // in the config
                List<String> filenames = new ArrayList<String>();
                for (URIContent uric : uris) {
                    URI uri = uric.getURI();
                    // Resolve the URI to a local path before adding it
                    File localFile = FileUtil.getFileFromURL(uri.toURL());
                    if (localFile == null) {
                        throw new InvalidSettingsException("Tool can only be executed with local files.");
                    }
                    filenames.add(localFile.getAbsolutePath());
                }
                ((FileListParameter) p).setValue(filenames);
            } else {
                // just one filename
                URI uri = uris.get(0).getURI();
                String filename = FileUtil.getFileFromURL(uri.toURL()).getAbsolutePath();
                ((FileParameter) p).setValue(filename);
            }
        }
    }
    
    /**
     * Retrieves the node configuration.
     * 
     * @return the node configuration.
     */
    public INodeConfiguration getNodeConfiguration() {
        return m_nodeConfig;
    }

    /**
     * Returns a collection with the command line elements to execute this node.
     * 
     * @param workingDirectory
     *            The working folder.
     * @return A collection with the command line elements.
     * @throws Exception
     *             If the generation of the command line elements fails.
     */
    public Collection<CommandLineElement> getCommandLine(
            final File workingDirectory) throws Exception {
        final IToolExecutor executor = prepareExecutor(workingDirectory);
        final ICommandGenerator commandGenerator = executor
                .getCommandGenerator();
        return commandGenerator.generateCommands(m_nodeConfig, m_pluginConfig,
                workingDirectory);
    }
}
