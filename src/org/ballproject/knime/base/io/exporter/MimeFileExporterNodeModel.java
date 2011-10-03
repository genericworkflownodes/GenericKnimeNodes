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

package org.ballproject.knime.base.io.exporter;

import java.io.File;
import java.io.IOException;
//import java.util.NoSuchElementException;

import org.ballproject.knime.base.mime.MIMEFileCell;
import org.ballproject.knime.base.mime.MIMEFileDelegate;
import org.ballproject.knime.base.port.*;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
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
 * This is the model implementation of MimeFileImporter.
 * 
 * 
 * @author roettig
 */
public class MimeFileExporterNodeModel extends NodeModel /*implements LoopEndNode*/
{

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(MimeFileExporterNodeModel.class);

	static final String CFG_FILENAME = "FILENAME";

	private SettingsModelString  m_filename = MimeFileExporterNodeDialog.createFileChooserModel();
	

	/**
	 * Constructor for the node model.
	 */
	protected MimeFileExporterNodeModel()
	{
		super(1, 0);
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{			
		DataRow  row  = inData[0].iterator().next();
		DataCell cell = row.getCell(0);
		if( cell instanceof MimeMarker)
		{
			MIMEFileCell cell_ = (MIMEFileCell) cell;
			
			MimeMarker mrk = (MimeMarker) cell;
			MIMEFileDelegate del = mrk.getDelegate();
			if(!m_filename.getStringValue().toLowerCase().endsWith(mrk.getExtension().toLowerCase()))
			{
				throw new Exception("invalid extension given for filename. Must be "+mrk.getExtension());
			}
	
			/*
			if(isLooping())
			{
				int iter = getIterationIndex();
				del.write(m_filename.getStringValue()+"."+iter);
				
				boolean terminateLoop =
		                ((LoopStartNodeTerminator)this.getLoopStartNode())
		                        .terminateLoop();
		        if (terminateLoop) 
		        {
		        	//NOP
		        }
		        else
		        {
		        	continueLoop();
		        }
			}
			else
			{
				del.write(m_filename.getStringValue());
			}
			*/
			del.write(m_filename.getStringValue());
		}
		return new BufferedDataTable[]{};
	}

	/*
	private boolean isLooping()
	{
		return (getLoopStartNode()!=null);
	}
	
	private int getIterationIndex()
	{
		int ret = -1;
		try
		{
			ret = peekFlowVariableInt("currentIteration");
		}
		catch(NoSuchElementException e)
		{
			
		}
		return ret;
	}
	*/
	
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
