/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.mangler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ballproject.knime.base.util.FileStash;
import org.eclipse.ui.PlatformUI;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.url.MIMEType;
import org.knime.core.data.url.URIContent;
import org.knime.core.data.url.port.MIMEURIPortObject;
import org.knime.core.data.url.port.MIMEURIPortObjectSpec;
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

import com.genericworkflownodes.knime.mime.demangler.IDemangler;
import com.genericworkflownodes.knime.mime.demangler.IDemanglerRegistry;

/**
 * This is the model implementation of ManglerNodeModel.
 * 
 * @author roettig, aiche
 */
public class ManglerNodeModel extends NodeModel {

	/**
	 * Settings field where the currently selected demangler is stored.
	 */
	static final String SELECTED_DEMANGLER_SETTINGNAME = "selected_demangler";

	/**
	 * Settings field where the currently configured {@link MIMEType} is stored.
	 */
	static final String AVAILABLE_MIMETYPE_SETTINGNAME = "available_demangler";

	/**
	 * The selected {@link IDemangler}.
	 */
	private IDemangler demangler;

	/**
	 * Available {@link IDemangler}.
	 */
	private List<IDemangler> availableMangler;

	/**
	 * The currently active inputTalbeSpecification.
	 */
	private DataTableSpec inputTalbeSpecification;

	/**
	 * Constructor for the node model.
	 */
	protected ManglerNodeModel() {
		super(new PortType[] { new PortType(BufferedDataTable.class) },
				new PortType[] { new PortType(MIMEURIPortObject.class) });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inData,
			final ExecutionContext exec) throws Exception {

		// translate portobject to table

		BufferedDataTable table = (BufferedDataTable) inData[0];

		// create a file where we can write to
		String filename = FileStash.getInstance().allocateFile(
				demangler.getMIMEType().getExtension());

		// translate the filename to a URIContent
		URIContent outputURI = new URIContent(new File(filename).toURI());

		// write file
		demangler.mangle(table, outputURI.getURI());

		// create list
		List<URIContent> uriList = new ArrayList<URIContent>();
		uriList.add(outputURI);

		return new MIMEURIPortObject[] { new MIMEURIPortObject(uriList,
				demangler.getMIMEType()) };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		demangler = null;
		availableMangler = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		if (inSpecs[0] instanceof DataTableSpec) {

			inputTalbeSpecification = (DataTableSpec) inSpecs[0];

			IDemanglerRegistry demanglerRegistry = (IDemanglerRegistry) PlatformUI
					.getWorkbench().getService(IDemanglerRegistry.class);
			if (demanglerRegistry == null)
				throw new InvalidSettingsException(
						"Could not find IDemanglerRegistry to find Demangler.");

			availableMangler = demanglerRegistry
					.getMangler(inputTalbeSpecification);

			if (availableMangler == null || availableMangler.size() == 0) {
				throw new InvalidSettingsException(
						"no IDemangler found for the given table configuration. "
								+ "Please register one before transforming the a file with "
								+ "this MIMEType to a KNIME table.");
			}

			if (demangler == null) {
				demangler = availableMangler.get(0);
			}

			return new MIMEURIPortObjectSpec[] { new MIMEURIPortObjectSpec(
					demangler.getMIMEType()) };
		} else {
			throw new InvalidSettingsException("Cannot handle non-table input");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		settings.addString(SELECTED_DEMANGLER_SETTINGNAME, demangler.getClass()
				.getName());
		String[] manglers = new String[availableMangler.size()];
		int i = 0;
		for (IDemangler mangler : availableMangler) {
			manglers[i++] = mangler.getClass().getName();
		}

		settings.addStringArray(AVAILABLE_MIMETYPE_SETTINGNAME, manglers);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		String manglerClassName = settings.getString(
				SELECTED_DEMANGLER_SETTINGNAME, "");

		IDemanglerRegistry demanglerRegistry = (IDemanglerRegistry) PlatformUI
				.getWorkbench().getService(IDemanglerRegistry.class);
		if (demanglerRegistry == null)
			throw new InvalidSettingsException(
					"Could not find IDemanglerRegistry to find Demangler.");

		List<IDemangler> matchingManglers = demanglerRegistry
				.getMangler(inputTalbeSpecification);

		demangler = null;
		for (IDemangler mangler : matchingManglers) {
			if (manglerClassName.equals(mangler.getClass().getName())) {
				demangler = mangler;
				break;
			}
		}

		if (demangler == null) {
			throw new InvalidSettingsException(
					"Could not find an implementation for the previously selected mangler: "
							+ manglerClassName);
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
