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

package org.ballproject.knime.base.io.listimporter;

import java.io.File;
import java.io.IOException;

import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEFileCell;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;

/**
 * This is the model implementation of ListMimeFileImporter.
 * 
 * 
 * @author roettig
 */
public class ListMimeFileImporterNodeModel extends NodeModel
{

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(ListMimeFileImporterNodeModel.class);

	static final String CFG_FILENAME = "FILENAME";

	private SettingsModelStringArray  m_filename = ListMimeFileImporterNodeDialog.createFileChooserModel();
	

	/**
	 * Constructor for the node model.
	 */
	protected ListMimeFileImporterNodeModel()
	{
		super(0, 1);
	}
	
	public byte[] data = new byte[]{};
	
	protected MIMEtypeRegistry resolver = GenericNodesPlugin.getMIMEtypeRegistry();
	protected MIMEFileCell cell;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{
		
		List<MIMEFileCell> files = new ArrayList<MIMEFileCell>();
		
		String[] filenames = m_filename.getStringArrayValue();
		for(String filename: filenames)
		{
			File f = new File(filename);
			cell = resolver.getCell(filename);
			cell.read(f);
			files.add(cell);
		}
			
		// FixME
		//data = cell.getData();
		
		ListCell lc = CollectionCellFactory.createListCell(files);
		
		BufferedDataContainer container = exec.createDataContainer(outspec);
		
		DataRow row = new DefaultRow("Row 0", lc);
		container.addRowToTable(row);
		
		container.close();
		
		BufferedDataTable out = container.getTable();
		
		return new BufferedDataTable[]{ out };
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException
	{	
		if(this.m_filename.getStringArrayValue().length==0)
			return new DataTableSpec[]{null};
			
		
		for(String filename: this.m_filename.getStringArrayValue())
		{
			cell = resolver.getCell(filename);
			if(cell==null)
				throw new InvalidSettingsException("could not resolve MIME type of file");	
		}
		
		return new DataTableSpec[]{ getDataTableSpec() };
	}

	private DataTableSpec outspec;
	
	private DataTableSpec getDataTableSpec() throws InvalidSettingsException
	{
        DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
        
        DataType type = ListCell.getCollectionType(cell.getDataType());
        
		allColSpecs[0] =  new DataColumnSpecCreator("MIMEFILE", type ).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

        // save this internally
        outspec = outputSpec;
        
        return outputSpec;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
	{
		m_filename.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.
		m_filename.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		m_filename.validateSettings(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
		ZipFile zip = new ZipFile(new File(internDir,"loadeddata"));
		
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

		int    BUFFSIZE = 2048;
		byte[] BUFFER   = new byte[BUFFSIZE];
		
	    while(entries.hasMoreElements()) 
	    {
	        ZipEntry entry = (ZipEntry)entries.nextElement();
	        if(entry.getName().equals("rawdata.bin"))
	        {
	        	int  size = (int) entry.getSize(); 
	        	data = new byte[size];
	        	InputStream in = zip.getInputStream(entry);
	        	int len;
	        	int totlen=0;
	        	while( (len=in.read(BUFFER, 0, BUFFSIZE))>=0 )
	        	{
	        		System.arraycopy(BUFFER, 0, data, totlen, len);
	        		totlen+=len;
	        	}
	        }
	    }
	    zip.close();
	    
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(internDir,"loadeddata")));
		ZipEntry entry = new ZipEntry("rawdata.bin");
	    out.putNextEntry(entry);
	    out.write(data);
	    out.close(); 
	}

}
