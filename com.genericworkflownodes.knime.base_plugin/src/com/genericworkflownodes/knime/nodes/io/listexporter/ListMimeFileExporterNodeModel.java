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

package com.genericworkflownodes.knime.nodes.io.listexporter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ballproject.knime.base.util.Helper;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

/**
 * This is the model implementation of ListMimeFileImporter.
 * 
 * 
 * @author roettig
 */
public class ListMimeFileExporterNodeModel extends NodeModel {

	static final String CFG_FILENAME = "FILENAME";

	private SettingsModelString m_filename = ListMimeFileExporterNodeDialog
			.createFileChooserModel();

	/**
	 * Constructor for the node model.
	 */
	protected ListMimeFileExporterNodeModel() {
		super(new PortType[] { new PortType(URIPortObject.class) },
				new PortType[] {});
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
		m_filename.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_filename.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		m_filename.validateSettings(settings);
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

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		if (!(inSpecs[0] instanceof URIPortObjectSpec)) {
			throw new InvalidSettingsException(
					"no MIMEURIPortObject compatible port object at port 0");
		}
		return new PortObjectSpec[] {};
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		URIPortObject obj = (URIPortObject) inObjects[0];
		List<URIContent> uris = obj.getURIContents();

		if (uris.size() == 0) {
			throw new Exception(
					"there were no URIs in the supplied MIMEURIPortObject at port 0");
		}

		int idx = 1;
		for (URIContent uri : uris) {
			File in = new File(uri.getURI());
			if (!in.canWrite()) {
				throw new Exception("cannot read file to export: "
						+ in.getAbsolutePath());
			}

			String outfilename = insertIndex(m_filename.getStringValue(), obj
					.getSpec().getFileExtensions().get(0), idx++);
			File out = new File(outfilename);

			// if(!out.canWrite())
			// throw new
			// Exception("cannot write to file: "+out.getAbsolutePath());

			Helper.copyFile(in, out);
		}

		return new PortObject[] {};
	}

	private static String insertIndex(String filename, String extension, int idx) {
		if (filename.equals("") || filename.length() == 0) {
			return filename;
		}

		String filename_ = filename.toLowerCase();
		String ext = extension.toLowerCase();

		int idx1 = filename_.lastIndexOf(ext);

		if (idx == -1) {
			return filename;
		}

		String s1 = filename.substring(0, idx1);
		return s1 + idx + "." + extension;
	}
}
