/*
 * Copyright (c) 2011, Marc Röttig.
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
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.LoopStartNodeTerminator;

public class ListZipLoopStartNodeModel extends NodeModel implements
		LoopStartNodeTerminator {
	private int m_iteration;

	private static int NinPorts = 4;

	/**
	 * Creates a new model.
	 */
	public ListZipLoopStartNodeModel() {
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

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		assert m_iteration == 0;
		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", 0);

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

	private PortObjectSpec[] outspec;

	private int K;

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

	private int rowCount = 0;

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		if (m_iteration == 0) {
			assert getLoopEndNode() == null : "1st iteration but end node set";
		} else {
			assert getLoopEndNode() != null : "No end node set";
		}

		MIMEURIPortObject[] out = new MIMEURIPortObject[NinPorts];

		rowCount = ((MIMEURIPortObject) inObjects[0]).getURIContents().size();

		for (int i = 0; i < NinPorts; i++) {
			MIMEURIPortObject in = (MIMEURIPortObject) inObjects[i];
			if (i < K) {
				URIContent uri = in.getURIContents().get(m_iteration);
				List<URIContent> uriC = new ArrayList<URIContent>();
				uriC.add(uri);
				out[i] = new MIMEURIPortObject(uriC, in.getSpec().getMIMEType());
			} else {
				List<URIContent> uriC = new ArrayList<URIContent>();
				out[i] = new MIMEURIPortObject(uriC, MIMEType.getType(""));
			}
		}

		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", rowCount);
		m_iteration++;

		return out;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		m_iteration = 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean terminateLoop() {
		boolean continueLoop = (m_iteration != rowCount);
		return !continueLoop;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// no internals to load
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// no internals to save
	}
}
