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

package com.genericworkflownodes.knime.nodes.flow.columnmerger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ballproject.knime.base.mime.MIMEFileCell;
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
import org.knime.core.node.port.PortType;

/**
 * This is the model implementation of DemanglerNodeModel.
 * 
 * 
 * @author roettig
 */
public class ColumnMergerNodeModel extends NodeModel {

	/**
	 * Constructor for the node model.
	 */
	protected ColumnMergerNodeModel() {
		super(createOPOs(), new PortType[] { BufferedDataTable.TYPE,
				BufferedDataTable.TYPE });
	}

	public static final PortType OPTIONAL_PORT_TYPE = new PortType(
			BufferedDataTable.class, true);

	private static PortType[] createOPOs() {
		PortType[] portTypes = new PortType[4];
		Arrays.fill(portTypes, BufferedDataTable.TYPE);
		portTypes[1] = OPTIONAL_PORT_TYPE;
		portTypes[2] = OPTIONAL_PORT_TYPE;
		portTypes[3] = OPTIONAL_PORT_TYPE;
		return portTypes;
	}

	private int rowIdx = 1;

	private void fill(DataCell cell, BufferedDataContainer cont,
			List<MIMEFileCell> list) {
		if (cell.getType().isCollectionType()) {
			ListCell lc = (ListCell) cell;
			for (DataCell dc : lc) {
				fill(dc, cont, list);
			}
		} else {
			DataRow row = new DefaultRow("Row " + rowIdx, cell);
			cont.addRowToTable(row);
			rowIdx++;
			list.add((MIMEFileCell) cell);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {
		BufferedDataContainer container1 = exec.createDataContainer(outspec1);
		BufferedDataContainer container2 = exec.createDataContainer(outspec2);

		List<MIMEFileCell> mfcells = new ArrayList<MIMEFileCell>();

		rowIdx = 1;

		DataCell port_cell = null;

		// first port must be set
		port_cell = inData[0].iterator().next().getCell(0);
		fill(port_cell, container2, mfcells);

		// port_cell = inData[1].iterator().next().getCell(0);
		// fill(port_cell, container2, mfcells);

		// other ports are optional
		if (inData[1] != null) {
			port_cell = inData[1].iterator().next().getCell(0);
			fill(port_cell, container2, mfcells);
		}

		if (inData[2] != null) {
			port_cell = inData[2].iterator().next().getCell(0);
			fill(port_cell, container2, mfcells);
		}

		if (inData[3] != null) {
			CloseableRowIterator iter = inData[3].iterator();

			while (iter.hasNext()) {
				port_cell = iter.next().getCell(0);
				fill(port_cell, container2, mfcells);
			}
			iter.close();
		}

		ListCell lc = CollectionCellFactory.createListCell(mfcells);
		DataRow row = new DefaultRow("Row 1", lc);
		container1.addRowToTable(row);

		container1.close();
		container2.close();

		BufferedDataTable out1 = container1.getTable();
		BufferedDataTable out2 = container2.getTable();

		return new BufferedDataTable[] { out1, out2 };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	protected List<DataType> inTypes;
	protected DataType inType;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		// get data type of first columns (where the MIMEFileCell is stored by
		// convention)
		DataType mfct = DataType.getType(MIMEFileCell.class);

		int N = 3;

		inTypes = new ArrayList<DataType>();
		for (int i = 0; i < N; i++) {
			if (inSpecs[i] == null) {
				continue;
			}

			DataType inType_ = inSpecs[i].getColumnSpec(0).getType();

			if (inType_.isCollectionType()) {
				inType_ = inType_.getCollectionElementType();
			}

			if (!(mfct.isASuperTypeOf(inType_))) {
				throw new InvalidSettingsException(
						"at least one non-MIMEFileCells was supplied at input ports");
			}
			inTypes.add(inType_);
		}

		inType = inTypes.get(0);
		for (DataType dt : inTypes) {
			if (dt != inType) {
				throw new InvalidSettingsException(
						"all mimetypes supplied at input ports must be equal");
			}
		}

		return new DataTableSpec[] { getDataTableSpec(true),
				getDataTableSpec(false) };
	}

	private DataTableSpec outspec1;
	private DataTableSpec outspec2;

	private DataTableSpec getDataTableSpec(boolean collection)
			throws InvalidSettingsException {
		DataColumnSpec[] allColSpecs = new DataColumnSpec[1];

		if (collection) {
			allColSpecs[0] = new DataColumnSpecCreator("column 0",
					ListCell.getCollectionType(inType)).createSpec();
		} else {
			allColSpecs[0] = new DataColumnSpecCreator("column 0", inType)
					.createSpec();
		}

		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

		// save this internally
		if (collection) {
			outspec1 = outputSpec;
		} else {
			outspec2 = outputSpec;
		}

		return outputSpec;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
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
