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

package com.genericworkflownodes.knime.generic_node.dialogs.param_dialog.itemlist;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.genericworkflownodes.knime.generic_node.dialogs.UIHelper;

public class ItemListFillerDialog extends JDialog {
	private static final long serialVersionUID = -3296308108315788626L;

	private final ItemListFillerDialogModel model;

	private String[] choices;

	public ItemListFillerDialog(ItemListFillerDialogModel mdl) {
		super();

		this.setTitle("List Editor");

		if (mdl.hasRestrictedValues()) {
			this.choices = mdl.getRestrictedValues();
		}

		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		this.model = mdl;

		final JList list = new JList(model);
		JScrollPane listScrollPane = new JScrollPane(list);

		UIHelper.addComponent(pane, listScrollPane, 0, 0, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2, 2);

		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (model.hasRestrictedValues()) {
					Object choice = ChoiceDialog.showDialog(
							"Please select ...", choices);
					String text = choice.toString();
					if (text != null) {
						model.addItem(text);
					}
				} else {
					String text = JOptionPane.showInputDialog("Enter value ("
							+ model.getValidatorName() + "):");

					if (text != null) {
						boolean valid = model.addItem(text);

						if (!valid) {
							JOptionPane.showMessageDialog(
									null,
									"entered value is not a valid "
											+ model.getValidatorName()
											+ ". reason: "
											+ model.getValidatorReason());
						}
					}
				}
			}
		});

		final JButton modifyButton = new JButton("Modify");
		modifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (list.getSelectedValue() == null)
					return;

				if (model.hasRestrictedValues()) {
					Object choice = ChoiceDialog.showDialog(
							"Please select ...", choices);
					String text = choice.toString();
					if (text != null) {
						model.replaceItem(list.getSelectedIndex(), text);
					}
				} else {
					String text = JOptionPane.showInputDialog(
							"Enter new value (" + model.getValidatorName()
									+ "):", list.getSelectedValue());

					if (text != null) {
						boolean valid = model.replaceItem(
								list.getSelectedIndex(), text);

						if (!valid) {
							JOptionPane.showMessageDialog(
									null,
									"entered value is not a valid "
											+ model.getValidatorName()
											+ ". reason: "
											+ model.getValidatorReason());
						}
					}
				}
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				modifyButton.setEnabled(list.getSelectedValue() != null);
			}
		});
		list.setSelectedIndex(0);

		JButton removeButton = new JButton("Delete");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.removeItems(list.getSelectedIndices());
			}
		});

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ItemListFillerDialog.this.dispose();
			}
		});

		makeButtonWidthEqual(addButton, modifyButton, removeButton, okButton);

		Box box = Box.createVerticalBox();
		box.add(addButton);
		box.add(modifyButton);
		box.add(removeButton);
		box.add(okButton);
		box.add(Box.createVerticalGlue());

		UIHelper.addComponent(pane, box, 1, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 0, 0);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.pack();
	}

	public static void makeButtonWidthEqual(JButton... buttons) {
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
