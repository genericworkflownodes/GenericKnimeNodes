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
package com.genericworkflownodes.knime.cluster.nodes.splittabletoport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.base.data.port.AbstractFileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.FileStoreValue;
import com.genericworkflownodes.knime.base.data.port.SerializableFileStoreCell;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of FileMerger. This nodes takes two files
 * (file lists) as input and outputs a merged list of both inputs.
 *
 * @author Alexander Fillbrunn
 */
public class SplitTableToPortNodeModel extends NodeModel {

    /**
     * Static method that provides the incoming {@link PortType}s.
     *
     * @return The incoming {@link PortType}s of this node.
     */
    private static PortType[] getIncomingPorts() {
        return new PortType[] { BufferedDataTable.TYPE };
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
    protected SplitTableToPortNodeModel() {
        super(getIncomingPorts(), getOutgoing());
    }

    /**
     * Creates a settings model for the column name setting.
     * @return a settings model for the column name, default value <code>null</code>
     */
    public static SettingsModelColumnName createFileColumnSettingsModel() {
        return new SettingsModelColumnName("fileColumn", null);
    }

    private SettingsModelColumnName m_fileCol = createFileColumnSettingsModel();

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {

        BufferedDataTable input = (BufferedDataTable)inData[0];
        int index = input.getDataTableSpec().findColumnIndex(m_fileCol.getColumnName());

        // For multiple rows, we need to collect all the URIContents and put them in a single port object.
        // Otherwise we retrieve the port object from the only cell we have.
        if (input.size() > 1) {
            String mimetype = null;
            List<URIContent> contents = new ArrayList<>();

            for (DataRow row : input) {
                SerializableFileStoreCell cell = (SerializableFileStoreCell)row.getCell(index);
                AbstractFileStoreURIPortObject po = cell.getPortObject();

                List<URIContent> uriContents = po.getURIContents();
                for (URIContent uc : uriContents) {
                    String mt = MIMETypeHelper.getMIMEtypeByExtension(uc.getExtension()).orElse(uc.getExtension());
                    if (mimetype == null) {
                        mimetype = mt;
                    } else if (!mt.equals(mimetype)) {
                        throw new InvalidSettingsException("File ports do not support mixed mimetypes, yet.");
                    }
                    contents.add(uc);
                }
            }
            return new PortObject[] {new URIPortObject(contents)};
        } else {
            DataRow row = input.iterator().next();
            SerializableFileStoreCell cell = (SerializableFileStoreCell)row.getCell(index);
            return new PortObject[] {cell.getPortObject()};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    private PortObjectSpec createSpec() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        DataTableSpec spec = (DataTableSpec)inSpecs[0];
        // If the user has not selected a column yet, we use one that fits
        if (m_fileCol.getColumnName() == null) {
            for (int i = 0; i < spec.getNumColumns(); i++) {
                DataColumnSpec colSpec = spec.getColumnSpec(i);
                if (colSpec.getType().isCompatible(FileStoreValue.class)) {
                    m_fileCol.setStringValue(colSpec.getName());
                    setWarningMessage("No column with file cells configured. Using \"" + m_fileCol.getColumnName() + "\".");
                    break;
                }
            }
        } else {
            // Check that the configured column still exists
            int index = spec.findColumnIndex(m_fileCol.getColumnName());
            if (index == -1) {
                throw new InvalidSettingsException("Column \""+  m_fileCol.getColumnName() + "\" does not exist in the input table. "
                        + "Please reconfigure the node.");
            }
        }

        return new PortObjectSpec[] {createSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_fileCol.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_fileCol.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_fileCol.loadSettingsFrom(settings);
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
