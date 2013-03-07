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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.eclipse.ui.PlatformUI;
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
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.execution.AsynchronousToolExecutor;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.impl.CancelMonitorThread;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.FileParameter;
import com.genericworkflownodes.knime.parameter.IFileParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;
import com.genericworkflownodes.knime.toolfinderservice.ExternalTool;
import com.genericworkflownodes.knime.toolfinderservice.IToolLocatorService;
import com.genericworkflownodes.util.FileStash;
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
	/**
	 * The local LOGGER.
	 */
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(GenericKnimeNodeModel.class);

	protected int[] selected_output_type;

	/**
	 * stores the node configuration (i.e. parameters, ports, ..)
	 */
	protected INodeConfiguration nodeConfig;

	private final IPluginConfiguration pluginConfig;

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(
			URIPortObject.class, true);

	protected String[][] mimetypes_in;
	protected String[][] mimetypes_out;
	protected PortObjectSpec[] outspec_;

	/**
	 * The actual executor used to run the tool.
	 */
	IToolExecutor executor;

	/**
	 * Constructor for the node model.
	 */
	protected GenericKnimeNodeModel(INodeConfiguration config,
			IPluginConfiguration pluginConfig) {
		super(createOPOs(config.getInputPorts()), createOPOs(config
				.getOutputPorts()));
		nodeConfig = config;
		this.pluginConfig = pluginConfig;
		init();
	}

	protected void init() {
		// init with [0,0,....,0]
		selected_output_type = new int[nodeConfig.getNumberOfOutputPorts()];
	}

	protected String getOutputType(int idx) {
		return nodeConfig.getOutputPorts().get(idx).getMimeTypes()
				.get(selected_output_type[idx]);
	}

	protected int getOutputTypeIndex(int idx) {
		return selected_output_type[idx];
	}

	private static PortType[] createOPOs(List<Port> ports) {
		PortType[] portTypes = new PortType[ports.size()];
		Arrays.fill(portTypes, URIPortObject.TYPE);
		for (int i = 0; i < ports.size(); i++) {
			if (ports.get(i).isOptional()) {
				portTypes[i] = OPTIONAL_PORT_TYPE;
			}
		}
		return portTypes;
	}

	private void prepareExecute(final File jobdir, final ExecutionContext exec)
			throws Exception {

		instantiateToolExecutor();

		executor.setWorkingDirectory(jobdir);
		executor.prepareExecution(nodeConfig, pluginConfig);

		executeTool(jobdir, exec);
	}

	/**
	 * Try to instantiate the IToolExecutor specified by the plugin.
	 * 
	 * @throws Exception
	 */
	private void instantiateToolExecutor() throws Exception {

		String executorClassName = "";
		String commandGeneratorClassName = "";
		try {
			executorClassName = pluginConfig.getPluginProperties().getProperty(
					"executor");
			commandGeneratorClassName = pluginConfig.getPluginProperties()
					.getProperty("commandGenerator");
			if (executorClassName == null || "".equals(executorClassName)) {
				throw new Exception("No executor was specified by the plugin.");
			}

			executor = (IToolExecutor) Class.forName(executorClassName)
					.newInstance();

			// configure the executor
			ICommandGenerator generator = (ICommandGenerator) Class.forName(
					commandGeneratorClassName).newInstance();
			executor.setCommandGenerator(generator);

		} catch (IllegalAccessException ex) {
			throw new Exception(
					"Could not instantiate executor/generator (IllegalAccessException): "
							+ executorClassName + "/"
							+ commandGeneratorClassName);
		} catch (ClassNotFoundException ex) {
			throw new Exception(
					"Could not instantiate executor/generator (ClassNotFoundException): "
							+ executorClassName + "/"
							+ commandGeneratorClassName);
		} catch (InstantiationException ex) {
			throw new Exception(
					"Could not instantiate executor/generator (InstantiationException): "
							+ executorClassName + "/"
							+ commandGeneratorClassName);
		}
	}

	private void executeTool(final File jobdir, final ExecutionContext exec)
			throws Exception {

		final AsynchronousToolExecutor asyncExecutor = new AsynchronousToolExecutor(
				executor);

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
			ex.printStackTrace();
		}

		GenericNodesPlugin.log("STDOUT: " + executor.getToolOutput());
		GenericNodesPlugin.log("STDERR: " + executor.getToolErrorOutput());

		GenericNodesPlugin.log("retcode=" + retcode);

		if (retcode != 0) {
			LOGGER.error("Failing process stdout: " + executor.getToolOutput());
			LOGGER.error("Failing process stderr: "
					+ executor.getToolErrorOutput());
			throw new Exception("Execution of external tool failed.");
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
		for (String key : nodeConfig.getParameterKeys()) {
			settings.addString(key, nodeConfig.getParameter(key).getStringRep());
		}
		for (int i = 0; i < nodeConfig.getNumberOfOutputPorts(); i++) {
			settings.addInt("GENERIC_KNIME_NODES_outtype#" + i,
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
		for (String key : nodeConfig.getParameterKeys()) {
			// FileParameters are not set by the UI
			if (nodeConfig.getParameter(key) instanceof IFileParameter)
				continue;

			String value = settings.getString(key);
			try {
				nodeConfig.getParameter(key).fillFromString(value);
			} catch (InvalidParameterValueException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < nodeConfig.getNumberOfOutputPorts(); i++) {
			int idx = settings.getInt("GENERIC_KNIME_NODES_outtype#" + i);
			selected_output_type[i] = idx;
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

		for (String key : nodeConfig.getParameterKeys()) {
			Parameter<?> param = nodeConfig.getParameter(key);
			// FileParameters are not set by the UI
			if (param instanceof IFileParameter)
				continue;
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

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {

		// Test if the named tool exists in the tool-db, if not throws an
		// exception to tell the user that the executable is missing.
		checkIfToolExists();

		for (Parameter<?> param : nodeConfig.getParameters()) {
			if (!param.isOptional() && param.getValue() != null
					&& "".equals(param.getStringRep())
					&& !(param instanceof IFileParameter)) {
				setWarningMessage("some mandatory parameters might not be set");
			}
		}

		int nIn = mimetypes_in.length;

		for (int i = 0; i < nIn; i++) {
			// not connected input ports have nulls in inSpec
			if (inSpecs[i] == null) {
				// .. if port is optional everything is fine
				if (nodeConfig.getInputPorts().get(i).isOptional()) {
					continue;
				} else {
					throw new InvalidSettingsException(
							"non-optional input port not connected");
				}
			}

			URIPortObjectSpec spec = (URIPortObjectSpec) inSpecs[i];

			// get input MIMEType
			// TODO: why do we have more then one
			String mt = spec.getFileExtensions().get(0);

			// check whether input MIMEType is in list of allowed MIMETypes
			boolean ok = false;
			String mismatch = "";

			for (int j = 0; j < mimetypes_in[i].length; j++) {
				if (mt.toLowerCase().equals(mimetypes_in[i][j].toLowerCase())) {
					ok = true;
				} else {
					mismatch = String.format("in: [%s] expected:[%s]", mt,
							Arrays.toString(mimetypes_in[i]));
				}
			}
			if (!ok) {
				throw new InvalidSettingsException(
						"invalid MIMEtype at port number " + i + " : "
								+ mismatch);
			}
		}

		// create output spec
		outspec_ = createOutSpec();

		return outspec_;
	}

	private void checkIfToolExists() throws InvalidSettingsException {
		IToolLocatorService toolLocator = (IToolLocatorService) PlatformUI
				.getWorkbench().getService(IToolLocatorService.class);

		try {

			if (toolLocator == null) {
				throw new InvalidSettingsException(
						"Could not find matching ToolLocatorService.");
			}

			File executable = toolLocator.getToolPath(new ExternalTool(
					pluginConfig.getPluginId(), nodeConfig.getName(),
					nodeConfig.getExecutableName()));

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

	protected PortObjectSpec[] createOutSpec() {
		int nOut = mimetypes_out.length;
		PortObjectSpec[] out_spec = new PortObjectSpec[nOut];

		// set selected MIMEURIPortObjectSpecs at output ports
		for (int i = 0; i < nOut; i++) {
			// selected output MIMEType
			int selectedMIMETypeIndex = getOutputTypeIndex(i);
			// TODO: check
			out_spec[i] = new URIPortObjectSpec(
					mimetypes_out[i][selectedMIMETypeIndex]);
		}

		return out_spec;
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		// fetch node descriptors
		String nodeName = nodeConfig.getName();

		// create job directory
		File jobdir = new File(Helper.getTemporaryDirectory(nodeName,
				!GenericNodesPlugin.isDebug()));
		GenericNodesPlugin.log("jobdir=" + jobdir);

		// transfer the incoming files into the nodeConfiguration
		transferIncomingPorts2Config(inObjects);

		// prepare input data and parameter values
		List<List<URI>> outputFiles = transferOutgoingPorts2Config(jobdir,
				inObjects);

		// launch executable
		prepareExecute(jobdir, exec);

		// process result files
		PortObject[] outports = processOutput(outputFiles, exec);

		if (!GenericNodesPlugin.isDebug()) {
			FileUtils.deleteDirectory(jobdir);
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
	private List<List<URI>> transferOutgoingPorts2Config(final File jobdir,
			PortObject[] inData) throws Exception {

		List<List<URI>> outfiles = new ArrayList<List<URI>>();

		int nOut = nodeConfig.getOutputPorts().size();

		for (int i = 0; i < nOut; i++) {
			Port port = nodeConfig.getOutputPorts().get(i);
			String name = port.getName();
			String ext = getOutputType(i);

			Parameter<?> p = nodeConfig.getParameter(name);
			// used to remember which files we actually generated here
			List<URI> fileURIs = new ArrayList<URI>();
			if (p instanceof FileListParameter && port.isMultiFile()) {

				FileListParameter flp = (FileListParameter) p;
				int numberOfOutputFiles = getNumberOfOutputFiles();
				List<String> files = new ArrayList<String>();

				for (int f = 0; f < numberOfOutputFiles; ++f) {
					String filename = FileStash.getInstance().allocateFile(ext);
					files.add(filename);
					fileURIs.add(new File(filename).toURI());
				}

				// overwrite existing settings with new values generated by the
				// stash
				flp.setValue(files);

			} else if (p instanceof FileParameter && !port.isMultiFile()) {
				String filename = FileStash.getInstance().allocateFile(ext);
				((FileParameter) p).setValue(filename);
				GenericNodesPlugin.log("> setting param " + name + "->"
						+ filename);

				// remember output file
				fileURIs.add(new File(filename).toURI());
			} else {
				throw new Exception(
						"Invalid connection between ports and parameters.");
			}

			outfiles.add(fileURIs);
		}

		return outfiles;
	}

	/**
	 * Determines the number of output files based on the incoming ports.
	 * 
	 * @param flp
	 * @return
	 * @throws Exception
	 */
	private int getNumberOfOutputFiles() throws Exception {
		int numberOfOutputFiles = -1;

		// check for input lists
		for (int i = 0; i < nodeConfig.getInputPorts().size(); ++i) {
			Port port = nodeConfig.getInputPorts().get(i);
			String name = port.getName();
			Parameter<?> p = nodeConfig.getParameter(name);

			// we only check FileListParameter
			if (p instanceof FileListParameter) {
				if (numberOfOutputFiles == -1)
					numberOfOutputFiles = ((FileListParameter) p).getValue()
							.size();
				else {
					// check if the values agree
					if (((FileListParameter) p).getValue().size() != numberOfOutputFiles)
						throw new Exception(
								"The number of output files cannot be determined since multiple input file lists with disagreeing numbers exist.");
				}
			}
		}

		if (numberOfOutputFiles == -1) {
			throw new Exception(
					"The number of output files cannot be determined since no input file list was found to determine the output size.");
		}

		return numberOfOutputFiles;
	}

	private void transferIncomingPorts2Config(PortObject[] inData)
			throws Exception {
		// Transfer settings from the input ports into the configuration object
		for (int i = 0; i < inData.length; i++) {
			// skip optional and unconnected inport ports
			if (inData[i] == null) {
				continue;
			}

			// find the internal port for this PortObject
			Port port = nodeConfig.getInputPorts().get(i);

			URIPortObject po = (URIPortObject) inData[i];
			List<URIContent> uris = po.getURIContents();

			String name = port.getName();
			boolean isMultiFile = port.isMultiFile();

			if (uris.size() > 1 && !isMultiFile) {
				throw new Exception(
						"MIMEURIPortObject with multiple URIs supplied at single URI port #"
								+ i);
			}

			// find the associated parameter in the configuration
			Parameter<?> p = nodeConfig.getParameter(name);
			// check that we are actually referencing a file parameter from this
			// port
			if (!(p instanceof IFileParameter)) {
				throw new Exception(
						"Invalid reference from port to non-file parameter. URI port #"
								+ i);
			}

			if (isMultiFile) {
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

	/**
	 * Converts the given list of output files to an array of {@link PortObject}
	 * s that can be passed on in the current workflow.
	 * 
	 * @param outputFileNames
	 *            The output name as list of lists of {@link URI}.
	 * @param exec
	 *            The execution context of the current node.
	 * @return
	 * @throws Exception
	 */
	private PortObject[] processOutput(final List<List<URI>> outputFileNames,
			final ExecutionContext exec) throws Exception {
		int nOut = nodeConfig.getOutputPorts().size();

		// create output tables
		URIPortObject[] outports = new URIPortObject[nOut];

		for (int i = 0; i < nOut; i++) {
			List<URIContent> uris = new ArrayList<URIContent>();

			String someFileName = "";
			// multi output file
			for (URI filename : outputFileNames.get(i)) {
				someFileName = filename.getPath();
				uris.add(new URIContent(filename, getExtension(filename
						.getPath())));
			}

			String mimeType = getExtension(someFileName);
			if (mimeType == null)
				throw new NonExistingMimeTypeException(someFileName);
			outports[i] = new URIPortObject(uris);
		}

		return outports;
	}

	private String getExtension(String path) {
		if (path.lastIndexOf('.') == -1) {
			return "";
		} else {
			return path.substring(path.lastIndexOf('.') + 1);
		}
	}

}
