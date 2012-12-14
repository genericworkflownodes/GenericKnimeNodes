/**
 * Copyright (c) 2012, Marc Röttig.
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
package com.genericworkflownodes.knime.nodes.io.listimporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.genericworkflownodes.knime.mime.IMIMEtypeRegistry;

/**
 * This is the model implementation of ListMimeFileImporter.
 * 
 * @author roettig
 */
public class ListMimeFileImporterNodeModel extends NodeModel {

	/**
	 * ID for the filename configuration.
	 */
	static final String CFG_FILENAME = "FILENAME";

	/**
	 * Model containing the file names.
	 */
	private SettingsModelStringArray m_filenames = ListMimeFileImporterNodeDialog
			.createFileChooserModel();

	/**
	 * Constructor for the node model.
	 */
	protected ListMimeFileImporterNodeModel() {
		super(new PortType[] {}, new PortType[] { new PortType(
				URIPortObject.class) });
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_filenames.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_filenames.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_filenames.validateSettings(settings);
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

	/**
	 * The {@link MIMEType} of the imported files.
	 */
	private String mt = null;

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		String[] filenames = this.m_filenames.getStringArrayValue();

		if (filenames == null || filenames.length == 0) {
			return new PortObjectSpec[] { null };
		}

		List<String> mts = new ArrayList<String>();

		IMIMEtypeRegistry registry = (IMIMEtypeRegistry) PlatformUI
				.getWorkbench().getService(IMIMEtypeRegistry.class);
		if (registry == null)
			throw new InvalidSettingsException(
					"Could not find IMIMETypeRegistry to resolve MIMETypes.");

		for (String filename : filenames) {
			mt = registry.getMIMEtype(filename);
			if (mt == null) {
				throw new InvalidSettingsException(
						"could not resolve MIMEType of file " + filename);
			}
			mts.add(mt);
		}

		for (String mimeType : mts) {
			if (!mimeType.equals(mt)) {
				throw new InvalidSettingsException(
						"Files with mixed MIMEType loaded");
			}
		}

		return new PortObjectSpec[] { new URIPortObjectSpec(mt) };
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		String[] filenames = this.m_filenames.getStringArrayValue();

		List<URIContent> uris = new ArrayList<URIContent>();
		for (String filename : filenames) {
			File in = new File(filename);

			if (!in.canRead()) {
				throw new Exception("cannot read from input file: "
						+ in.getAbsolutePath());
			}

			uris.add(new URIContent(new File(filename).toURI(),
					getExtension(filename)));
		}

		return new PortObject[] { new URIPortObject(uris) };
	}

	private String getExtension(String path) {
		if (path.lastIndexOf('.') == -1) {
			return "";
		} else {
			return path.substring(path.lastIndexOf('.') + 1);
		}
	}
}
