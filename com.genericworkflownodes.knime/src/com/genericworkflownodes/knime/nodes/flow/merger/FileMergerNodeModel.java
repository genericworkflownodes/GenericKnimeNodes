/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.flow.merger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
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

import com.genericworkflownodes.knime.base.data.port.FileStoreReferenceURIPortObject;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of FileMerger. This nodes takes two files
 * (file lists) as input and outputs a merged list of both inputs.
 *
 * @author aiche
 */
public class FileMergerNodeModel extends NodeModel {

    /*
     * The logger instance. (currently unused)
     */
    // private static final NodeLogger logger = NodeLogger
    // .getLogger(FileMergerNodeModel.class);

    /**
     * Static method that provides the incoming {@link PortType}s.
     *
     * @return The incoming {@link PortType}s of this node.
     */
    private static PortType[] getIncomingPorts() {
        return new PortType[] { IURIPortObject.TYPE, IURIPortObject.TYPE };
    }

    /**
     * Static method that provides the outgoing {@link PortType}s.
     *
     * @return The outgoing {@link PortType}s of this node.
     */
    private static PortType[] getOutgoing() {
        return new PortType[] { IURIPortObject.TYPE };
    }

    /**
     * Constructor for the node model.
     */
    protected FileMergerNodeModel() {
        super(getIncomingPorts(), getOutgoing());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
        // create list of port objects
        List<IURIPortObject> incomingPorts = new ArrayList<IURIPortObject>(2);
        incomingPorts.add((IURIPortObject) inData[0]);
        incomingPorts.add((IURIPortObject) inData[1]);
        // transform to new FileStoreReferenceURIPortObject
        return new PortObject[] { FileStoreReferenceURIPortObject
                .create(incomingPorts) };
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

        // check not null
        checkNotNullSpecs(inSpecs);

        // check equal MIMEType
        checkEqualMIMETypes(inSpecs);

        // create output spec
        URIPortObjectSpec outSpec = new URIPortObjectSpec(
                ((URIPortObjectSpec) inSpecs[0]).getFileExtensions());

        return new PortObjectSpec[] { outSpec };
    }

    /**
     * Checks if all input {@link PortObjectSpec}s are not null.
     *
     * @param inSpecs
     *            The {@link PortObjectSpec}s to check.
     * @throws InvalidSettingsException
     *             Is thrown if one of the {@link PortObjectSpec}s is null.
     */
    private void checkNotNullSpecs(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        for (PortObjectSpec inSpec : inSpecs) {
            if (inSpec == null) {
                throw new InvalidSettingsException(
                        "All ports need to be connected.");
            }
        }
    }

    /**
     * Checks if all input specs have the same {@link MIMEType}.
     *
     * @param inSpecs
     *            The {@link PortObjectSpec}s to check.
     * @throws InvalidSettingsException
     *             Is thrown if the {@link PortObjectSpec}s have different
     *             {@link MIMEType}s.
     */
    private void checkEqualMIMETypes(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        // get mime type from 1st port
        String inSpec0MimeType = MIMETypeHelper
                .getMIMEtypeByExtension(((URIPortObjectSpec) inSpecs[0])
                        .getFileExtensions().get(0)).orElse(null);
        // get mime type from 2nd port
        String inSpec1MimeType = MIMETypeHelper
                .getMIMEtypeByExtension(((URIPortObjectSpec) inSpecs[1])
                        .getFileExtensions().get(0)).orElse(null);

        // compare and indicate mismatch
        if (!inSpec0MimeType.equals(inSpec1MimeType)) {
            throw new InvalidSettingsException(
                    "All incoming ports need to have the same MIMEType");
        }
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
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
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
