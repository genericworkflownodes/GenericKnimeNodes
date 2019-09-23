/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.nodes.io.dirloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
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

import com.genericworkflownodes.knime.base.data.port.IPrefixURIPortObject;
import com.genericworkflownodes.knime.base.data.port.PrefixURIPortObject;

/**
 * This is the model implementation of DirectoryLoader.
 * 
 * @author roettig, aiche, neubert
 */
public class DirectoryLoaderNodeModel extends NodeModel {

    /**
     * ID for the directoryname configuration.
     */
    static final String CFG_DIRECTORYNAME = "DIRECTORYNAME";

    /**
     * Model containing the file names and optional extension.
     */
    private SettingsModelString m_directory_name = new SettingsModelString(
            DirectoryLoaderNodeModel.CFG_DIRECTORYNAME, new String());


    /**
     * Constructor for the node model.
     */
    protected DirectoryLoaderNodeModel() {
        super(new PortType[] {}, new PortType[] { IURIPortObject.TYPE });
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_directory_name.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
        throws InvalidSettingsException {
    	m_directory_name.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
        throws InvalidSettingsException {

        SettingsModelString tmp_directoryname = m_directory_name
                .createCloneWithValidatedValue(settings);

        if (tmp_directoryname == null
                || tmp_directoryname.equals("")) {
            throw new InvalidSettingsException("No Files selected.");
        }


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

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
        throws InvalidSettingsException {

        /*
         * Upon inserting the node into a workflow, it gets configured, so at
         * least something fundamental like the file name should be checked
         */
        String directoryname = m_directory_name.getStringValue();
        if (directoryname == null || directoryname.equals("")) {
            throw new InvalidSettingsException("No Directory selected.");
        }

        URIPortObjectSpec uri_spec = null;
        uri_spec = new URIPortObjectSpec("");

        return new PortObjectSpec[] { uri_spec };
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
            throws Exception {
    	      
        String directoryname = m_directory_name.getStringValue();
        List<URIContent> uris = new ArrayList<URIContent>();
        File in = new File(convertToURL(directoryname).toURI());
            
        if (!in.canRead()) {
            throw new Exception("Cannot read from input file: "
                  + in.getAbsolutePath());
        }

        String prefix = directoryname;
        if (directoryname.contains(".")) {
            prefix = prefix.split("\\.",2)[0];
        }  
  
        List<File> files_list_ext = listf(directoryname);
            
        if (files_list_ext.isEmpty()) {
            throw new Exception("Could not find any files in the selected directory");
        }
        for (File file : files_list_ext) {
            uris.add(new URIContent(file.toURI(), FilenameUtils.getExtension(file.toString())));
        }
        
     	IPrefixURIPortObject uri_prefix_object = null;
     	uri_prefix_object = new PrefixURIPortObject(uris, prefix);
     	
     	return new PortObject[] { (URIPortObject) uri_prefix_object };     	     	
    }

    public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }

        return resultList;
    } 
    
    /**
     * Extract a URL from the given String, trying different conversion
     * approaches. Inspired by CSVReaderConfig#loadSettingsInModel().
     * 
     * @param urlS
     *            The string containing the URL.
     * @return A URL object.
     * @throws InvalidSettingsException
     *             If the given string cannot be converted properly.
     */
    private URL convertToURL(String urlS) throws InvalidSettingsException {
        URL url;

        if (urlS == null) {
            throw new InvalidSettingsException("URL must not be null");
        }
        try {
            url = new URL(urlS);
        } catch (MalformedURLException e) {
            // might be a file, bug fix 3477
            File file = new File(urlS);
            try {
                url = file.toURI().toURL();
            } catch (Exception fileURLEx) {
                throw new InvalidSettingsException("Invalid URL: "
                        + e.getMessage(), e);
            }
        }

        return url;
    }
}
