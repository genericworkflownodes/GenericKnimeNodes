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
package com.genericworkflownodes.knime.cluster.nodes.porttofilestore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.filestore.FileStore;
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
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.util.FileUtil;

import com.genericworkflownodes.knime.base.data.port.AbstractFileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.FileStorePrefixURIPortObject;
import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.PrefixFileStoreCell;
import com.genericworkflownodes.knime.base.data.port.PrefixURIPortObject;
import com.genericworkflownodes.knime.base.data.port.SerializableFileStoreCell;
import com.genericworkflownodes.knime.base.data.port.SimpleFileStoreCell;

/**
 * This is the model implementation of FileMerger. This nodes takes two files
 * (file lists) as input and outputs a merged list of both inputs.
 * 
 * @author Alexander Fillbrunn
 */
public class PortToFileStoreNodeModel extends NodeModel {

    /**
     * Static method that provides the incoming {@link PortType}s.
     * 
     * @return The incoming {@link PortType}s of this node.
     */
    private static PortType[] getIncomingPorts() {
        return new PortType[] { IURIPortObject.TYPE };
    }

    /**
     * Static method that provides the outgoing {@link PortType}s.
     * 
     * @return The outgoing {@link PortType}s of this node.
     */
    private static PortType[] getOutgoing() {
        return new PortType[] { BufferedDataTable.TYPE };
    }

    /**
     * Constructor for the node model.
     */
    protected PortToFileStoreNodeModel() {
        super(getIncomingPorts(), getOutgoing());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
        
        IURIPortObject input = (IURIPortObject)inData[0];
        
        DataContainer dc = exec.createDataContainer(createSpec());
        /**
         * Files that are not yet managed by KNIME (e.g. when they come from an InputFiles node) come as URIPortObject
         * and must be copied into a FileStore to be handled properly.
         * Other files already have a file store and can be put into a <code>PortObjectHandlerCell</code>.
         */
        if (input instanceof FileStoreURIPortObject) {
            AbstractFileStoreURIPortObject po = (AbstractFileStoreURIPortObject)input;
            SimpleFileStoreCell cell = new SimpleFileStoreCell(po);
            dc.addRowToTable(new DefaultRow(new RowKey("files"), cell));
        } else if (input instanceof FileStorePrefixURIPortObject) {
            FileStorePrefixURIPortObject po = (FileStorePrefixURIPortObject)input;
            PrefixFileStoreCell cell = new PrefixFileStoreCell(po);
            dc.addRowToTable(new DefaultRow(new RowKey("files"), cell));
        } else if (input instanceof PrefixURIPortObject) {
            PrefixURIPortObject po = (PrefixURIPortObject)input;
            FileStore fs = exec.createFileStore("files");
            fs.getFile().mkdirs();
            
            for (URIContent uc : po.getURIContents()) {
                Path localFile = FileUtil.getFileFromURL(uc.getURI().toURL()).toPath();
                String filename = localFile.getFileName().toString();
                if (!filename.endsWith(uc.getExtension())) {
                    filename = filename.concat(".").concat(uc.getExtension());
                }
                Files.copy(localFile, Paths.get(fs.getFile().toURI()).resolve(filename));
                PrefixFileStoreCell cell = new PrefixFileStoreCell(fs, po.getPrefix(), Collections.singletonList(filename));
                dc.addRowToTable(new DefaultRow(new RowKey(filename), cell));
             }
        } else if (input instanceof URIPortObject) {
            URIPortObject po = (URIPortObject)input;
            FileStore fs = exec.createFileStore("files");
            fs.getFile().mkdirs();
            
            for (URIContent uc : po.getURIContents()) {
                Path localFile = FileUtil.getFileFromURL(uc.getURI().toURL()).toPath();
                String filename = localFile.getFileName().toString();
                if (!filename.endsWith(uc.getExtension())) {
                    filename = filename.concat(".").concat(uc.getExtension());
                }
                Files.copy(localFile, Paths.get(fs.getFile().toURI()).resolve(filename));
                SimpleFileStoreCell cell = new SimpleFileStoreCell(fs, Collections.singletonList(filename));
                dc.addRowToTable(new DefaultRow(new RowKey(filename), cell));
             }
        }
        dc.close();
        return new PortObject[] {(BufferedDataTable)dc.getTable()};
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }
    
    private DataTableSpec createSpec() {
        DataColumnSpec spec = new DataColumnSpecCreator("files", SerializableFileStoreCell.TYPE).createSpec();
        return new DataTableSpecCreator()
                .addColumns(spec)
                .createSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        
        return new PortObjectSpec[] {createSpec()};
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
