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

package com.genericworkflownodes.knime.nodes.io.demangler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.url.MIMEType;
import org.knime.core.data.url.URIContent;
import org.knime.core.data.url.port.MIMEURIPortObject;
import org.knime.core.data.url.port.MIMEURIPortObjectSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.GenericNodesPlugin;
import com.genericworkflownodes.knime.mime.IMIMEtypeRegistry;
import com.genericworkflownodes.knime.mime.demangler.IDemangler;
import com.genericworkflownodes.knime.mime.demangler.IDemanglerRegistry;

/**
 * This is the model implementation of DemanglerNodeModel.
 * 
 * 
 * @author roettig
 */
public class DemanglerNodeModel extends NodeModel {

	/**
	 * Settings field where the currently selected demangler is stored.
	 */
	private static final String SELECTED_DEMANGLER_SETTINGNAME = "selected_demangler";

	/**
	 * Settings field where the currently configured {@link MIMEType} is stored.
	 */
	private static final String CONFIGURED_MIMETYPE_SETTINGNAME = "configured_mime_type";

	/**
	 * Ref. to the central {@link IMIMEtypeRegistry}.
	 */
	private IMIMEtypeRegistry resolver = GenericNodesPlugin
			.getMIMEtypeRegistry();

	/**
	 * Ref. to the central {@link IDemanglerRegistry}.
	 */
	private IDemanglerRegistry demanglerRegistry = GenericNodesPlugin
			.getDemanglerRegistry();

	/**
	 * The selected {@link IDemangler}.
	 */
	private IDemangler demangler;

	/**
	 * The currently configured {@link MIMEType}.
	 */
	private MIMEType configuredMIMEType;

	/**
	 * Constructor for the node model.
	 */
	protected DemanglerNodeModel() {
		super(new PortType[] { new PortType(MIMEURIPortObject.class) },
				new PortType[] { new PortType(BufferedDataTable.class) });
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * @Override protected BufferedDataTable[] execute(final BufferedDataTable[]
	 * inData, final ExecutionContext exec) throws Exception {
	 * BufferedDataContainer container = null; DataCell inCell0 =
	 * inData[0].iterator().next().getCell(0);
	 * 
	 * if(inCell0.getType().isCollectionType()) { ListCell lc = (ListCell)
	 * inCell0; int N = lc.size(); container =
	 * exec.createDataContainer(adjustOutSpec(N)); List<Iterator<DataCell>>
	 * iters = new ArrayList<Iterator<DataCell>>(); for(DataCell dc: lc) { if(!
	 * (dc instanceof MIMEFileCell) ) { throw new
	 * Exception("ListCell does not contain MIMEFileCells"); } MIMEFileCell mfc
	 * = (MIMEFileCell) dc; iters.add( demangler.demangle(mfc) ); } fillTable(
	 * iters, container); } else { container =
	 * exec.createDataContainer(outspec); if(! (inCell0 instanceof MIMEFileCell)
	 * ) { throw new Exception("first DataCell is not a MIMEFileCell"); }
	 * List<Iterator<DataCell>> iters = new ArrayList<Iterator<DataCell>>();
	 * MIMEFileCell mfc = (MIMEFileCell) inCell0; iters.add(
	 * demangler.demangle(mfc) ); fillTable( iters, container); }
	 * container.close();
	 * 
	 * BufferedDataTable out = container.getTable();
	 * 
	 * return new BufferedDataTable[]{ out }; }
	 * 
	 * private void fillTable(List<Iterator<DataCell>> iters,
	 * BufferedDataContainer container) { int C = iters.size(); int idx = 1;
	 * while(true) { DataCell[] rowcells = new DataCell[C]; int nDepleted = 0;
	 * for(int i=0;i<C;i++) { if(iters.get(i).hasNext()) { rowcells[i] =
	 * iters.get(i).next(); } else { nDepleted++; rowcells[i] =
	 * DataType.getMissingCell(); } }
	 * 
	 * // all iterators are depleted if(nDepleted==C) { break; }
	 * 
	 * DataRow row = new DefaultRow("Row "+idx, rowcells);
	 * 
	 * container.addRowToTable(row); idx++; } }
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		demangler = null;
		configuredMIMEType = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		settings.addString(CONFIGURED_MIMETYPE_SETTINGNAME,
				configuredMIMEType.getExtension());
		settings.addStringArray(SELECTED_DEMANGLER_SETTINGNAME, demangler
				.getClass().getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		String demanglerClassName = settings
				.getString(SELECTED_DEMANGLER_SETTINGNAME);
		String configuredMIMEExtension = settings
				.getString(CONFIGURED_MIMETYPE_SETTINGNAME);

		// get a list of registered MIMEType
		configuredMIMEType = resolver.getMIMEtype(configuredMIMEExtension);
		List<IDemangler> availableDemangler = demanglerRegistry
				.getDemangler(configuredMIMEType);

		demangler = null;

		for (IDemangler de : availableDemangler) {
			if (demanglerClassName.equals(de.getClass().getName())) {
				demangler = de;
				break;
			}
		}

		if (demangler == null) {
			throw new InvalidSettingsException(
					"Could not find an implementation for the previously selected demangler: "
							+ demanglerClassName);
		}
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
		/*
		 * ObjectInputStream in = new ObjectInputStream(new FileInputStream( new
		 * File(internDir, "demanglers"))); try { demanglers =
		 * (List<IDemangler>) in.readObject(); } catch (ClassNotFoundException
		 * e) { e.printStackTrace(); } in.close();
		 * 
		 * // create the file File f = new File(internDir, "selected_index"); //
		 * load the settings from the file NodeSettingsRO settings =
		 * NodeSettings.loadFromXML(new FileInputStream( f)); // retrieve the
		 * stored values try { idx = settings.getInt("selected_index"); } catch
		 * (InvalidSettingsException e) { e.printStackTrace(); }
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		/*
		 * DataOutputStream out = new DataOutputStream(new FileOutputStream( new
		 * File(internDir, "demanglers")));
		 * 
		 * ObjectOutput oout = new ObjectOutputStream(out);
		 * oout.writeObject(this.demanglers); oout.close();
		 * 
		 * // create a settings object with a config name NodeSettings settings
		 * = new NodeSettings("selected_index"); // store your values under a
		 * certain key settings.addInt("selected_index", idx); // create a file
		 * in the given directory File f = new File(internDir,
		 * "selected_index"); // and save it settings.saveToXML(new
		 * FileOutputStream(f));
		 */
	}

	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		if (!(inSpecs[0] instanceof MIMEURIPortObjectSpec)) {
			throw new InvalidSettingsException(
					"no MIMEURIPortObject compatible port object at port 0");
		}

		MIMEURIPortObjectSpec spec = (MIMEURIPortObjectSpec) inSpecs[0];
		configuredMIMEType = spec.getMIMEType();

		// try to find a demangler for the data type ...
		List<IDemangler> availableDemanglers = demanglerRegistry
				.getDemangler(configuredMIMEType);

		if (availableDemanglers == null) {
			throw new InvalidSettingsException("no IDemangler found for "
					+ configuredMIMEType.toString()
					+ ". Please register one first.");
		}

		// demangler = demanglers.get(idx);

		return new DataTableSpec[] { getDataTableSpec() };
	}

	/**
	 * Retrieves the {@link DataTableSpec} from the selected {@link IDemangler}.
	 * 
	 * @return A configured {@link DataTableSpec}.
	 * @throws InvalidSettingsException
	 *             If the requested configuration can not be created.
	 */
	private DataTableSpec getDataTableSpec() throws InvalidSettingsException {
		return demangler.getTableSpec();
	}

	@Override
	protected BufferedDataTable[] execute(final PortObject[] inObjects,
			final ExecutionContext exec) throws Exception {
		BufferedDataContainer container = exec.createDataContainer(demangler
				.getTableSpec());

		MIMEURIPortObject obj = (MIMEURIPortObject) inObjects[0];
		List<URIContent> uris = obj.getURIContents();
		if (uris.size() == 0) {
			throw new Exception(
					"No URI was supplied in MIMEURIPortObject at input port 0");
		} else if (uris.size() != 1) {
			throw new Exception(String.format(
					"We can only demangle a single file but got %d.",
					uris.size()));
		}

		URI relURI = uris.get(0).getURI();

		Iterator<DataRow> iter = demangler.demangle(relURI);
		while (iter.hasNext()) {
			container.addRowToTable(iter.next());
		}
		BufferedDataTable out = container.getTable();

		return new BufferedDataTable[] { out };
	}
}
