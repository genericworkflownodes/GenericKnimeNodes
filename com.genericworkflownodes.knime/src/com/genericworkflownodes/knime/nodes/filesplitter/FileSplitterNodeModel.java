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
package com.genericworkflownodes.knime.nodes.filesplitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.knime.base.filehandling.mime.MIMETypeEntry;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.filestore.FileStoreCell;
import org.knime.core.data.filestore.FileStoreFactory;
import org.knime.core.data.filestore.FileStoreUtil;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.PortObjectHandlerCell;
import com.genericworkflownodes.knime.filesplitter.Splitter;
import com.genericworkflownodes.knime.filesplitter.SplitterFactory;
import com.genericworkflownodes.knime.filesplitter.SplitterFactoryManager;
import com.genericworkflownodes.knime.filesplitter.impl.LineSplitter;
import com.genericworkflownodes.knime.filesplitter.impl.LineSplitterFactory;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of FileMerger. This nodes takes two files
 * (file lists) as input and outputs a merged list of both inputs.
 * 
 * @author aiche
 */
public class FileSplitterNodeModel extends NodeModel {

    /*
     * The logger instance. (currently unused)
     */
    // private static final NodeLogger logger = NodeLogger
    // .getLogger(FileMergerNodeModel.class);
    
    private static final String FACTORY_ID_KEY = "factoryID";

    private static final String NUM_PARTS_KEY = "numParts";
    
    public static SettingsModelString createFactoryIDSettingsModel() {
        return new SettingsModelString(FACTORY_ID_KEY, null);
    }
    
    public static SettingsModelInteger createNumPartsSettingsModel() {
        return new SettingsModelInteger(NUM_PARTS_KEY, 2);
    }
    
    private SettingsModelString m_factoryID = createFactoryIDSettingsModel();
    private Splitter m_splitter;
    private SettingsModelInteger m_numParts = createNumPartsSettingsModel();

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
    protected FileSplitterNodeModel() {
        super(getIncomingPorts(), getOutgoing());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
        IURIPortObject input = (IURIPortObject) inData[0];
        if (input.getURIContents().size() > 1) {
            throw new InvalidSettingsException("This node can only split a single file");
        }
        
        String factoryID = m_factoryID.getStringValue();
        if (factoryID == null) {
            String ext = input.getURIContents().get(0).getExtension();
            String mime = MIMETypeHelper.getMIMEtypeByExtension(ext);
            for (SplitterFactory fac : SplitterFactoryManager.getInstance().getFactories()) {
                if (fac.isApplicable(mime)) {
                    factoryID = fac.getID();
                    m_factoryID.setStringValue(factoryID);
                    m_splitter = fac.createSplitter();
                    break;
                }
            }
        }
                
        File f = new File(input.getURIContents().get(0).getURI().toURL().getFile());

        FileStore fs = exec.createFileStore("test");

        File[] outputs = new File[m_numParts.getIntValue()];
        for (int i = 0; i < m_numParts.getIntValue(); i++) {
            int idx = f.getPath().lastIndexOf('.');
            String ext;
            String name;
            if (idx == -1) {
                ext = "";
                name = f.getName();
            } else {
                ext = f.getPath().substring(idx);
                name = f.getName().substring(0, f.getName().lastIndexOf('.'));
            }
            //filestores[i] = exec.createFileStore(name + i + ext);
            outputs[i] = Paths.get(fs.getFile().toString()).resolve(name + i + ext).toFile();
            outputs[i].getParentFile().mkdirs();
        }
        
        m_splitter.split(f, outputs);
        DataContainer dc = exec.createDataContainer(createSpec());

        for (int i = 0; i < m_numParts.getIntValue(); i++) {
            FileStoreURIPortObject po = new FileStoreURIPortObject(fs);
            String relPath = Paths.get(fs.getFile().toString())
                                .relativize(Paths.get(outputs[i].getAbsolutePath()))
                                .toString();
            po.registerFile(relPath);
            PortObjectHandlerCell cell = new PortObjectHandlerCell(po);
            
            dc.addRowToTable(new DefaultRow(new RowKey("Row" + i), cell));
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
        DataTableSpecCreator specCreator = new DataTableSpecCreator();
        specCreator.addColumns(
                new DataColumnSpecCreator("files",
                                            PortObjectHandlerCell.TYPE)
                .createSpec());
        return specCreator.createSpec();
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
        m_factoryID.saveSettingsTo(settings);
        if (m_splitter != null) {
            m_splitter.saveSettingsTo(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_factoryID.loadSettingsFrom(settings);
        m_splitter = SplitterFactoryManager.getInstance()
                        .getFactory(m_factoryID.getStringValue())
                        .createSplitter();
        m_splitter.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_factoryID.validateSettings(settings);
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
