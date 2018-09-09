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

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.core.node.NodeView;

import com.genericworkflownodes.knime.nodes.io.listimporter.ListMimeFileImporterNodeModel;

/**
 * <code>NodeView</code> for the "MimeFileImporter" Node.
 *
 * @author roettig
 */
final class MimeFileImporterNodeView extends
        NodeView<MimeFileImporterNodeModel> {

    /**
     * Creates a new view.
     *
     * @param nodeModel
     *            The model (class: {@link ListMimeFileImporterNodeModel})
     */
    protected MimeFileImporterNodeView(final MimeFileImporterNodeModel nodeModel) {
        super(nodeModel);

        final JTextArea text = new JTextArea(new String(nodeModel.getContent()), 40, 80);
        text.setFont(new Font("Monospaced", Font.BOLD, 12));
        setComponent(new JScrollPane(text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        MimeFileImporterNodeModel nodeModel = getNodeModel();
        assert nodeModel != null;
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
