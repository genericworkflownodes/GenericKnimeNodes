/**
 * Copyright (c) 2011, Marc Röttig.
 * Copyright (c) 2013, Stephan Aiche.
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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.LoopStartNodeTerminator;

/**
 * Node model for the ListZipLoopStart node.
 * 
 * @author aiche
 */
public class ListZipLoopStartNodeModel extends NodeModel implements
		LoopStartNodeTerminator {

	/**
	 * The current iteration.
	 */
	private int m_iteration;

	/**
	 * The number of incoming ports that were actually connected by the user.
	 */
	private int m_numAssignedIncomingPorts;

	/**
	 * The actual number of iterations.
	 */
	private int m_rowCount = 0;

	private static int NinPorts = 4;

	static String CFG_RECYCLE = "recycle_1st_port";
	static boolean DEFAULT_RECYCLE = false;
	SettingsModelBoolean m_multiplex = new SettingsModelBoolean(CFG_RECYCLE,
			DEFAULT_RECYCLE);

	/**
	 * Creates a new model.
	 */
	public ListZipLoopStartNodeModel() {
		super(createIncomingPortObjects(), createOutgoingPortObjects());
	}

	private static final PortType OPTIONAL_PORT_TYPE = new PortType(
			URIPortObject.class, true);

	private static PortType[] createIncomingPortObjects() {
		PortType[] portTypes = new PortType[NinPorts];
		Arrays.fill(portTypes, URIPortObject.TYPE);
		portTypes[1] = OPTIONAL_PORT_TYPE;
		portTypes[2] = OPTIONAL_PORT_TYPE;
		portTypes[3] = OPTIONAL_PORT_TYPE;
		return portTypes;
	}

	private static PortType[] createOutgoingPortObjects() {
		PortType[] portTypes = new PortType[NinPorts];
		Arrays.fill(portTypes, URIPortObject.TYPE);
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

		List<URIPortObjectSpec> specs = new ArrayList<URIPortObjectSpec>();

		for (int i = 0; i < NinPorts; i++) {
			if (inSpecs[i] == null) {
				break;
			}
			URIPortObjectSpec spec = (URIPortObjectSpec) inSpecs[i];
			specs.add(spec);
		}

		return getOutputSpec(specs);
	}

	private PortObjectSpec[] getOutputSpec(List<URIPortObjectSpec> specs) {
		m_numAssignedIncomingPorts = specs.size();

		PortObjectSpec[] ret = new PortObjectSpec[NinPorts];

		for (int i = 0; i < NinPorts; i++) {
			if (i < m_numAssignedIncomingPorts) {
				ret[i] = specs.get(i);
			} else {
				ret[i] = null;
			}
		}

		return ret;
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {

		// check the loop conditions
		if (m_iteration == 0) {
			assert getLoopEndNode() == null : "1st iteration but end node set";
		} else {
			assert getLoopEndNode() != null : "No end node set";
		}

		URIPortObject[] out = new URIPortObject[NinPorts];
		m_rowCount = ((URIPortObject) inObjects[0]).getURIContents().size();

		for (int i = 0; i < NinPorts; i++) {
			URIPortObject in = (URIPortObject) inObjects[i];
			if (i < m_numAssignedIncomingPorts) {
				URIContent uri = in.getURIContents().get(m_iteration);
				List<URIContent> uriContents = new ArrayList<URIContent>();
				uriContents.add(uri);
				out[i] = new URIPortObject(uriContents);
			} else {
				out[i] = new URIPortObject(new ArrayList<URIContent>());
			}
		}

		// TODO: check if this is necessary
		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", m_rowCount);

		// proceed in the number of iterations
		m_iteration++;

		return out;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		m_iteration = 0;
		m_multiplex.setBooleanValue(DEFAULT_RECYCLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean terminateLoop() {
		return m_iteration == m_rowCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_multiplex.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_multiplex.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_multiplex.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}
}
