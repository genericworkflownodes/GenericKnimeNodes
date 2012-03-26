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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ChoiceDialog extends JDialog implements ActionListener
{
	private JList   list;
	private JButton addButton;
	private Object[] values;
	
	public static Object showDialog(String title, Object[] values)
	{
		ChoiceDialog diag = new ChoiceDialog(values);
		diag.setVisible(true);
		return diag.getSelection();
	}
	
	public ChoiceDialog(Object[] values)
	{
		this.values = values;
		init();
	}
	
	private void init()
	{
		list = new JList(values);
		list.setFixedCellWidth(200);
		JScrollPane listScrollPane = new JScrollPane(list);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		addButton = new JButton("Pick");
		addButton.addActionListener(this);
				
		Box box = Box.createVerticalBox();
		box.add(listScrollPane);
		box.add(addButton);
		box.add(Box.createVerticalGlue());
		
		this.setContentPane(box);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(300, 200);
		pack();

	}

	private Object selection;
	
	public Object getSelection()
	{
		return selection;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		selection = list.getSelectedValue();
		dispose();
	}
}
