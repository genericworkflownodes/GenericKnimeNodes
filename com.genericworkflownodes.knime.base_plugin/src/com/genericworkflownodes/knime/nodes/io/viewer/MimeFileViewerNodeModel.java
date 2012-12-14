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

package com.genericworkflownodes.knime.nodes.io.viewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.knime.core.data.uri.URIPortObject;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

/**
 * This is the model implementation of MimeFileViewer.
 * 
 * 
 * @author roettig
 */
public class MimeFileViewerNodeModel extends NodeModel {

	public static String NUM_LINES = "MAX_NUMBER_LINES";

	private SettingsModelInteger max_num_lines = MimeFileViewerNodeDialog
			.createIntModel();

	private String data;

	public String getContent() {
		return data;
	}

	/**
	 * Constructor for the node model.
	 */
	protected MimeFileViewerNodeModel() {
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
		max_num_lines.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		max_num_lines.loadSettingsFrom(settings);
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
		ZipFile zip = new ZipFile(new File(internDir, "loadeddata"));

		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

		int BUFFSIZE = 2048;
		byte[] BUFFER = new byte[BUFFSIZE];

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

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

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		return new PortObjectSpec[] {};
	}

	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		URIPortObject po = (URIPortObject) inObjects[0];
		File file = new File(po.getURIContents().get(0).getURI());

		int maxLines = max_num_lines.getIntValue();
		data = readFileSummary(file, maxLines);

		return new PortObject[] {};
	}

	public static String readFileSummary(File file, int maxLines)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();

		String line = "";

		int cnt = 0;

		sb.append("File path: " + file.getAbsolutePath()
				+ System.getProperty("line.separator"));
		sb.append("File size: " + file.length() + " bytes"
				+ System.getProperty("line.separator"));

		Date date = new Date(file.lastModified());
		Format formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
		String s = formatter.format(date);

		sb.append("File time: " + s + System.getProperty("line.separator"));

		sb.append(String.format(
				"File content (first %d lines):"
						+ System.getProperty("line.separator"), maxLines));

		while ((line = br.readLine()) != null) {
			sb.append(line + System.getProperty("line.separator"));
			cnt++;
			if (cnt > maxLines) {
				sb.append("######### OUTPUT TRUNCATED #########"
						+ System.getProperty("line.separator"));
				break;
			}
		}

		return sb.toString();
	}

}
