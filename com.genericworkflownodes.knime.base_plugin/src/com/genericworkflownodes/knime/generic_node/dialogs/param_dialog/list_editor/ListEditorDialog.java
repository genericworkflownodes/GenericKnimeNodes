/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.list_editor;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.genericworkflownodes.knime.generic_node.dialogs.UIHelper;
import com.genericworkflownodes.knime.parameter.ListParameter;

/**
 * ListEditor dialog for StringList and NumericalList parameters.
 * 
 * @author aiche
 */
public class ListEditorDialog extends JDialog {

	private static final int TABLE_HEIGHT = 500;
	private static final int COLUMN_WIDTH = 1000;
	/**
	 * 
	 */
	private static final long serialVersionUID = 9130341015527518732L;
	private ListParameter parameter;
	private final static String TITLE = "List Editor";
	private ListEditorDialogModel model;
	private JTable table;
	private ListCellEditor cellEditor;

	/**
	 * Initialize the editor.
	 */
	public ListEditorDialog(ListParameter p) {
		super();
		setTitle(TITLE);
		init(p);
	}

	private void init(ListParameter p) {
		parameter = p;

		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());

		model = new ListEditorDialogModel(parameter);
		table = new JTable(model);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

		cellEditor = new ListCellEditor(p);
		table.setCellEditor(cellEditor);
		table.getColumnModel().getColumn(0).setCellEditor(cellEditor);

		table.setMinimumSize(new Dimension(COLUMN_WIDTH, TABLE_HEIGHT));

		// adapt width of column
		adjustColumnSize();

		// remove the tableheader
		table.setTableHeader(null);

		JScrollPane listScrollPane = new JScrollPane(table);

		UIHelper.addComponent(pane, listScrollPane, 0, 0, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2, 2);

		addButtons(pane);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		UIHelper.centerDialog(this);
		pack();
	}

	private void adjustColumnSize() {
		table.doLayout();
	}

	private void addButtons(Container pane) {
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int addedIndex = model.addValue();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						table.setRowSelectionInterval(addedIndex, addedIndex);
					}
				});
			}
		});

		JButton removeButton = new JButton("Delete");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.transferToParameter();
				ListEditorDialog.this.dispose();
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListEditorDialog.this.dispose();
			}
		});

		makeButtonWidthEqual(addButton, removeButton, okButton, cancelButton);

		Box box = Box.createVerticalBox();
		box.add(addButton);
		box.add(removeButton);
		box.add(okButton);
		box.add(cancelButton);
		box.add(Box.createVerticalGlue());

		UIHelper.addComponent(pane, box, 1, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 0, 0);
	}

	/**
	 * Customize buttons size to the maximal value.
	 * 
	 * @param buttons
	 *            The buttons to customize.
	 */
	private static void makeButtonWidthEqual(JButton... buttons) {
		int maxWidth = Integer.MIN_VALUE;

		for (JButton button : buttons) {
			maxWidth = Math.max(maxWidth, button.getPreferredSize().width);
		}

		for (JButton button : buttons) {
			Dimension size = new Dimension(maxWidth,
					button.getPreferredSize().height);
			button.setPreferredSize(size);
			button.setMinimumSize(size);
			button.setMaximumSize(size);
		}
	}

}
