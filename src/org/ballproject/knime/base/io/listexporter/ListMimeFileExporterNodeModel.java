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

package org.ballproject.knime.base.io.listexporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
//import java.util.NoSuchElementException;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.ballproject.knime.base.port.*;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowIterator;
import org.knime.core.data.collection.ListCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortType;
//import org.knime.core.node.workflow.LoopEndNode;
//import org.knime.core.node.workflow.LoopStartNodeTerminator;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of ListMimeFileImporter.
 * 
 * 
 * @author roettig
 */
public class ListMimeFileExporterNodeModel extends NodeModel /*implements LoopEndNode*/
{

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(ListMimeFileExporterNodeModel.class);

	static final String CFG_FILENAME = "FILENAME";

	private SettingsModelString  m_filename = ListMimeFileExporterNodeDialog.createFileChooserModel();
	

	/**
	 * Constructor for the node model.
	 */
	protected ListMimeFileExporterNodeModel()
	{
		super(createIPOs(), new PortType[]{});
	}
	
	public static final PortType OPTIONAL_PORT_TYPE = new PortType(BufferedDataTable.class, true);
	
	private static PortType[] createIPOs()
	{
		PortType[] portTypes = new PortType[2];
	    Arrays.fill(portTypes, OPTIONAL_PORT_TYPE);
	    return portTypes;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{	
		if(inData[0]==null&&inData[1]==null)
		{
			throw new Exception("no input port is connected");
		}
		
		List<MIMEFileCell> files = new ArrayList<MIMEFileCell>();
		
		if(inData[0]!=null)
		{
			ListCell lc = (ListCell) inData[0].iterator().next().getCell(0);
			for(DataCell dc: lc)
			{
				files.add((MIMEFileCell) dc);
			}
		}
		else
		{
			RowIterator iter = inData[1].iterator();
			while(iter.hasNext())
			{
				DataRow row = iter.next();
				files.add((MIMEFileCell) row.getCell(0));
			}
		}
		
		int idx = 1;
		for(MIMEFileCell mfc: files)
		{
			if(!m_filename.getStringValue().toLowerCase().endsWith(mfc.getExtension().toLowerCase()))
			{
				throw new Exception("invalid extension given for filename. Must be "+mfc.getExtension());
			}
	
			mfc.write(insertIndex(m_filename.getStringValue(),mfc.getExtension(),idx));
			idx++;
		}
		return new BufferedDataTable[]{};
	}

	private static String insertIndex(String filename, String extension, int idx)
	{
		if(filename.equals("")||filename.length()==0)
			return filename;
		
		String filename_ = filename.toLowerCase();
		String ext       = extension.toLowerCase();
		
		int idx1  = filename_.lastIndexOf(ext);
		
		if(idx==-1)
			return filename;
		
		String s1 = filename.substring(0, idx1);
		return s1+idx+"."+extension;
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
		/*
		if(inSpecs[0]==null&&inSpecs[1]==null)
		{
			throw new InvalidSettingsException("no input port connected");
		}
		*/
		if(inSpecs[0]!=null&&inSpecs[1]!=null)
		{
			throw new InvalidSettingsException("only one input port may be connected");
		}
		return new DataTableSpec[]{};
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
		m_filename.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		m_filename.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{	
	}
	
	
}
