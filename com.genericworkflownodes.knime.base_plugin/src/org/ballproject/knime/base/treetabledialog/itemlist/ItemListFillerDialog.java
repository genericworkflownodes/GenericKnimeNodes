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

package org.ballproject.knime.base.treetabledialog.itemlist;

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
import javax.swing.SwingUtilities;

import org.ballproject.knime.base.treetabledialog.UIHelper;

public class ItemListFillerDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -3296308108315788626L;
	private JList list;
	private JButton addButton;
	private JButton delButton;
	private JButton okButton;

	private final ItemListFillerDialogModel model;

	private String[] choices;

	public ItemListFillerDialog(ItemListFillerDialogModel mdl) {
		super();

		this.setTitle("List editor");

		if (mdl.hasRestrictedValues()) {
			choices = mdl.getRestrictedValues();
		}

		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		this.model = mdl;

		list = new JList(model);
		list.setFixedCellWidth(200);
		JScrollPane listScrollPane = new JScrollPane(list);

		UIHelper.addComponent(pane, listScrollPane, 0, 0, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 2, 2);

		addButton = new JButton("Add");
		addButton.addActionListener(this);

		delButton = new JButton("Del");
		delButton.addActionListener(this);

		okButton = new JButton("OK");
		okButton.addActionListener(this);

		makeButtonWidthEqual(addButton, delButton, okButton);

		Box box = Box.createVerticalBox();
		box.add(addButton);
		box.add(delButton);
		box.add(okButton);
		box.add(Box.createVerticalGlue());

		UIHelper.addComponent(pane, box, 1, 0, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, 0, 0);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(300, 200);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Object source = ev.getSource();
		if (source == addButton) {
			if (model.hasRestrictedValues()) {
				Object choice = ChoiceDialog.showDialog("Please select ...",
						choices);
				String text = choice.toString();
				model.addItem(text);
			} else {
				String text = JOptionPane.showInputDialog("Enter value ("
						+ model.getValidatorName() + "):");

				boolean valid = model.addItem(text);

				if (!valid) {
					JOptionPane.showMessageDialog(
							null,
							"entered value is not a valid "
									+ model.getValidatorName() + ". reason: "
									+ model.getValidatorReason());
				}
			}
		}
		if (source == delButton) {
			model.removeItems(list.getSelectedIndices());
		}
		if (source == okButton) {
			dispose();
		}
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

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ItemListFillerDialogModel mdl = new ItemListFillerDialogModel(
						new String[] { "A", "B", "D" });
				// mdl.setValidator(new DoubleValidator());
				mdl.restrictValues("A", "B", "C", "D");
				mdl.setSetLike(true);

				ItemListFillerDialog sd = new ItemListFillerDialog(mdl);
				sd.setVisible(true);
			}
		});
	}

}
