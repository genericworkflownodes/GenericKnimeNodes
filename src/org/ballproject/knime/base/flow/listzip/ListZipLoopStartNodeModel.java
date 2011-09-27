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
import java.util.List;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.workflow.LoopStartNodeTerminator;

/**
 * Loop start node that outputs a set of rows at a time. Used to implement a
 * streaming (or chunking approach) where only a set of rows is processed at a
 * time
 * 
 * @author Bernd Wiswedel, KNIME.com, Zurich, Switzerland
 */
public class ListZipLoopStartNodeModel extends NodeModel implements
		LoopStartNodeTerminator
{

	// loop invariants
	private BufferedDataTable m_table;
	private CloseableRowIterator m_iterator;

	// loop variants
	private int m_iteration;

	
	private static int NinPorts = 2;
	
	/**
	 * Creates a new model.
	 */
	public ListZipLoopStartNodeModel()
	{
		super(NinPorts, NinPorts);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException
	{
		System.out.println("configure called ");
		assert m_iteration == 0;
		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", 0);
		
		List<DataType> dts = new ArrayList<DataType>();
		
		for(int i=0;i<NinPorts;i++)
		{
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
	
	
	
	private DataTableSpec[] getOutSpec(List<DataType> types)
	{
		int K = types.size();
		DataTableSpec[] ret = new DataTableSpec[NinPorts];
		
		
		for(int i=0;i<NinPorts;i++)
		{
			DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
			allColSpecs[0]   =  new DataColumnSpecCreator("column 0",  types.get(i)).createSpec();
			DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
			ret[i] = outputSpec;
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
		System.out.println("execute called " + m_iteration);
		
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
		
		BufferedDataContainer cont1 = exec.createDataContainer(outspec[0]);
		lc = (ListCell) inData[0].iterator().next().getCell(0);
		row = new DefaultRow("Row 1", lc.get(m_iteration));
		cont1.addRowToTable(row);
		
		BufferedDataContainer cont2 = exec.createDataContainer(outspec[1]);
		lc = (ListCell) inData[1].iterator().next().getCell(0);
		row = new DefaultRow("Row 1", lc.get(m_iteration));
		cont2.addRowToTable(row);
	
		cont1.close();
		cont2.close();
		
		pushFlowVariableInt("currentIteration", m_iteration);
		pushFlowVariableInt("maxIterations", rowCount);
		m_iteration++;
		
		return new BufferedDataTable[] { cont1.getTable(), cont2.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		System.out.println("reset called");
		m_iteration = 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean terminateLoop()
	{
		boolean continueLoop = (m_iteration!=rowCount);
		System.out.println("terminateLoop called " + !continueLoop);
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
