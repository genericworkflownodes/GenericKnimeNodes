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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.FileNotFoundException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.parameter.Parameter;

public class ParameterDialog extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 8098990326681120709L;
	private JXTreeTable table;
	private JTextPane help;
	// private JButton toggle;
	private ParameterDialogModel model;

	private static Font MAND_FONT = new Font("Dialog", Font.BOLD, 12);
	private static Font OPT_FONT = new Font("Dialog", Font.ITALIC, 12);

	public ParameterDialog(INodeConfiguration config)
			throws FileNotFoundException, Exception {
		this.setLayout(new GridBagLayout());

		model = new ParameterDialogModel(config);

		table = new JXTreeTable(model);
		table.setMinimumSize(new Dimension(1000, 500));
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumn(1).setCellEditor(model.getCellEditor());
		table.getSelectionModel().addListSelectionListener(this);

		table.setHighlighters(new Highlighter() {

			@Override
			public void addChangeListener(ChangeListener arg0) {
			}

			@Override
			public ChangeListener[] getChangeListeners() {
				return null;
			}

			@Override
			public Component highlight(Component comp, ComponentAdapter adapter) {
				boolean optional = true;
				boolean advanced = false;
				TreePath path = table.getPathForRow(adapter.row);

				if (path != null && path.getLastPathComponent() != null) {
					// @SuppressWarnings("unchecked")
					// Node<Parameter<?>> node = (Node<Parameter<?>>)
					// path.getLastPathComponent();
					ParameterNode node = (ParameterNode) path
							.getLastPathComponent();
					if (node.getPayload() != null) {
						optional = node.getPayload().isOptional();
						advanced = node.getPayload().isAdvanced();
					}
				}
				if (!optional) {
					comp.setForeground(Color.blue);
					comp.setFont(MAND_FONT);
					return comp;
				} else {
					comp.setFont(OPT_FONT);
					if (advanced) {
						comp.setForeground(Color.GRAY);
					}
				}
				return comp;
			}

			@Override
			public void removeChangeListener(ChangeListener arg0) {
			}

		});

		// expand full tree by default
		table.expandAll();

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		size.width /= 1.5;
		size.height /= 1.5;
		this.setMinimumSize(size);
		this.setPreferredSize(size);

		this.add(new JScrollPane(table), new GridBagConstraints(0, 0, 1, 1,
				1.0, 2.0f, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		help = new JTextPane();
		this.add(new JScrollPane(help), new GridBagConstraints(0, 1, 1, 1, 1.0,
				1.0f, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		/*
		 * toggle = new JButton("Toggle adv. Parameters");
		 * toggle.addActionListener(new ActionListener(){
		 * 
		 * @Override public void actionPerformed(ActionEvent arg0) {
		 * model.toggleAdvanced(); }} ); UIHelper.addComponent(this, toggle, 0,
		 * 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,1.0f);
		 */
	}

	public ParameterDialogModel getModel() {
		return model;
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting()) {
			return;
		}
		if (evt.getSource() == table.getSelectionModel()) {
			int row = table.getSelectedRow();
			Object val = table.getModel().getValueAt(row, 1);
			if (val instanceof Parameter<?>) {
				Parameter<?> param = (Parameter<?>) val;

				StyledDocument doc = (StyledDocument) help.getDocument();
				Style style = doc.addStyle("StyleName", null);
				StyleConstants.setFontFamily(style, "SansSerif");

				try {
					doc.remove(0, doc.getLength());
					doc.insertString(0, param.getDescription(), style);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
