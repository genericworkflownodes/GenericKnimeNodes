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

package org.ballproject.knime.base.io.mangler;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


import org.ballproject.knime.GenericNodesPlugin;
import org.ballproject.knime.base.mime.MIMEFileCell;
import org.ballproject.knime.base.mime.MIMEtypeRegistry;
import org.ballproject.knime.base.mime.demangler.Demangler;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;



/**
 * This is the model implementation of ManglerNodeModel.
 * 
 * 
 * @author roettig
 */
public class ManglerNodeModel extends NodeModel
{

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(ManglerNodeModel.class);
	
	protected Demangler        demangler;
	protected MIMEtypeRegistry resolver = GenericNodesPlugin.getMIMEtypeRegistry();
	
	/**
	 * Constructor for the node model.
	 */
	protected ManglerNodeModel()
	{
		super(1, 1);
	}
	
	private static class Adapter implements Iterator<DataCell>
	{
		private RowIterator rowiter;
		private int idx;
		
		public Adapter(RowIterator rowiter, int idx)
		{
			this.rowiter = rowiter;
			this.idx     = idx;
		}

		@Override
		public boolean hasNext()
		{
			return rowiter.hasNext();
		}

		@Override
		public DataCell next()
		{
			return rowiter.next().getCell(idx);
		}

		@Override
		public void remove()
		{
			// NOP
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{
		BufferedDataContainer container = exec.createDataContainer(outspec);
		
		DataCell inCell0 = inData[0].iterator().next().getCell(0);
		
		
		Adapter      iter = new Adapter(inData[0].iterator(),0);
		MIMEFileCell cell = demangler.mangle(iter);
		
		DataRow row = new DefaultRow("Row 0", cell);
		container.addRowToTable(row);
		
		container.close();
		inData[0].iterator().close();
		
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
	
	
	protected DataType inType;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException
	{	
		// get data type of first column (where the MIMEFileCell is stored by convention)
		inType = inSpecs[0].getColumnSpec(0).getType();
		
		// try to find a demangler for the data type ... 
		List<Demangler> demanglers = resolver.getMangler(inType); 
		
		
		if(demanglers.size()==0)
		{
			throw new InvalidSettingsException("no Mangler found for "+inType.toString()+". Please register one first.");
		}
		
		// we support only one mangler (the first one)
		demangler = demanglers.get(0);
		
		return new DataTableSpec[]{ getDataTableSpec() };
	}

	private DataTableSpec outspec;
	
	private DataTableSpec getDataTableSpec() throws InvalidSettingsException
	{
		DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
        
        DataType      dt =  demangler.getSourceType();
		allColSpecs[0]   =  new DataColumnSpecCreator("column 0",  dt).createSpec();
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{
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
