/**
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.param_tree.ParameterNode;
import com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.ui_helper.TableColumnAdjuster;
import com.genericworkflownodes.knime.parameter.Parameter;

/**
 * 
 * @author roettig, aiche, bkahlert
 */
public class ParameterDialog extends JPanel {
	private static final long serialVersionUID = 8098990326681120709L;
	private JXTreeTable table;
	private JTextPane help;
	private JCheckBox toggle;
	private ParameterDialogModel model;

	private static Font MAND_FONT = new Font("Dialog", Font.BOLD, 12);
	private static Font OPT_FONT = new Font("Dialog", Font.ITALIC, 12);

	public ParameterDialog(INodeConfiguration config)
			throws FileNotFoundException, Exception {
		setLayout(new GridBagLayout());

		// create the data model for the table
		createModel(config);

		// create the JXTreeTable
		createTable();

		// adjust size of columns initially to fit the screen
		updateTableView();

		// create the sub controls (documentation and toggle for advanced)
		createHelpPane();
		createShowAdvancedToggle();

		// finally add controls to panel
		addControlsToPanel();
	}

	private void createModel(INodeConfiguration config)
			throws FileNotFoundException, Exception {
		model = new ParameterDialogModel(config);
		model.addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				// defer column adjustment till all the recreation events are
				// handled
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						TableColumnAdjuster tca = new TableColumnAdjuster(table);
						tca.adjustColumns();
					}
				});
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
			}

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
			}
		});
	}

	private void createTable() {
		table = new JXTreeTable(model);
		table.setMinimumSize(new Dimension(1000, 500));
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.getColumn(1).setCellEditor(model.getCellEditor());
		// under some circumstances the cellEditor gets lost, therefore we
		// register a default for parameter objects
		table.setDefaultEditor(Parameter.class, model.getCellEditor());

		addHighlighter();
		addSelectionListener();
	}

	private void addSelectionListener() {
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent event) {
						if (event.getValueIsAdjusting()) {
							return;
						}
						if (event.getSource() == table.getSelectionModel()) {
							int row = table.getSelectedRow();
							Object val = table.getModel().getValueAt(row, 3);
							if (val != null && val instanceof String) {
								updateDocumentationSection((String) val);
							}
						}
					}
				});
	}

	private void addHighlighter() {
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
	}

	private void addControlsToPanel() {
		add(new JScrollPane(table), new GridBagConstraints(0, 0, 1, 1, 1.0,
				.79f, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		add(new JScrollPane(help), new GridBagConstraints(0, 1, 1, 2, 1.0, .2f,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						2, 2, 2, 2), 0, 0));
		add(toggle, new GridBagConstraints(0, 3, 1, 1, 1.0, .01f,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL,
				new Insets(2, 2, 2, 2), 0, 0));
	}

	private void createHelpPane() {
		help = new JTextPane();
		help.setPreferredSize(new Dimension(table.getWidth(), 50));
	}

	private void createShowAdvancedToggle() {
		toggle = new JCheckBox("Show advanced parameter");
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setShowAdvanced(toggle.isSelected());
				model.refresh();
				updateTableView();
			}
		});
	}

	private void updateTableView() {
		// expand full tree by default
		table.expandAll();
		TableColumnAdjuster tca = new TableColumnAdjuster(table);
		tca.adjustColumns();
	}

	private void updateDocumentationSection(String description) {
		StyledDocument doc = (StyledDocument) help.getDocument();
		Style style = doc.addStyle("StyleName", null);
		StyleConstants.setFontFamily(style, "SansSerif");

		try {
			doc.remove(0, doc.getLength());
			doc.insertString(0, description, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
