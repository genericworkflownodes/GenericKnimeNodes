/**
 * Copyright (c) 2011-2013, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.outputfile;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.core.node.NodeView;

import com.genericworkflownodes.knime.nodes.io.listimporter.ListMimeFileImporterNodeModel;

/**
 * <code>NodeView</code> for the "MimeFileExporter" Node.
 * 
 * 
 * @author roettig
 */
public class OutputFileNodeView extends NodeView<OutputFileNodeModel> {

	/**
	 * The {@link JTextArea} where the file content preview is stored.
	 */
	private final JTextArea m_text;

	/**
	 * Creates a new view.
	 * 
	 * @param nodeModel
	 *            The model (class: {@link ListMimeFileImporterNodeModel})
	 */
	protected OutputFileNodeView(final OutputFileNodeModel nodeModel) {
		super(nodeModel);
		m_text = new JTextArea("", 40, 80);
		JScrollPane scrollpane = new JScrollPane(m_text);
		m_text.setFont(new Font("Monospaced", Font.BOLD, 12));
		m_text.setText(getNodeModel().getContent());
		setComponent(scrollpane);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged() {
		assert getNodeModel() != null;
		m_text.setText(getNodeModel().getContent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen() {
	}
}
