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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.ballproject.knime.base.util.FileStash;
import org.ballproject.knime.base.util.Helper;
import org.knime.core.data.url.MIMEType;
import org.knime.core.data.url.URIContent;
import org.knime.core.data.url.port.MIMEURIPortObject;
import org.knime.core.data.url.port.MIMEURIPortObjectSpec;
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
import com.genericworkflownodes.knime.config.INodeConfigurationStore;
import com.genericworkflownodes.knime.config.IPluginConfiguration;
import com.genericworkflownodes.knime.config.NodeConfigurationStore;
import com.genericworkflownodes.knime.execution.AsynchronousToolExecutor;
import com.genericworkflownodes.knime.execution.ICommandGenerator;
import com.genericworkflownodes.knime.execution.IToolExecutor;
import com.genericworkflownodes.knime.execution.impl.CancelMonitorThread;
import com.genericworkflownodes.knime.mime.IMIMEtypeRegistry;
import com.genericworkflownodes.knime.outputconverter.IOutputConverter;
import com.genericworkflownodes.knime.outputconverter.config.Converter;
import com.genericworkflownodes.knime.outputconverter.util.OutputConverterHelper;
import com.genericworkflownodes.knime.parameter.FileListParameter;
import com.genericworkflownodes.knime.parameter.InvalidParameterValueException;
import com.genericworkflownodes.knime.parameter.ListParameter;
import com.genericworkflownodes.knime.parameter.Parameter;
import com.genericworkflownodes.knime.port.Port;

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
	public String output = "";

	protected IMIMEtypeRegistry resolver = GenericNodesPlugin
			.getMIMEtypeRegistry();

	/**
	 * stores the node configuration (i.e. parameters, ports, ..)
	 */
	protected INodeConfiguration nodeConfig;

	private final IPluginConfiguration pluginConfig;

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(
			MIMEURIPortObject.class, true);

	protected INodeConfigurationStore store = new NodeConfigurationStore();

	protected IToolExecutor executor;

	/**
	 * Constructor for the node model.
	 */
	protected GenericKnimeNodeModel(INodeConfiguration config,
			IPluginConfiguration pluginConfig) {
		super(createOPOs(config.getInputPorts()), createOPOs(config
				.getOutputPorts()));
		this.nodeConfig = config;
		this.pluginConfig = pluginConfig;
		this.init();
	}

	protected void init() {
		// init with [0,0,....,0]
		this.selected_output_type = new int[this.nodeConfig
				.getNumberOfOutputPorts()];
	}

	protected MIMEType getOutputType(int idx) {
		return this.nodeConfig.getOutputPorts()[idx].getMimeTypes().get(
				this.selected_output_type[idx]);
	}

	protected int getOutputTypeIndex(int idx) {
		return this.selected_output_type[idx];
	}

	private static PortType[] createOPOs(Port[] ports) {
		PortType[] portTypes = new PortType[ports.length];
		Arrays.fill(portTypes, MIMEURIPortObject.TYPE);
		for (int i = 0; i < ports.length; i++) {
			if (ports[i].isOptional()) {
				portTypes[i] = OPTIONAL_PORT_TYPE;
			}
		}
		return portTypes;
	}

	private void prepareExecute(final File jobdir, final ExecutionContext exec)
			throws Exception {

		this.instantiateToolExecutor();

		this.executor.setWorkingDirectory(jobdir);
		this.executor.prepareExecution(this.nodeConfig, this.store,
				this.pluginConfig);

		this.executeTool(jobdir, exec);
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
			executorClassName = this.pluginConfig.getPluginProperties()
					.getProperty("executor");
			commandGeneratorClassName = this.pluginConfig.getPluginProperties()
					.getProperty("commandGenerator");
			if (executorClassName == null || "".equals(executorClassName)) {
				throw new Exception("No executor was specified by the plugin.");
			}

			this.executor = (IToolExecutor) Class.forName(executorClassName)
					.newInstance();

			// configure the executor
			ICommandGenerator generator = (ICommandGenerator) Class.forName(
					commandGeneratorClassName).newInstance();
			this.executor.setCommandGenerator(generator);

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
				this.executor);

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

		this.output = this.executor.getToolOutput();

		GenericNodesPlugin.log(this.output);
		GenericNodesPlugin.log("retcode=" + retcode);

		if (retcode != 0) {
			LOGGER.error(this.output);
			throw new Exception("execution of external tool failed");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
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
		for (String key : this.nodeConfig.getParameterKeys()) {
			settings.addString(key, this.nodeConfig.getParameter(key)
					.getStringRep());
		}
		for (int i = 0; i < this.nodeConfig.getNumberOfOutputPorts(); i++) {
			settings.addInt("GENERIC_KNIME_NODES_outtype#" + i,
					this.getOutputTypeIndex(i));
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
		for (String key : this.nodeConfig.getParameterKeys()) {
			String value = settings.getString(key);
			try {
				this.nodeConfig.getParameter(key).fillFromString(value);
			} catch (InvalidParameterValueException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < this.nodeConfig.getNumberOfOutputPorts(); i++) {
			int idx = settings.getInt("GENERIC_KNIME_NODES_outtype#" + i);
			this.selected_output_type[i] = idx;
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

		for (String key : this.nodeConfig.getParameterKeys()) {
			Parameter<?> param = this.nodeConfig.getParameter(key);
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

	protected MIMEType[][] mimetypes_in;
	protected MIMEType[][] mimetypes_out;
	protected PortObjectSpec[] outspec_;

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		for (Parameter<?> param : this.nodeConfig.getParameters()) {
			// System.out.println(param.getKey()+" "+param.getIsOptional()+" "+param.isNull()+" |"+param.getStringRep());
			if (!param.isOptional() && param.getStringRep().equals("")) {
				// throw new
				// InvalidSettingsException("not all mandatory parameters are set");
				this.setWarningMessage("some mandatory parameters might not be set");
			}

		}

		int nIn = this.mimetypes_in.length;

		for (int i = 0; i < nIn; i++) {
			// not connected input ports have nulls in inSpec
			if (inSpecs[i] == null) {
				// .. if port is optional everything is fine
				if (this.nodeConfig.getInputPorts()[i].isOptional()) {
					continue;
				} else {
					throw new InvalidSettingsException(
							"non-optional input port not connected");
				}
			}

			MIMEURIPortObjectSpec spec = (MIMEURIPortObjectSpec) inSpecs[i];

			// get input MIMEType
			MIMEType mt = spec.getMIMEType();

			// check whether input MIMEType is in list of allowed MIMETypes
			boolean ok = false;
			String mismatch = "";

			for (int j = 0; j < this.mimetypes_in[i].length; j++) {
				if (mt.equals(this.mimetypes_in[i][j])) {
					ok = true;
				} else {
					mismatch = String.format("in: [%s] expected:[%s]", mt,
							Arrays.toString(this.mimetypes_in[i]));
				}
			}
			if (!ok) {
				throw new InvalidSettingsException(
						"invalid MIMEtype at port number " + i + " : "
								+ mismatch);
			}
		}

		// create output spec
		this.outspec_ = this.createOutSpec();

		return this.outspec_;
	}

	protected PortObjectSpec[] createOutSpec() {
		int nOut = this.mimetypes_out.length;
		PortObjectSpec[] out_spec = new PortObjectSpec[nOut];

		// set selected MIMEURIPortObjectSpecs at output ports
		for (int i = 0; i < nOut; i++) {
			// selected output MIMEType
			int selectedMIMETypeIndex = this.getOutputTypeIndex(i);
			out_spec[i] = new MIMEURIPortObjectSpec(
					this.mimetypes_out[i][selectedMIMETypeIndex]);
		}

		return out_spec;
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		// fetch node descriptors
		String nodeName = this.nodeConfig.getName();

		// create job directory
		File jobdir = new File(Helper.getTemporaryDirectory(nodeName,
				!GenericNodesPlugin.isDebug()));
		GenericNodesPlugin.log("jobdir=" + jobdir);

		this.store = new NodeConfigurationStore();

		// prepare input data and parameter values
		List<List<URI>> outputFiles = this.outputParameters(jobdir, inObjects);

		// launch executable
		this.prepareExecute(jobdir, exec);

		// process result files
		PortObject[] outports = this.processOutput(outputFiles, exec);

		if (!GenericNodesPlugin.isDebug()) {
			Helper.deleteDirectory(jobdir);
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
	private List<List<URI>> outputParameters(final File jobdir,
			PortObject[] inData) throws Exception {
		// .. input files
		for (int i = 0; i < inData.length; i++) {
			// skip optional and unconnected inport ports
			if (inData[i] == null) {
				continue;
			}

			Port port = this.nodeConfig.getInputPorts()[i];

			MIMEURIPortObject po = (MIMEURIPortObject) inData[i];
			List<URIContent> uris = po.getURIContents();

			String name = port.getName();
			boolean isMultiFile = port.isMultiFile();

			if (uris.size() > 1 && !isMultiFile) {
				throw new Exception(
						"MIMEURIPortObject with multiple URIs supplied at single URI port #"
								+ i);
			}

			for (URIContent uric : uris) {
				URI uri = uric.getURI();
				String filename = new File(uri).getAbsolutePath();
				GenericNodesPlugin.log("< setting param " + name + "->"
						+ filename);
				this.store.setParameterValue(name, filename);
			}
		}

		List<List<URI>> outfiles = new ArrayList<List<URI>>();

		Map<Port, Integer> port2slot = new HashMap<Port, Integer>();

		// .. output files
		int nOut = this.nodeConfig.getOutputPorts().length;
		for (int i = 0; i < nOut; i++) {
			Port port = this.nodeConfig.getOutputPorts()[i];
			String name = port.getName();

			String ext = this.getOutputType(i).getExtension();

			if (port.isMultiFile()) {
				// keep this list empty for now ...
				List<URI> files = new ArrayList<URI>();
				outfiles.add(files);
				// but store the slot index for later filling
				port2slot.put(port, i);
			} else {
				List<URI> files = new ArrayList<URI>();
				String filename = FileStash.getInstance().allocateFile(ext);
				GenericNodesPlugin.log("> setting param " + name + "->"
						+ filename);
				this.store.setParameterValue(name, filename);
				files.add(new File(filename).toURI());
				outfiles.add(files);
			}
		}

		// .. node parameters
		for (String key : this.nodeConfig.getParameterKeys()) {
			Parameter<?> param = this.nodeConfig.getParameter(key);
			if (param.isNull()) {
				if (param.isOptional()) {
					continue;
				}
			}
			if (param instanceof ListParameter) {
				ListParameter lp = (ListParameter) param;
				if (param instanceof FileListParameter) {
					// FIXME

					FileListParameter flp = (FileListParameter) param;
					List<String> files = lp.getStrings();

					int slot = port2slot.get(flp.getPort());

					String ext = this.getOutputType(slot).getExtension();

					for (String file : files) {
						String filename = FileStash.getInstance().allocateFile(
								ext);
						// TODO
						// URL fileurl =
						// FileStash.getInstance().allocatePortableFile(ext);
						// String filename =
						// fileurl.openConnection().getURL().getFile();
						// String filename = jobdir.getAbsolutePath() +
						// File.separator + file + "." + ext;
						outfiles.get(slot).add(new File(filename).toURI());
						this.store.setMultiParameterValue(key, filename);
					}

				} else {
					for (String val : lp.getStrings()) {
						GenericNodesPlugin.log("@@ setting param " + key + "->"
								+ val);
						this.store.setMultiParameterValue(key, val);
					}
				}
			} else {
				GenericNodesPlugin.log("@ setting param " + key + "->"
						+ param.getValue().toString());
				this.store.setParameterValue(key, param.getValue().toString());
			}
		}

		return outfiles;
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
		int nOut = this.nodeConfig.getOutputPorts().length;

		// create output tables
		MIMEURIPortObject[] outports = new MIMEURIPortObject[nOut];

		for (int i = 0; i < nOut; i++) {
			List<IOutputConverter> converters = new ArrayList<IOutputConverter>();
			if (this.nodeConfig.getOutputConverters().findConverter(
					this.nodeConfig.getOutputPorts()[i].getName()) != null) {
				// we should transform this port
				for (Converter conv : this.nodeConfig.getOutputConverters()
						.findConverter(
								this.nodeConfig.getOutputPorts()[i].getName())) {
					try {
						converters.add(OutputConverterHelper
								.getConfiguredOutputConverter(conv));
					} catch (Exception ex) {
						LOGGER.error("Failed to instantiate converter: "
								+ ex.getMessage());
					}
				}
			}

			List<URIContent> uris = new ArrayList<URIContent>();

			String someFileName = "";
			// multi output file
			for (URI filename : outputFileNames.get(i)) {
				someFileName = filename.getPath();
				URI convertedUri = this.applyConverter(converters, filename);
				uris.add(new URIContent(convertedUri));
			}

			MIMEType mimeType = this.resolveMIMEType(someFileName);
			if (mimeType == null)
				throw new NonExistingMimeTypeException(someFileName);
			outports[i] = new MIMEURIPortObject(uris, mimeType);
		}

		return outports;
	}

	/**
	 * Applies the given list of conversions to the URI.
	 * 
	 * @param converters
	 *            List of converters to apply.
	 * @param filename
	 *            The initial URI to convert.
	 * @return The converted URI.
	 */
	private URI applyConverter(final List<IOutputConverter> converters,
			final URI filename) {
		URI finalConverterdUri = new File(filename.getPath()).toURI();
		for (IOutputConverter converter : converters) {
			finalConverterdUri = converter.convert(finalConverterdUri);
		}
		return finalConverterdUri;
	}

	private MIMEType resolveMIMEType(String filename) {
		return this.resolver.getMIMEtype(filename);
	}

}
