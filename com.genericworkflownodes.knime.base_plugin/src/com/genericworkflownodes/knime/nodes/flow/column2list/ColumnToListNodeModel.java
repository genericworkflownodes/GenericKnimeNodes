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

package com.genericworkflownodes.knime.nodes.flow.column2list;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.CollectionCellFactory;
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

/**
 * @author roettig
 * 
 */
public class ColumnToListNodeModel extends NodeModel {

	protected ColumnToListNodeModel() {
		super(1, 1);
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		DataType type = inSpecs[0].iterator().next().getType();
		if (type.isCollectionType()) {
			throw new InvalidSettingsException(
					"collection type at port 0 detected");
		}

		outspec = getOutSpec(type);

		return outspec;
	}

	private DataTableSpec[] outspec;

	private DataTableSpec[] getOutSpec(DataType type) {

		DataTableSpec[] ret = new DataTableSpec[1];
		DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
		allColSpecs[0] = new DataColumnSpecCreator("column 0",
				ListCell.getCollectionType(type)).createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		ret[0] = outputSpec;

		return ret;
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData,
			ExecutionContext exec) throws Exception {
		BufferedDataContainer cont = exec.createDataContainer(outspec[0]);

		List<DataCell> cells = new ArrayList<DataCell>();
		CloseableRowIterator iter = inData[0].iterator();
		while (iter.hasNext()) {
			cells.add(iter.next().getCell(0));
		}

		ListCell lc = CollectionCellFactory.createListCell(cells);
		DataRow row = new DefaultRow("Row 1", lc);
		cont.addRowToTable(row);
		cont.close();

		return new BufferedDataTable[] { cont.getTable() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#loadInternals(java.io.File,
	 * org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void loadInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.NodeModel#loadValidatedSettingsFrom(org.knime.core
	 * .node.NodeSettingsRO)
	 */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO arg0)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#reset()
	 */
	@Override
	protected void reset() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#saveInternals(java.io.File,
	 * org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void saveInternals(File arg0, ExecutionMonitor arg1)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#saveSettingsTo(org.knime.core.node.
	 * NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeModel#validateSettings(org.knime.core.node.
	 * NodeSettingsRO)
	 */
	@Override
	protected void validateSettings(NodeSettingsRO arg0)
			throws InvalidSettingsException {
		// TODO Auto-generated method stub

	}

}
