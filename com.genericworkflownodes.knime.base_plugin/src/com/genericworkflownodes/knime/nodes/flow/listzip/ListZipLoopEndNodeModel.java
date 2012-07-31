/**
 * 
 */
package com.genericworkflownodes.knime.nodes.flow.listzip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.knime.core.node.workflow.LoopEndNode;
import org.knime.core.node.workflow.LoopStartNodeTerminator;

/**
 * @author roettig
 * 
 */
public class ListZipLoopEndNodeModel extends NodeModel implements LoopEndNode {
	// the logger instance
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(ListZipLoopEndNodeModel.class);

	private int m_count = 0;
	private long m_startTime;
	private static int NinPorts = 4;

	protected ListZipLoopEndNodeModel() {
		super(createIPOs(), createOPOs());
	}

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(
			MIMEURIPortObject.class, true);

	private static PortType[] createIPOs() {
		PortType[] portTypes = new PortType[NinPorts];
		Arrays.fill(portTypes, MIMEURIPortObject.TYPE);
		portTypes[1] = OPTIONAL_PORT_TYPE;
		portTypes[2] = OPTIONAL_PORT_TYPE;
		portTypes[3] = OPTIONAL_PORT_TYPE;
		return portTypes;
	}

	private static PortType[] createOPOs() {
		PortType[] portTypes = new PortType[NinPorts];
		Arrays.fill(portTypes, MIMEURIPortObject.TYPE);
		portTypes[1] = OPTIONAL_PORT_TYPE;
		portTypes[2] = OPTIONAL_PORT_TYPE;
		portTypes[3] = OPTIONAL_PORT_TYPE;
		return portTypes;
	}

	private PortObjectSpec[] outspec;

	private int K;

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		List<MIMEURIPortObjectSpec> specs = new ArrayList<MIMEURIPortObjectSpec>();

		for (int i = 0; i < NinPorts; i++) {
			if (inSpecs[i] == null) {
				break;
			}
			MIMEURIPortObjectSpec spec = (MIMEURIPortObjectSpec) inSpecs[i];
			specs.add(spec);
		}

		outspec = getOutSpec(specs);

		return outspec;
	}

	private PortObjectSpec[] getOutSpec(List<MIMEURIPortObjectSpec> specs) {
		K = specs.size();

		PortObjectSpec[] ret = new PortObjectSpec[NinPorts];

		for (int i = 0; i < NinPorts; i++) {
			if (i < K) {
				ret[i] = specs.get(i);
			} else {
				ret[i] = null;
			}
		}

		return ret;
	}

	private List<List<URIContent>> uris;

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {

		if (!(this.getLoopStartNode() instanceof LoopStartNodeTerminator)) {
			throw new IllegalStateException("Loop End is not connected"
					+ " to matching/corresponding Loop Start node. You"
					+ " are trying to create an infinite loop!");
		}

		if (uris == null) {
			// first time we are getting to this: open container
			m_startTime = System.currentTimeMillis();
			m_count = 0;
			uris = new ArrayList<List<URIContent>>();
			for (int i = 0; i < K; i++) {
				uris.add(new ArrayList<URIContent>());
			}
		}

		MIMEType[] mts = new MIMEType[NinPorts];

		for (int i = 0; i < K; i++) {
			MIMEURIPortObject po = (MIMEURIPortObject) inObjects[i];
			MIMEType mt = po.getSpec().getMIMEType();
			uris.get(i).add(po.getURIContents().get(0));
			mts[i] = mt;
		}

		boolean terminateLoop = ((LoopStartNodeTerminator) this
				.getLoopStartNode()).terminateLoop();

		if (terminateLoop) {
			MIMEURIPortObject[] ret = new MIMEURIPortObject[NinPorts];

			for (int i = 0; i < NinPorts; i++) {
				if (i < K) {
					ret[i] = new MIMEURIPortObject(uris.get(i), mts[i]);
				} else {
					List<URIContent> uriC = new ArrayList<URIContent>();
					ret[i] = new MIMEURIPortObject(uriC, MIMEType.getType(""));
				}
			}

			uris = null;
			m_count = 0;
			LOGGER.debug("Total loop execution time: "
					+ (System.currentTimeMillis() - m_startTime) + "ms");
			m_startTime = 0;

			return ret;
		} else {
			continueLoop();
			m_count++;
			return new PortObject[NinPorts];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#loadInternals(java.io.File,
	 * org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void loadInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.NodeModel#loadValidatedSettingsFrom(org.knime.core
	 * .node.NodeSettingsRO)
	 */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO arg0)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#reset()
	 */
	@Override
	protected void reset() {
		m_count = 0;
		uris = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#saveInternals(java.io.File,
	 * org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void saveInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#saveSettingsTo(org.knime.core.node.
	 * NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#validateSettings(org.knime.core.node.
	 * NodeSettingsRO)
	 */
	@Override
	protected void validateSettings(NodeSettingsRO arg0)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

}
