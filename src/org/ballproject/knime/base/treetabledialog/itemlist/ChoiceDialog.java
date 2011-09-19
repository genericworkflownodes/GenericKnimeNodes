package org.ballproject.knime.base.treetabledialog.itemlist;

import java.awt.GridBagConstraints;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.ballproject.knime.base.treetabledialog.UIHelper;

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
