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

package com.genericworkflownodes.knime.generic_node;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.knime.base.filehandling.mime.MIMEMap;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.base.data.port.FileStorePrefixURIPortObject;
import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.IPrefixURIPortObject;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.AsynchronousToolExecutor;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.ToolExecutorFactory;
import com.genericworkflownodes.knime.execution.impl.CancelMonitorThread;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.PluginPreferenceToolLocator;
import com.genericworkflownodes.util.Helper;

/**
 * The GenericKnimeNodeModel is the base class for all derived classes within
 * the GenericKnimeNodes system.
 * 
 * The base class is configured using a {@link INodeConfiguration} object,
 * holding information about:
 * <ul>
 * <li>number of input and output ports</li>
 * <li> {@link MIMEType}s of these ports</li>
 * </ul>
 * 
 * @author
 */
public abstract class GenericKnimeNodeModel extends NodeModel {
    static final String GENERIC_KNIME_NODES_OUTTYPE_PREFIX = "GENERIC_KNIME_NODES_outtype#";

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(GenericKnimeNodeModel.class);

    /**
     * Short-cut for optional ports.
     */
    public static final PortType OPTIONAL_PORT_TYPE = new PortType(
            IURIPortObject.class, true);

    /**
     * Contains information on which of the available output types is selected
     * for each output port.
     */
    protected int[] m_selectedOutputType;

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
    protected GenericKnimeNodeModel(INodeConfiguration nodeConfig,
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

        m_selectedOutputType = new int[m_nodeConfig.getNumberOfOutputPorts()];
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
                .get(m_selectedOutputType[idx]);
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
        return m_selectedOutputType[idx];
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
            }
        }
        return portTypes;
    }

    /**
     * Executes the tool underlying this node.
     * 
     * @param exec
     *            The {@link ExecutionContext} of the node.
     * @throws Exception
     */
    private void executeTool(final ExecutionContext exec)
            throws ExecutionFailedException {

        final AsynchronousToolExecutor asyncExecutor = new AsynchronousToolExecutor(
                m_executor);

        asyncExecutor.invoke();

        // create one thread that will periodically check if the user has
        // cancelled the execution of the node
        // if this monitor thread detects that a cancel was requested, then it
        // will invoke the kill method
        // of the asyncExecutor
        final CancelMonitorThread monitorThread = new CancelMonitorThread(
                asyncExecutor, exec);
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

        GenericNodesPlugin.log("STDOUT: " + m_executor.getToolOutput());
        GenericNodesPlugin.log("STDERR: " + m_executor.getToolErrorOutput());

        GenericNodesPlugin.log("retcode=" + retcode);

        if (retcode != 0) {
            LOGGER.error("Failing process stdout: "
                    + m_executor.getToolOutput());
            LOGGER.error("Failing process stderr: "
                    + m_executor.getToolErrorOutput());
            throw new ExecutionFailedException(m_nodeConfig.getName());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Reset all parameters to its defaults .. how
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
        /*
         * for(Parameter<?> param: config.getParameters()) {
         * param.setValue(null); }
         */
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
            settings.addInt(GENERIC_KNIME_NODES_OUTTYPE_PREFIX + i,
                    getOutputTypeIndex(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // - we know that values are validated and thus are valid
        // - we xfer the values into the corresponding model objects
        for (String key : m_nodeConfig.getParameterKeys()) {
            // FileParameters are not set by the UI
            if (m_nodeConfig.getParameter(key) instanceof IFileParameter)
                continue;

            String value = settings.getString(key);
            try {
                m_nodeConfig.getParameter(key).fillFromString(value);
            } catch (InvalidParameterValueException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                LOGGER.warn(sw.toString());
                try {
                    sw.close();
                    pw.close();
                } catch (IOException e1) {
                }
            }
        }

        for (int i = 0; i < m_nodeConfig.getNumberOfOutputPorts(); i++) {
            int idx = settings.getInt(GENERIC_KNIME_NODES_OUTTYPE_PREFIX + i);
            m_selectedOutputType[i] = idx;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // - we validate incoming settings values here
        // - we do not xfer values to member variables
        // - we throw an exception if something is invalid

        for (String key : m_nodeConfig.getParameterKeys()) {
            Parameter<?> param = m_nodeConfig.getParameter(key);
            // FileParameters are not set by the UI
            if (param instanceof IFileParameter) {
                continue;
            }
            if (!param.isOptional()) {
                if (!settings.containsKey(key)) {
                    GenericNodesPlugin
                            .log("\t no key found for mand. parameter " + key);
                    throw new InvalidSettingsException(
                            "no value for mandatory parameter " + key
                                    + " supplied");
                }
                if (settings.getString(key) == null) {
                    GenericNodesPlugin
                            .log("\t null value found for mand. parameter "
                                    + key);
                    throw new InvalidSettingsException(
                            "no value for mandatory parameter " + key
                                    + " supplied");
                }
            }

            String value = settings.getString(key);
            try {
                param.fillFromString(value);
            } catch (InvalidParameterValueException e) {
                GenericNodesPlugin.log("\t invalid value for parameter " + key);
                throw new InvalidSettingsException(
                        "invalid value for parameter " + key);
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
                setWarningMessage("Some mandatory parameters might are not set.");
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

            // get MIMEType from incoming port
            // TODO: we should check all file extensions, if its more then one
            String mt = MIMEMap.getMIMEType(spec.getFileExtensions().get(0));

            // check whether input MIMEType is in list of allowed MIMETypes
            boolean ok = false;
            if (m_fileEndingsInPorts[i].length > 0) {
                for (int j = 0; j < m_fileEndingsInPorts[i].length && !ok; j++) {
                    if (mt.equals(MIMEMap
                            .getMIMEType(m_fileEndingsInPorts[i][j]))) {
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
                        "has extension: [%s]; expected on of:[%s]", mt,
                        Arrays.toString(m_fileEndingsInPorts[i]));
                throw new InvalidSettingsException(
                        "Invalid MIMEtype at port number " + i + " : "
                                + mismatch);
            }
        }

        return createOutSpec();
    }

    private void checkIfToolExists() throws InvalidSettingsException {
        try {

            File executable = PluginPreferenceToolLocator
                    .getToolLocatorService().getToolPath(getToolForNode());

            if (executable == null) {
                throw new InvalidSettingsException(
                        "Neither externally configured nor shipped "
                                + "binaries exist for this node. Aborting execution.");
            }
        } catch (InvalidSettingsException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidSettingsException(
                    "Failed to find a matching executable in the Tool Registry. "
                            + ex.getMessage());
        }
    }

    private ExternalTool getToolForNode() {
        return new ExternalTool(m_pluginConfig.getPluginId(),
                m_nodeConfig.getName(), m_nodeConfig.getExecutableName());
    }

    protected PortObjectSpec[] createOutSpec() {
        int nOut = m_fileEndingsOutPorts.length;
        PortObjectSpec[] out_spec = new PortObjectSpec[nOut];

        // set selected MIMEURIPortObjectSpecs at output ports
        for (int i = 0; i < nOut; i++) {
            // selected output MIMEType
            int selectedMIMETypeIndex = getOutputTypeIndex(i);
            // TODO: check
            out_spec[i] = new URIPortObjectSpec(
                    m_fileEndingsOutPorts[i][selectedMIMETypeIndex]);
        }

        return out_spec;
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
        // create job directory
        File jobdir = Helper.getTempDir(m_nodeConfig.getName(),
                !GenericNodesPlugin.isDebug());
        GenericNodesPlugin.log("jobdir=" + jobdir);

        // transfer the incoming files into the nodeConfiguration
        transferIncomingPorts2Config(inObjects);

        // prepare input data and parameter values
        List<PortObject> outPorts = transferOutgoingPorts2Config(jobdir,
                inObjects, exec);

        // prepare the executor
        m_executor = ToolExecutorFactory.createToolExecutor(m_pluginConfig
                .getPluginProperties().getProperty("executor"), m_pluginConfig
                .getPluginProperties().getProperty("commandGenerator"));

        m_executor.setWorkingDirectory(jobdir);
        m_executor.prepareExecution(m_nodeConfig, m_pluginConfig);

        // launch executable
        executeTool(exec);

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

        for (int i = 0; i < nOut; i++) {
            Port port = m_nodeConfig.getOutputPorts().get(i);
            String name = port.getName();
            String ext = getOutputType(i);
            boolean isPrefix = port.isPrefix();

            Parameter<?> p = m_nodeConfig.getParameter(name);

            // basenames and number of output files guessed from input
            List<String> basenames = getOutputBaseNames();

            if (p instanceof FileListParameter && port.isMultiFile()) {
                // we currently do not support lists of prefixes
                if (isPrefix) {
                    throw new InvalidSettingsException(
                            "GKN currently does not support lists of prefixes as output.");
                }

                FileListParameter flp = (FileListParameter) p;
                List<String> fileNames = new ArrayList<String>();

                if (basenames.size() == 0) {
                    throw new Exception(
                            "Cannot determine number of output files if no input file is given.");
                }

                FileStoreURIPortObject fsupo = new FileStoreURIPortObject(
                        exec.createFileStore(m_nodeConfig.getName() + "_" + i));

                for (int f = 0; f < basenames.size(); ++f) {
                    // create basename: <base_name>_<port_nr>_<outfile_nr>
                    String file_basename = String.format("%s_%d_%d",
                            basenames.get(f), i, f);
                    File file = fsupo.registerFile(file_basename + "." + ext);
                    fileNames.add(file.getAbsolutePath());
                }

                // add filled portobject
                outPorts.add(fsupo);

                // overwrite existing settings with new values generated by the
                // stash
                flp.setValue(fileNames);

            } else if (p instanceof FileParameter && !port.isMultiFile()) {
                // if we have no basename to use (e.g., Node without input-file)
                // we use the nodename
                String basename;
                if (basenames.isEmpty()) {
                    basename = m_nodeConfig.getName();
                } else {
                    basename = basenames.get(0);
                }

                // create basename: <base_name>_<port_nr>_<outfile_nr>
                String file_basename = String.format("%s_%d", basename, i);
                String fileName = file_basename + '.' + ext;

                if (isPrefix) {
                    FileStorePrefixURIPortObject fspup = new FileStorePrefixURIPortObject(
                            exec.createFileStore(m_nodeConfig.getName() + "_"
                                    + i), fileName);
                    ((FileParameter) p).setValue(fspup.getPrefix());
                    GenericNodesPlugin.log("> setting param " + name + "->"
                            + fspup.getPrefix());

                    outPorts.add(fspup);
                } else {
                    FileStoreURIPortObject fsupo = new FileStoreURIPortObject(
                            exec.createFileStore(m_nodeConfig.getName() + "_"
                                    + i));

                    // we do not append the file extension if we have a prefix
                    File file = fsupo.registerFile(fileName);
                    ((FileParameter) p).setValue(file.getAbsolutePath());
                    GenericNodesPlugin.log("> setting param " + name + "->"
                            + file);

                    // remember output file
                    outPorts.add(fsupo);
                }
            } else {
                throw new Exception(
                        "Invalid connection between ports and parameters.");
            }
        }
        return outPorts;
    }

    /**
     * Tries to guess the optimal output file names given all the input edges.
     * The file names will be extracted from the configuration, hence the file
     * names need to be transferred into config prior to using this method. See
     * {@link GenericKnimeNodeModel#transferIncomingPorts2Config(PortObject[])}.
     * 
     * @return A list of base names for the output files.
     * @throws Exception
     */
    private List<String> getOutputBaseNames() throws Exception {

        // 1. we select always the list with the highest number of files.
        // 2. we prefer lists over files (independent of the number of
        // elements).
        // 3. we prefer files over prefixes since we assume that prefixes are
        // often indices or reference data

        List<String> basenames = new ArrayList<String>();

        // find the port
        int naming_port = 0;
        int max_size = -1;
        boolean seen_prefix = false;
        boolean is_fileParameter = false;
        for (int i = 0; i < m_nodeConfig.getInputPorts().size(); ++i) {
            Port port = m_nodeConfig.getInputPorts().get(i);
            String name = port.getName();
            Parameter<?> p = m_nodeConfig.getParameter(name);

            if (p instanceof FileListParameter) {
                FileListParameter flp = (FileListParameter) p;
                if (max_size == -1
                        || (is_fileParameter && (max_size <= flp.getValue()
                                .size()))) {
                    max_size = flp.getValue().size();
                    naming_port = i;
                } else if (flp.getValue().size() != max_size) {
                    throw new Exception(
                            "The number of output files cannot be determined since multiple input file lists with disagreeing numbers exist.");

                }
            } else if (max_size == -1 || seen_prefix) {
                // is a regular incoming port but we have no better option
                max_size = 1;
                naming_port = i;
                // indicating that we have (for now) selected a file parameter
                // which will be overruled by any FileListParameter
                is_fileParameter = true;
                seen_prefix = port.isPrefix();
            }
        }

        if (m_nodeConfig.getInputPorts().size() > 0) {
            // generate the filenames if there are input ports
            // without ports, the names are set in transferOutgoingPorts2Config
            Port port = m_nodeConfig.getInputPorts().get(naming_port);
            String name = port.getName();
            Parameter<?> p = m_nodeConfig.getParameter(name);

            if (p instanceof FileListParameter) {
                // we have multiple base names
                FileListParameter flp = (FileListParameter) p;
                for (String fName : flp.getValue()) {
                    basenames.add(FilenameUtils.getBaseName(fName));
                }
            } else {
                // we only have a single basename
                // FilenameUtils.getBaseName()
                basenames.add(FilenameUtils.getBaseName(((FileParameter) p)
                        .getValue()));
            }
        }

        return basenames;
    }

    /**
     * Transfers the incoming ports into the config, that it can be written out
     * into a config file or can be tranferred to the command line.
     * 
     * @param inData
     *            The incoming port objects.
     * @throws Exception
     */
    private void transferIncomingPorts2Config(PortObject[] inData)
            throws Exception {
        // Transfer settings from the input ports into the configuration object
        for (int i = 0; i < inData.length; i++) {
            // skip optional and unconnected inport ports
            if (inData[i] == null) {
                continue;
            }

            // find the internal port for this PortObject
            Port port = m_nodeConfig.getInputPorts().get(i);

            IURIPortObject po = (IURIPortObject) inData[i];
            List<URIContent> uris = po.getURIContents();

            String name = port.getName();
            boolean isMultiFile = port.isMultiFile();
            boolean isPrefix = port.isPrefix();

            if (uris.size() > 1 && (!isMultiFile && !isPrefix)) {
                throw new Exception(
                        "IURIPortObject with multiple URIs supplied at single URI port #"
                                + i);
            }

            // find the associated parameter in the configuration
            Parameter<?> p = m_nodeConfig.getParameter(name);
            // check that we are actually referencing a file parameter from this
            // port
            if (!(p instanceof IFileParameter)) {
                throw new Exception(
                        "Invalid reference from port to non-file parameter. URI port #"
                                + i);
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
                    filenames.add(new File(uri).getAbsolutePath());
                }
                ((FileListParameter) p).setValue(filenames);
            } else {
                // just one filename
                URI uri = uris.get(0).getURI();
                String filename = new File(uri).getAbsolutePath();
                ((FileParameter) p).setValue(filename);
            }
        }
    }
}
