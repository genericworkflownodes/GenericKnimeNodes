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
package com.genericworkflownodes.knime.nodes.io.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.activation.MimeType;

import org.knime.core.data.DataTableSpec;
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

import com.genericworkflownodes.util.Helper;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of MimeFileImporter.
 * 
 * @author roettig
 */
public class MimeFileImporterNodeModel extends NodeModel {

	/**
	 * Config name.
	 */
	static final String CFG_FILENAME = "FILENAME";

	/**
	 * SettingsModel to the filename.
	 */
	private SettingsModelString m_filename = MimeFileImporterNodeDialog
			.createFileChooserModel();

	/**
	 * Data member.
	 */
	private String data;

	/**
	 * Getter for data member.
	 * 
	 * @return The data member.
	 */
	public String getContent() {
		return data;
	}

	/**
	 * Constructor for the node model.
	 */
	protected MimeFileImporterNodeModel() {
		super(new PortType[] {}, new PortType[] { new PortType(
				URIPortObject.class) });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
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
		String filename = settings.getString(CFG_FILENAME);
		File in = new File(filename);
		if (!in.canRead()) {
			throw new InvalidSettingsException("input file cannot be read: "
					+ filename);
		}

		if (MIMETypeHelper.getMIMEtype(filename) == null) {
			throw new InvalidSettingsException(
					"file of unknown MIMEtype selected: " + filename);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		ZipFile zip = new ZipFile(new File(internDir, "loadeddata"));

		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

		int BUFFSIZE = 2048;
		byte[] BUFFER = new byte[BUFFSIZE];

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();

			if (entry.getName().equals("rawdata.bin")) {
				int size = (int) entry.getSize();
				byte[] data = new byte[size];
				InputStream in = zip.getInputStream(entry);
				int len;
				int totlen = 0;
				while ((len = in.read(BUFFER, 0, BUFFSIZE)) >= 0) {
					System.arraycopy(BUFFER, 0, data, totlen, len);
					totlen += len;
				}
				this.data = new String(data);
			}
		}
		zip.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				new File(internDir, "loadeddata")));
		ZipEntry entry = new ZipEntry("rawdata.bin");
		out.putNextEntry(entry);
		out.write(data.getBytes());
		out.close();
	}

	/**
	 * {@link MimeType} of the loaded file.
	 */
	private String mt = null;

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		mt = MIMETypeHelper.getMIMEtype(m_filename.getStringValue());

		if (mt == null) {
			return new DataTableSpec[] { null };
		}

		return new PortObjectSpec[] { new URIPortObjectSpec(
				MIMETypeHelper.getMIMEtypeExtension(m_filename.getStringValue())) };
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		List<URIContent> uris = new ArrayList<URIContent>();

		File file = new File(m_filename.getStringValue());

		if (!file.exists()) {
			throw new Exception("file does not exist: "
					+ file.getAbsolutePath());
		}

		uris.add(new URIContent(file.toURI(), MIMETypeHelper
				.getMIMEtypeExtension(file.getAbsolutePath())));

		data = Helper.readFileSummary(file, 50);

		return new PortObject[] { new URIPortObject(uris) };
	}
}
