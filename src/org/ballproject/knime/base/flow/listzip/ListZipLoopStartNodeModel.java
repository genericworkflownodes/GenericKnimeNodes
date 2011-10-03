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

package org.ballproject.knime.base.flow.listzip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.LoopStartNodeTerminator;

public class ListZipLoopStartNodeModel extends NodeModel implements LoopStartNodeTerminator
{
	private int m_iteration;

	
	private static int NinPorts = 4;
	
	/**
	 * Creates a new model.
	 */
	public ListZipLoopStartNodeModel()
	{
		super(createIPOs(), createOPOs());
	}

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(BufferedDataTable.class, true);
	
	private static PortType[] createIPOs()
	{
		PortType[] portTypes = new PortType[NinPorts];
	    Arrays.fill(portTypes, BufferedDataTable.TYPE);
	    portTypes[1] = OPTIONAL_PORT_TYPE;
	    portTypes[2] = OPTIONAL_PORT_TYPE;
	    portTypes[3] = OPTIONAL_PORT_TYPE;
	    return portTypes;
	}
	
	private static PortType[] createOPOs()
	{
		PortType[] portTypes = new PortType[NinPorts];
	    Arrays.fill(portTypes, BufferedDataTable.TYPE);
	    portTypes[1] = OPTIONAL_PORT_TYPE;
	    portTypes[2] = OPTIONAL_PORT_TYPE;
	    portTypes[3] = OPTIONAL_PORT_TYPE;
	    return portTypes;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException
	{
		assert m_iteration == 0;
		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", 0);
		
		List<DataType> dts = new ArrayList<DataType>();
		
		for(int i=0;i<NinPorts;i++)
		{
			if(inSpecs[i]==null)
				break;
			
			DataType type = inSpecs[i].iterator().next().getType();
			if(!type.isCollectionType())
			{
				throw new InvalidSettingsException("no collection type at port "+i); 
			}
			dts.add(type.getCollectionElementType());
		}
		
		outspec = getOutSpec(dts);
		
		return outspec;
	}

	
	
	private DataTableSpec[] outspec;
	
	private int K;
	
	private DataTableSpec[] getOutSpec(List<DataType> types)
	{
		K = types.size();
		DataTableSpec[] ret = new DataTableSpec[NinPorts];
		
		
		for(int i=0;i<NinPorts;i++)
		{
			if(i<K)
			{
				DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
				allColSpecs[0]   =  new DataColumnSpecCreator("column 0",  types.get(i)).createSpec();
				DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
				ret[i] = outputSpec;
			}
			else
			{
				DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
				allColSpecs[0]   =  new DataColumnSpecCreator("column 0",  StringCell.TYPE).createSpec();
				DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
				ret[i] = outputSpec;				
			}
		}
		
		return ret;
	}
	
	private int rowCount=0;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception
	{
		
		ListCell lc = (ListCell) inData[0].iterator().next().getCell(0);
		rowCount = lc.size();

		if (m_iteration == 0)
		{
			assert getLoopEndNode() == null : "1st iteration but end node set";
		} 
		else
		{
			assert getLoopEndNode() != null : "No end node set";
		}

		DataRow row;
		
		BufferedDataContainer[] conts = new BufferedDataContainer[NinPorts];
		BufferedDataTable[]     ret   = new BufferedDataTable[NinPorts];
		
		for(int i=0;i<NinPorts;i++)
		{
			if(i<K)
			{
				conts[i] = exec.createDataContainer(outspec[i]);
				lc = (ListCell) inData[i].iterator().next().getCell(0);
				row = new DefaultRow("Row 1", lc.get(m_iteration));
				conts[i].addRowToTable(row);
				conts[i].close();
				ret[i] = conts[i].getTable();
			}
			else
			{
				// create empty table
				conts[i] = exec.createDataContainer(outspec[i]);
				conts[i].close();
				ret[i] = conts[i].getTable();
			}
		}
		
		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", rowCount);
		m_iteration++;
		
		return ret;
		//return new BufferedDataTable[] { conts[0], cont2.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		m_iteration = 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean terminateLoop()
	{
		boolean continueLoop = (m_iteration!=rowCount);
		return !continueLoop;
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
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException
	{

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException
	{

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException
	{
		// no internals to load
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File nodeInternDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException
	{
		// no internals to save
	}
}
