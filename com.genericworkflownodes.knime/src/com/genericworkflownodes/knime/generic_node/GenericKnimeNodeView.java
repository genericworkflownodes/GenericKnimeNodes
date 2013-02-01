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

package com.genericworkflownodes.knime.generic_node;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "GenericKnimeNode" Node.
 * 
 * 
 * @author
 */
public class GenericKnimeNodeView extends NodeView<GenericKnimeNodeModel> {

	/**
	 * Creates a new view.
	 * 
	 * @param nodeModel
	 *            The model (class: {@link GenericKnimeNodeModel})
	 */
	protected GenericKnimeNodeView(final GenericKnimeNodeModel nodeModel) {
		super(nodeModel);

		JTabbedPane tabs = new JTabbedPane();

		String stdout = "", stderr = "";

		if (nodeModel.executor != null) {
			stdout = nodeModel.executor.getToolOutput() != null ? nodeModel.executor
					.getToolOutput() : "";
			stderr = nodeModel.executor.getToolErrorOutput() != null ? nodeModel.executor
					.getToolErrorOutput() : "";
		}

		tabs.add("stdout", createScrollableOutputArea(stdout));
		tabs.add("stderr", createScrollableOutputArea(stderr));

		// we generally prefer stderr (if available), since it should be more
		// important
		if (nodeModel.executor.getToolErrorOutput().length() > 0) {
			tabs.setSelectedIndex(1);
		}

		setComponent(tabs);
	}

	private JScrollPane createScrollableOutputArea(final String content) {
		JTextArea text = new JTextArea(content, 40, 80);
		text.setFont(new Font("Monospaced", Font.BOLD, 12));
		text.setEditable(false);
		if (content.length() == 0) {
			text.setEnabled(false);
		}
		JScrollPane scrollpane = new JScrollPane(text);
		return scrollpane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged() {

		// TODO retrieve the new model from your nodemodel and
		// update the view.
		GenericKnimeNodeModel nodeModel = getNodeModel();
		assert nodeModel != null;

		// be aware of a possibly not executed nodeModel! The data you retrieve
		// from your nodemodel could be null, emtpy, or invalid in any kind.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose() {

		// TODO things to do when closing the view
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen() {

		// TODO things to do when opening the view
	}

}
