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
 * Node model for the ListZipEnd node.
 * 
 * @author roettig, aiche
 */
public class ListZipLoopEndNodeModel extends NodeModel implements LoopEndNode {
	// the logger instance
	@SuppressWarnings("unused")
	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(ListZipLoopEndNodeModel.class);
	private static int PORT_COUNT = 4;

	private int m_numOfAssignedPorts;
	private List<List<URIContent>> m_uris;

	protected ListZipLoopEndNodeModel() {
		super(createInputPortObjectSpecs(), createOutputPortObjectSpecs());
	}

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(
			URIPortObject.class, true);

	private static PortType[] createInputPortObjectSpecs() {
		PortType[] portTypes = new PortType[PORT_COUNT];
		Arrays.fill(portTypes, URIPortObject.TYPE);
		portTypes[1] = OPTIONAL_PORT_TYPE;
		portTypes[2] = OPTIONAL_PORT_TYPE;
		portTypes[3] = OPTIONAL_PORT_TYPE;
		return portTypes;
	}

	private static PortType[] createOutputPortObjectSpecs() {
		PortType[] portTypes = new PortType[PORT_COUNT];
		Arrays.fill(portTypes, URIPortObject.TYPE);
		portTypes[1] = OPTIONAL_PORT_TYPE;
		portTypes[2] = OPTIONAL_PORT_TYPE;
		portTypes[3] = OPTIONAL_PORT_TYPE;
		return portTypes;
	}

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		List<URIPortObjectSpec> specs = new ArrayList<URIPortObjectSpec>();

		for (int i = 0; i < PORT_COUNT; i++) {
			if (inSpecs[i] == null) {
				break;
			}
			URIPortObjectSpec spec = (URIPortObjectSpec) inSpecs[i];
			specs.add(spec);
		}

		return getOutSpec(specs);
	}

	private PortObjectSpec[] getOutSpec(List<URIPortObjectSpec> specs) {
		m_numOfAssignedPorts = specs.size();

		PortObjectSpec[] ret = new PortObjectSpec[PORT_COUNT];

		for (int i = 0; i < PORT_COUNT; i++) {
			if (i < m_numOfAssignedPorts) {
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

		if (!(this.getLoopStartNode() instanceof LoopStartNodeTerminator)) {
			throw new IllegalStateException("Loop End is not connected"
					+ " to matching/corresponding Loop Start node. You"
					+ " are trying to create an infinite loop!");
		}

		if (m_uris == null) {
			// first time we are getting to this: open container
			m_uris = new ArrayList<List<URIContent>>();
			for (int i = 0; i < m_numOfAssignedPorts; i++) {
				m_uris.add(new ArrayList<URIContent>());
			}
		}

		for (int i = 0; i < m_numOfAssignedPorts; i++) {
			URIPortObject po = (URIPortObject) inObjects[i];
			m_uris.get(i).add(po.getURIContents().get(0));
		}

		boolean terminateLoop = ((LoopStartNodeTerminator) this
				.getLoopStartNode()).terminateLoop();

		if (terminateLoop) {
			URIPortObject[] ret = new URIPortObject[PORT_COUNT];

			for (int i = 0; i < PORT_COUNT; i++) {
				if (i < m_numOfAssignedPorts) {
					ret[i] = new URIPortObject(m_uris.get(i));
				} else {
					List<URIContent> uriC = new ArrayList<URIContent>();
					ret[i] = new URIPortObject(uriC);
				}
			}

			m_uris = null;
			return ret;
		} else {
			continueLoop();
			return new PortObject[PORT_COUNT];
		}
	}

	@Override
	protected void loadInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException {
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO arg0)
			throws InvalidSettingsException {
	}

	@Override
	protected void reset() {
		m_uris = null;
	}

	@Override
	protected void saveInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException {
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO arg0) {
	}

	@Override
	protected void validateSettings(NodeSettingsRO arg0)
			throws InvalidSettingsException {
	}

}
