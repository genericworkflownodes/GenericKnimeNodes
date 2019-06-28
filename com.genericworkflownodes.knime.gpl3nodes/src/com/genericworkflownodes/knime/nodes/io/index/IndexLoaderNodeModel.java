/**
 * Copyright (c) by GKN team
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
package com.genericworkflownodes.knime.nodes.io.index;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.knime.core.data.uri.IURIPortObject;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

import com.genericworkflownodes.knime.base.data.port.PrefixURIPortObject;


/**
 * This is the model implementation of IndexLoader.
 * 
 *
 * @author Kerstin Neubert, FU Berlin
 */
public class IndexLoaderNodeModel extends NodeModel { 
	
    /**
	 * Config name for the file name.
	 */
	static final String CFGKEY_FILENAME = "FILENAME";
	
	/**
     * Config name for the index type.
     */
    static final String CFGKEY_INDEXTYPE = "";

    /**
     * Initial default index type
     */
    static final String DEFAULT_INDEXTYPE = "unknown";

	/**
	 * SettingsModel for the filename
	 */
	private SettingsModelString m_filename = createSettingsModelFileSelection();
		
	protected static String[] available_index_types = IndexTypeHelper.getAllIndexTypes();
	
	protected static String[] available_index_extensions = IndexTypeHelper.getRepresentativeExtensions();
	
	
	static SettingsModelString createSettingsModelFileSelection() {
	    return new SettingsModelString(IndexLoaderNodeModel.CFGKEY_FILENAME, "");
	}	
	
	static SettingsModelString createSettingsModelSelection() {
	    return new SettingsModelString(IndexLoaderNodeModel.CFGKEY_INDEXTYPE, IndexLoaderNodeModel.DEFAULT_INDEXTYPE);
	}
	
    /**
     * Constructor for the node model.
     */
    protected IndexLoaderNodeModel() {
    
        // TODO: Specify the amount of input and output ports needed.
        super(new PortType[] { }, 
        	  new PortType[] { PortTypeRegistry.getInstance().getPortType(IURIPortObject.class) });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
   
        File file = new File(m_filename.getStringValue());
        URIPortObjectSpec uri_spec = null;
        
        if (file.exists()) {
            String[] m_fileExtensions = get_fileExtensions(file.getName());
            uri_spec = new URIPortObjectSpec(m_fileExtensions);
        }
        else {
            throw new InvalidSettingsException("No file selected.");
        }
        
        return new PortObjectSpec[]{ uri_spec };
        
    }


    private String[] get_fileExtensions(String filename) throws InvalidSettingsException {
        
        String index_type = IndexTypeHelper.getIndextype(filename);
        String[] file_extensions = IndexTypeHelper.getExtensionsByIndexType(index_type);
        if (file_extensions.length == 0) {
            throw new InvalidSettingsException("File " + filename + " has an unregistered extension.");
        }
        
        return file_extensions;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {

        File file = new File(m_filename.getStringValue());
        String fileName = m_filename.getStringValue();
        String[] m_fileExtensions = get_fileExtensions(fileName);
               
        // file prefix is the prefix in front of the first "." in a file name 
        String prefix = fileName;
        String filename_prefix = file.getName().toString();
        if (m_filename.getStringValue().contains(".")) {
            prefix = prefix.split("\\.",2)[0];
            filename_prefix = filename_prefix.split("\\.",2)[0];
        }
    
    	File dir = new File(file.getParentFile().toString());
        Path file_path = Paths.get(m_filename.getStringValue());
    	if (!dir.exists() || !dir.isDirectory()) {
    	    throw new RuntimeException(file_path.toString() + " does not exist or is not a directory.");
    	}

        List<URIContent> uris = new ArrayList<URIContent>();

        for (int i = 0; i < m_fileExtensions.length; i++) {
            String wildCard = filename_prefix + ".*" + m_fileExtensions[i];
            File[] ffiles = dir.listFiles((FileFilter)new WildcardFileFilter(wildCard));

            for (File index_file : ffiles) {
                uris.add(new URIContent(index_file.toURI(), m_fileExtensions[i]));
            }
        }
        
        if (uris.isEmpty()) {
            throw new Exception("Could not read index files for file " + prefix);
        }
        
     	PrefixURIPortObject uri_prefix_object = null;
     	uri_prefix_object = new PrefixURIPortObject(uris, prefix);
  
    	return new PortObject[] { (URIPortObject) uri_prefix_object };

    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         m_filename.saveSettingsTo(settings);
    	 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        m_filename.loadSettingsFrom(settings);
  
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        SettingsModelString tmp_filename = m_filename.createCloneWithValidatedValue(settings);
        File file = new File(tmp_filename.getStringValue());
        
        // check, if file has a known index type
        if (tmp_filename.getStringValue() != null && !tmp_filename.getStringValue().isEmpty()) {
            if (!file.exists() ) {
                throw new InvalidSettingsException("File not found " + tmp_filename.getStringValue());
            }
            String index_type = IndexTypeHelper.getIndextype(file.getName());
            if (index_type == null) {
                throw new InvalidSettingsException(
                    "Index type of the selected file is unknown.");
            }
        }
        
    	m_filename.validateSettings(settings);    	

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

    
}

