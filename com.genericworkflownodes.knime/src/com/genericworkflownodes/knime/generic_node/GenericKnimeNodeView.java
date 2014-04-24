/**
 * Copyright (c) 2011, Marc Röttig.
 * Copyright (c) 2014, Stephan Aiche.
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
 * NodeView for the GenericKnimeNode.
 * 
 * @author roettig, aiche
 */
public class GenericKnimeNodeView extends NodeView<GenericKnimeNodeModel> {

    private static final int TEXT_AREA_FONT_SIZE = 12;
    private static final int TEXT_AREA_COLUMNS = 80;
    private static final int TEXT_AREA_ROWS = 40;

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

        if (nodeModel.m_executor != null) {
            stdout = nodeModel.m_executor.getToolOutput() != null ? nodeModel.m_executor
                    .getToolOutput() : "";
            stderr = nodeModel.m_executor.getToolErrorOutput() != null ? nodeModel.m_executor
                    .getToolErrorOutput() : "";
        }

        tabs.add("stdout", createScrollableOutputArea(stdout));
        tabs.add("stderr", createScrollableOutputArea(stderr));

        // we generally prefer stderr (if available), since it should be more
        // important
        if (nodeModel.m_executor != null
                && nodeModel.m_executor.getToolErrorOutput().length() > 0) {
            tabs.setSelectedIndex(1);
        }

        setComponent(tabs);
    }

    private JScrollPane createScrollableOutputArea(final String content) {
        JTextArea text = new JTextArea(content, TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);
        text.setFont(new Font("Monospaced", Font.BOLD, TEXT_AREA_FONT_SIZE));
        text.setEditable(false);
        if (content.length() == 0) {
            text.setEnabled(false);
        }
        return new JScrollPane(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        GenericKnimeNodeModel nodeModel = getNodeModel();
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
