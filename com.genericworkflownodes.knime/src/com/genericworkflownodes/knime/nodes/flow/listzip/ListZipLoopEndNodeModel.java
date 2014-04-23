/**
 * Copyright (c) 2011, Marc Röttig.
 * Copyright (c) 2013-2014, Stephan Aiche.
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

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
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

import com.genericworkflownodes.knime.base.data.port.AbstractFileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.PortObjectHandlerCell;

/**
 * Node model for the ListZipEnd node.
 * 
 * @author roettig, aiche
 */
public class ListZipLoopEndNodeModel extends NodeModel implements LoopEndNode,
        BufferedDataTableHolder {

    /**
     * The logger instance.
     */
    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ListZipLoopEndNodeModel.class);
    /**
     * Number of incoming/outgoing ports.
     */
    private static final int PORT_COUNT = 4;

    /**
     * Array of containers used to make filestores permanent.
     */
    private BufferedDataContainer[] m_bufferedContainers;

    /**
     * List of URIContents collected during execution. Used later on to create
     * the outgoing port objects.
     */
    private List<List<URIContent>> m_uris;

    /**
     * Indicating if the loop is already running.
     */
    private boolean m_loopStarted;

    /**
     * C'tor.
     */
    protected ListZipLoopEndNodeModel() {
        super(createInputPortObjectSpecs(), createOutputPortObjectSpecs());
        m_loopStarted = false;
    }

    public static final PortType OPTIONAL_PORT_TYPE = new PortType(
            IURIPortObject.class, true);

    private static PortType[] createInputPortObjectSpecs() {
        PortType[] portTypes = new PortType[PORT_COUNT];
        Arrays.fill(portTypes, IURIPortObject.TYPE);
        portTypes[1] = OPTIONAL_PORT_TYPE;
        portTypes[2] = OPTIONAL_PORT_TYPE;
        portTypes[3] = OPTIONAL_PORT_TYPE;
        return portTypes;
    }

    private static PortType[] createOutputPortObjectSpecs() {
        PortType[] portTypes = new PortType[PORT_COUNT];
        Arrays.fill(portTypes, IURIPortObject.TYPE);
        portTypes[1] = OPTIONAL_PORT_TYPE;
        portTypes[2] = OPTIONAL_PORT_TYPE;
        portTypes[3] = OPTIONAL_PORT_TYPE;
        return portTypes;
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        PortObjectSpec[] outputSpec = new PortObjectSpec[PORT_COUNT];

        // we simply copy the incoming spec to the outgoing
        for (int i = 0; i < PORT_COUNT; i++) {
            outputSpec[i] = inSpecs[i];
        }
        return outputSpec;
    }

    private DataTableSpec createPseudoSpec() {
        DataColumnSpec colSpec = new DataColumnSpecCreator("holder-cells",
                PortObjectHandlerCell.TYPE).createSpec();
        return new DataTableSpec(colSpec);
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) {

        if (!(getLoopStartNode() instanceof LoopStartNodeTerminator)) {
            throw new IllegalStateException("Loop End is not connected"
                    + " to matching/corresponding Loop Start node. You"
                    + " are trying to create an infinite loop!");
        }

        if (!m_loopStarted) {
            // first time we are getting to this: create container
            m_uris = new ArrayList<List<URIContent>>(PORT_COUNT);
            m_bufferedContainers = new BufferedDataContainer[PORT_COUNT];

            for (int i = 0; i < PORT_COUNT; ++i) {
                // create data container
                m_bufferedContainers[i] = exec
                        .createDataContainer(createPseudoSpec());

                // create container to collect the incoming uris
                m_uris.add(new ArrayList<URIContent>());
            }
            m_loopStarted = true;
        }

        for (int i = 0; i < PORT_COUNT; i++) {
            if (inObjects[i] == null) {
                // skip unconnected ports
                continue;
            }

            IURIPortObject po = (IURIPortObject) inObjects[i];

            // some data we need
            int currentIteration = peekFlowVariableInt("currentIteration");

            if (po.getURIContents().size() > 1) {
                LOGGER.warn(String
                        .format("More then one incoming object at port %d. The outgoing port will only hold the first one.",
                                i));
            }
            // register file uri
            m_uris.get(i).add(po.getURIContents().get(0));

            // if we have a filestore port object, add it to the container
            // .. all our filestore port objects are derived from
            // AbstractFileStoreURIPortObject
            if (po instanceof AbstractFileStoreURIPortObject) {
                PortObjectHandlerCell pfsc = new PortObjectHandlerCell(
                        (AbstractFileStoreURIPortObject) po);
                String rowKey = String.format("Row_%d_%d", i, currentIteration);
                m_bufferedContainers[i].addRowToTable(new DefaultRow(rowKey,
                        pfsc));
            }
        }

        // check if this is the last iteration
        if (((LoopStartNodeTerminator) getLoopStartNode()).terminateLoop()) {
            URIPortObject[] portObjects = new URIPortObject[PORT_COUNT];

            for (int i = 0; i < PORT_COUNT; i++) {
                // assign collected uris to new portobject
                portObjects[i] = new URIPortObject(m_uris.get(i));
                // close the container
                m_bufferedContainers[i].close();
            }
            m_loopStarted = false;

            return portObjects;
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
        // ensure we have no running loop
        m_loopStarted = false;
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

    @Override
    public BufferedDataTable[] getInternalTables() {
        /*
         * BufferedDataTable[] tables = new BufferedDataTable[PORT_COUNT]; for
         * (int i = 0; i < PORT_COUNT; ++i) { tables[i] =
         * m_bufferedContainers[i].getTable(); } return tables;
         */
        return null;
    }

    @Override
    public void setInternalTables(BufferedDataTable[] tables) {
        assert tables.length == PORT_COUNT;
    }

}
